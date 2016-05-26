package com.github.czyzby.uedi.impl;

import java.util.Map;
import java.util.Set;

import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.cliffc.high_scale_lib.NonBlockingHashSet;

import com.github.czyzby.uedi.scanner.ClassScanner;

/** Uses high-scale-lib non-blocking concurrent collections to ensure thread-safety in multithreaded environment.
 *
 * @author MJ
 * @see NonBlockingHashMap
 * @see NonBlockingHashSet */
public class ParameterAwareConcurrentContext extends ParameterAwareContext {
    public ParameterAwareConcurrentContext(final ClassScanner classScanner) {
        super(classScanner);
    }

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new NonBlockingHashMap<>();
    }

    @Override
    protected <V> Set<V> createSet() {
        return new NonBlockingHashSet<>();
    }
}
