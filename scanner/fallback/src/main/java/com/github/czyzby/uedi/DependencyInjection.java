package com.github.czyzby.uedi;

import com.github.czyzby.uedi.impl.ConcurrentContext;
import com.github.czyzby.uedi.impl.DefaultContext;
import com.github.czyzby.uedi.scanner.impl.FallbackClassScanner;

/** Allows to easily construct {@link Context} instances.
 *
 * @author MJ */
public class DependencyInjection {
    private DependencyInjection() {
    }

    /** @return a new instance of non-thread-safe {@link Context} using {@link FallbackClassScanner}.
     * @see #newThreadSafeContext()
     * @see Context#scan(Class) */
    public static Context newContext() {
        return new DefaultContext(new FallbackClassScanner());
    }

    /** @return a new instance of thread-safe {@link Context} using {@link FallbackClassScanner}.
     * @see #newContext()
     * @see Context#scan(Class) */
    public static Context newThreadSafeContext() {
        return new ConcurrentContext(new FallbackClassScanner());
    }
}
