package com.github.czyzby.uedi.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.czyzby.uedi.scanner.ClassScanner;

/** Uses thread-safe collections to ensure correct behavior even in multi-threaded environment.
 *
 * @author MJ */
public class ConcurrentContext extends DefaultContext {
    /** @param classScanner can be null, but {@link #scan(Class)} method will not work correctly. */
    public ConcurrentContext(final ClassScanner classScanner) {
        super(classScanner);
    }

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new ConcurrentHashMap<K, V>();
    }

    @Override
    protected <V> Set<V> createSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<V, Boolean>());
    }
}
