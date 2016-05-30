package com.github.czyzby.uedi;

import com.github.czyzby.uedi.impl.ConcurrentContext;
import com.github.czyzby.uedi.impl.DefaultContext;
import com.github.czyzby.uedi.scanner.ClassScanner;

/** Allows to easily construct {@link Context} instances.
 *
 * @author MJ */
public class DependencyInjection {
    private DependencyInjection() {
    }

    /** @param classScanner will be used to scan for component classes.
     * @return a new instance of non-thread-safe {@link Context}.
     * @see #newThreadSafeContext(ClassScanner)
     * @see Context#scan(Class) */
    public static Context newContext(final ClassScanner classScanner) {
        return new DefaultContext(classScanner);
    }

    /** @param classScanner will be used to scan for component classes.
     * @return a new instance of thread-safe {@link Context}.
     * @see #newContext(ClassScanner)
     * @see Context#scan(Class) */
    public static Context newThreadSafeContext(final ClassScanner classScanner) {
        return new ConcurrentContext(classScanner);
    }
}