package com.github.czyzby.uedi.reflection.impl;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import com.github.czyzby.uedi.reflection.ReflectionPool;
import com.github.czyzby.uedi.stereotype.Factory;
import com.github.czyzby.uedi.stereotype.Property;
import com.github.czyzby.uedi.stereotype.Provider;
import com.github.czyzby.uedi.stereotype.Singleton;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/** Generates {@link com.github.czyzby.uedi.reflection.ReflectionPool} instance, aware of all GWT classes implementing
 * UEDI interfaces.
 *
 * @author MJ */
public class ReflectionPoolGenerator extends Generator {
    private static final String UEDI_ROOT_PROPERTY = "uedi.root";
    private static final String GENERATED_CLASS_PREFIX = "Generated";

    @Override
    @SuppressWarnings("resource")
    public String generate(final TreeLogger logger, final GeneratorContext context, final String typeName)
            throws UnableToCompleteException {
        final TypeOracle oracle = context.getTypeOracle();
        assert oracle != null;
        final JClassType type = oracle.findType(typeName);
        if (type == null || type.isInterface() == null) {
            logger.log(Type.ERROR, "UEDI: Invalid type: " + typeName + ". Aborting.");
            throw new UnableToCompleteException();
        }

        final String packageName = type.getPackage().getName();
        final String generatedClassName = GENERATED_CLASS_PREFIX + type.getSimpleSourceName();
        final ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName,
                generatedClassName);
        composer.addImplementedInterface(ReflectionPool.class.getCanonicalName());

        final PrintWriter printWriter = context.tryCreate(logger, packageName, generatedClassName);
        if (printWriter == null) {
            return getQualifiedGeneratedClassName(packageName, generatedClassName);
        }

        final SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);
        final Set<JType> reflectedClasses = findReflectedClasses(context, oracle, logger);
        appendClassBody(sourceWriter, reflectedClasses, logger);

        sourceWriter.commit(logger);
        return getQualifiedGeneratedClassName(packageName, generatedClassName);
    }

    private static String getQualifiedGeneratedClassName(final String packageName, final String generatedClassName) {
        return packageName + "." + generatedClassName;
    }

    private static void appendClassBody(final SourceWriter sourceWriter, final Set<JType> reflectedClasses,
            final TreeLogger logger) {
        sourceWriter.println("private static final Class<?>[] POOL = new Class<?>[] { ");
        logger.log(Type.INFO, "UEDI: Found the following UEDI components:");
        for (final JType reflectedClass : reflectedClasses) {
            logger.log(Type.INFO, reflectedClass.getQualifiedSourceName());
            sourceWriter.print(reflectedClass.getQualifiedSourceName() + ".class, ");
        }
        sourceWriter.println(" };");
        sourceWriter.println("@Override public Class<?>[] getReflectedClasses() { return POOL; } ");
    }

    protected Set<JType> findReflectedClasses(final GeneratorContext context, final TypeOracle typeOracle,
            final TreeLogger logger) throws UnableToCompleteException {
        final Set<JType> types = new HashSet<JType>();
        final Set<String> uediInterfaceNames = getUediInterfaceNames();
        final Set<JClassType> uediInterfaces = new HashSet<JClassType>();
        final String rootPackage = getRootPackage(context, logger);

        for (final JPackage jPackage : typeOracle.getPackages()) {
            for (final JClassType jType : jPackage.getTypes()) {
                if (uediInterfaceNames.contains(jType.getQualifiedSourceName())) {
                    uediInterfaces.add(jType);
                } else if (jType.isClass() != null && jType.isInterface() == null && !jType.isClass().isAbstract()
                        && jType.getQualifiedSourceName().startsWith(rootPackage)) {
                    types.add(jType);
                }
            }
        }
        if (uediInterfaces.size() < uediInterfaceNames.size()) {
            logger.log(Type.ERROR, "UEDIT: Unable to find UEDI interfaces in classpath. Aborting.");
            throw new UnableToCompleteException();
        }
        return filter(types, uediInterfaces);
    }

    protected Set<String> getUediInterfaceNames() {
        final Set<String> uediInterfaceNames = new HashSet<String>();
        uediInterfaceNames.add(Provider.class.getName());
        uediInterfaceNames.add(Property.class.getName());
        uediInterfaceNames.add(Singleton.class.getName());
        uediInterfaceNames.add(Factory.class.getName());
        return uediInterfaceNames;
    }

    protected String getRootPackage(final GeneratorContext context, final TreeLogger logger) {
        try {
            final String rootPackage = context.getPropertyOracle().getConfigurationProperty(UEDI_ROOT_PROPERTY)
                    .getValues().get(0);
            if (rootPackage == null) {
                throw new RuntimeException("'uedi.root' property is not set.");
            }
            return rootPackage;
        } catch (final Exception exception) {
            logger.log(Type.WARN,
                    "UEDI: Unable to find 'uedi.root' property. Including all matching types in classpath.", exception);
            return "";
        }
    }

    protected boolean isAssignableFromAny(final Set<JClassType> uediInterfaces, final JClassType classType) {
        for (final JClassType uediInterface : uediInterfaces) {
            if (classType.isAssignableTo(uediInterface)) {
                return true;
            }
        }
        return false;
    }

    protected Set<JType> filter(final Set<JType> types, final Set<JClassType> uediInterfaces) {
        final Set<JType> filteredTypes = new HashSet<JType>();
        for (JType jType : types) {
            jType = jType.getErasedType();
            final JClassType classType = jType.isClass();
            if (isAssignableFromAny(uediInterfaces, classType)) {
                filteredTypes.add(classType);
            }
        }
        return filteredTypes;
    }
}