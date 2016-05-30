package com.github.czyzby.uedi.scanner.impl;

import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import com.github.czyzby.uedi.scanner.ClassScanner;

import android.content.pm.ApplicationInfo;
import dalvik.system.DexFile;

/** Uses reflection and "native" Android API to analyze current classpath.
 *
 * @author MJ */
public class AndroidClassScanner implements ClassScanner {
    private final ApplicationInfo applicationInfo;

    /** @param applicationInfo will be used to extract data about available classes. */
    public AndroidClassScanner(final ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    @Override
    public Iterable<Class<?>> getClassesImplementing(final Class<?> root, final Class<?>... interfaces) {
        final Set<Class<?>> result = new HashSet<Class<?>>();
        final String classPath = applicationInfo.sourceDir;
        final String packageName = getPackageName(root);
        DexFile dexFile = null;
        try {
            dexFile = new DexFile(classPath);
            final Enumeration<String> classNames = dexFile.entries();
            while (classNames.hasMoreElements()) {
                final String className = classNames.nextElement();
                if (className.startsWith(packageName)) {
                    try {
                        final Class<?> testedClass = Class.forName(className);
                        if (isNotAbstract(testedClass) && isInstanceOfAny(testedClass, interfaces)) {
                            result.add(testedClass);
                        }
                    } catch (final Exception exception) {
                        exception.printStackTrace(); // Unexpected. Reported file should be present.
                    }
                }
            }
            return result;
        } catch (final Exception exception) {
            throw new RuntimeException("Unable to scan Android application.", exception);
        } finally {
            closeFile(dexFile);
        }
    }

    private static void closeFile(final DexFile dexFile) {
        try {
            if (dexFile != null) {
                dexFile.close();
            }
        } catch (final Exception exception) {
            ignore(exception);
        }
    }

    private static void ignore(final Exception exception) {
    }

    /** Package name extraction method that does not involve package query.
     *
     * @param root scanning root.
     * @return name of the package of scanning root. */
    protected String getPackageName(final Class<?> root) {
        return root.getName().substring(0, root.getName().length() - root.getSimpleName().length() - 1);
    }

    /** @param testedClass will be validated
     * @return true if the class is not abstract or anonymous and not an interface. */
    protected boolean isNotAbstract(final Class<?> testedClass) {
        return !Modifier.isAbstract(testedClass.getModifiers()) && !testedClass.isAnonymousClass()
                && !testedClass.isInterface();
    }

    /** @param testedClass will be validated.
     * @param interfaces set of interfaces to be checked against.
     * @return true if the class implements any of the passed interfaces. */
    protected boolean isInstanceOfAny(final Class<?> testedClass, final Class<?>[] interfaces) {
        for (final Class<?> possibleMatch : interfaces) {
            if (possibleMatch.isAssignableFrom(testedClass)) {
                return true;
            }
        }
        return false;
    }
}
