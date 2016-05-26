package com.github.czyzby.uedi.scanner.impl;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.github.czyzby.uedi.scanner.ClassScanner;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.InterfaceMatchProcessor;

/** Default class scanner using {@link FastClasspathScanner} to find classes implementing chosen interfaces. Does not
 * rely on on reflection: analyzes class files (processes JVM bytecode).
 *
 * @author MJ
 * @see FastClasspathScanner */
public class DefaultClassScanner implements ClassScanner {
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" }) // Raw types usage due to awkward generics API.
    public Iterable<Class<?>> getClassesImplementing(final Class<?> root, final Class<?>... interfaces) {
        final Set<Class<?>> classes = new HashSet<>();
        final FastClasspathScanner scanner = new FastClasspathScanner(root.getPackage().getName());
        for (final Class<?> implemented : interfaces) {
            scanner.matchClassesImplementing(implemented, new InterfaceMatchProcessor() {
                @Override
                public void processMatch(final Class implementingClass) {
                    if (!Modifier.isAbstract(implementingClass.getModifiers()) && !implementingClass.isInterface()) {
                        classes.add(implementingClass);
                    }
                }
            });
        }
        scanner.scan();
        return classes;
    }
}
