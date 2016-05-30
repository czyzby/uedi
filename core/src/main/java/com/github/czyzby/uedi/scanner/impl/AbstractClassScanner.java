package com.github.czyzby.uedi.scanner.impl;

import java.lang.reflect.Modifier;

import com.github.czyzby.uedi.scanner.ClassScanner;

/** Provides utilities for {@link ClassScanner} implementations.
 *
 * @author MJ */
public abstract class AbstractClassScanner implements ClassScanner {
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
