package com.github.czyzby.uedi.scanner.impl;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.github.czyzby.uedi.scanner.ClassScanner;
import com.jtransc.reflection.JTranscReflection;

/** Uses reflection and internal JTransc API to analyze current classpath.
 *
 * @author MJ */
public class JTranscClassScanner implements ClassScanner {
    @Override
    public Iterable<Class<?>> getClassesImplementing(final Class<?> root, final Class<?>... interfaces) {
        final Set<Class<?>> result = new HashSet<>();
        final String packageName = getPackageName(root);
        for (final String className : JTranscReflection.getAllClasses()) {
            if (className != null && className.startsWith(packageName)) {
                try {
                    final Class<?> testedClass = Class.forName(className);
                    if (isNotAbstract(testedClass) && isInstanceOfAny(testedClass, interfaces)) {
                        result.add(testedClass);
                    }
                } catch (final Exception exception) {
                    exception.printStackTrace(); // Should not happen. Returned class is expected to exist.
                }
            }
        }
        return result;
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