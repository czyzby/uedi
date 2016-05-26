package com.github.czyzby.uedi.scanner.impl;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.github.czyzby.uedi.scanner.ClassScanner;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

/** Default class scanner using {@link FastClasspathScanner} to find classes implementing chosen interfaces. Does not
 * rely on on reflection: analyzes class files (processes JVM bytecode).
 *
 * @author MJ
 * @see FastClasspathScanner */
public class StandardClassScanner implements ClassScanner {
    @Override
    public Iterable<Class<?>> getClassesImplementing(final Class<?> root, final Class<?>... interfaces) {
        final Set<Class<?>> classes = new HashSet<>();
        final FastClasspathScanner scanner = new FastClasspathScanner(root.getPackage().getName());
        Stream.of(interfaces).forEach(implemented -> scanner.matchClassesImplementing(implemented,
                implementingClass -> process(implementingClass, classes)));
        scanner.scan();
        return classes;
    }

    private static void process(final Class<?> implementingClass, final Set<Class<?>> classes) {
        if (!Modifier.isAbstract(implementingClass.getModifiers()) && !implementingClass.isInterface()) {
            classes.add(implementingClass);
        }
    }
}
