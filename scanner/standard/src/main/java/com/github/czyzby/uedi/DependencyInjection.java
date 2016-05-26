package com.github.czyzby.uedi;

import com.github.czyzby.uedi.impl.ConcurrentContext;
import com.github.czyzby.uedi.impl.DefaultContext;
import com.github.czyzby.uedi.scanner.impl.DefaultClassScanner;

/** Allows to easily construct {@link Context} instances.
 *
 * @author MJ */
public class DependencyInjection {
    private DependencyInjection() {
    }

    /** @return a new instance of non-thread-safe {@link Context} using {@link DefaultClassScanner}.
     * @see #newThreadSafeContext()
     * @see Context#scan(Class) */
    public static Context newContext() {
        return new DefaultContext(new DefaultClassScanner());
    }

    /** @return a new instance of thread-safe {@link Context} using {@link DefaultClassScanner}.
     * @see #newContext()
     * @see Context#scan(Class) */
    public static Context newThreadSafeContext() {
        return new ConcurrentContext(new DefaultClassScanner());
    }
}
