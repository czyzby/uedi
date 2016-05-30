package com.github.czyzby.uedi.scanner.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.czyzby.uedi.reflection.ReflectionPool;
import com.github.czyzby.uedi.scanner.ClassScanner;
import com.google.gwt.core.client.GWT;

/** Scans classes implementing UEDI interfaces that were registered for GWT reflection.
 *
 * @author MJ */
public class GwtClassScanner implements ClassScanner {
    private static final ReflectionPool REFLECTION_POOL = GWT.create(ReflectionPool.class);

    @Override
    public Iterable<Class<?>> getClassesImplementing(final Class<?> root, final Class<?>... interfaces) {
        // Classes in the reflection pool are already filtered. Just need to check the package.
        final List<Class<?>> result = new ArrayList<Class<?>>();
        final String packageName = root.getName().substring(0,
                root.getName().length() - root.getSimpleName().length() - 1);
        for (final Class<?> type : REFLECTION_POOL.getReflectedClasses()) {
            final String className = type.getName();
            if (className != null && className.startsWith(packageName)) {
                result.add(type);
            }
        }
        return result;
    }
}