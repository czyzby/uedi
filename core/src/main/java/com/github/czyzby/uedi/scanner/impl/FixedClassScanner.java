package com.github.czyzby.uedi.scanner.impl;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.czyzby.uedi.scanner.ClassScanner;

/** Does not support automatic scanning. Instead, a fixed pool of scannable classes is provided and scanned when
 * requested. While heavily relying on reflection, this might actually be a faster solution in case of huge contexts (if
 * the components are not in separate root package, which they should be). Use when absolutely necessary.
 *
 * @author MJ */
public class FixedClassScanner implements ClassScanner {
    private final Set<Class<?>> context = new HashSet<Class<?>>();

    /** @param scannableClasses will be available for scanning. */
    public FixedClassScanner(final Class<?>... scannableClasses) {
        add(scannableClasses);
    }

    /** @param scannableClasses will be available for scanning. */
    public void add(final Class<?>... scannableClasses) {
        for (final Class<?> scannable : scannableClasses) {
            context.add(scannable);
        }
    }

    /** @param scannableClasses will be available for scanning. */
    public void add(final Collection<Class<?>> scannableClasses) {
        context.addAll(scannableClasses);
    }

    @Override
    public Iterable<Class<?>> getClassesImplementing(final Class<?> root, final Class<?>... interfaces) {
        final Set<Class<?>> implementingClasses = new HashSet<Class<?>>();
        final String rootPackage = root.getPackage().getName();
        for (final Class<?> scannableClass : context) {
            if (rootPackage.startsWith(scannableClass.getPackage().getName())
                    && !Modifier.isAbstract(scannableClass.getModifiers()) && !scannableClass.isInterface()
                    && isInstanceOfAny(scannableClass, interfaces)) {
                implementingClasses.add(scannableClass);
            }
        }
        return implementingClasses;
    }

    protected boolean isInstanceOfAny(final Class<?> scannableClass, final Class<?>[] interfaces) {
        for (final Class<?> possibleMatch : interfaces) {
            if (possibleMatch.isAssignableFrom(scannableClass)) {
                return true;
            }
        }
        return false;
    }
}
