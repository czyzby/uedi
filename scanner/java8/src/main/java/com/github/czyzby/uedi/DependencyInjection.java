package com.github.czyzby.uedi;

import com.github.czyzby.uedi.impl.ParameterAwareConcurrentContext;
import com.github.czyzby.uedi.impl.ParameterAwareContext;
import com.github.czyzby.uedi.scanner.impl.StandardClassScanner;

/** Allows to easily construct {@link Context} instances.
 *
 * @author MJ */
public class DependencyInjection {
    private DependencyInjection() {
    }

    /** @return a new instance of non-thread-safe {@link Context} using {@link StandardClassScanner}.
     * @see #newThreadSafeContext()
     * @see Context#scan(Class) */
    public static Context newContext() {
        return new ParameterAwareContext(new StandardClassScanner());
    }

    /** @return a new instance of thread-safe {@link Context} using {@link StandardClassScanner}.
     * @see #newContext()
     * @see Context#scan(Class) */
    public static Context newThreadSafeContext() {
        return new ParameterAwareConcurrentContext(new StandardClassScanner());
    }
}
