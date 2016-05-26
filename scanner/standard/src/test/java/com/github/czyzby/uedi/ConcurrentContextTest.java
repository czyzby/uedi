package com.github.czyzby.uedi;

public class ConcurrentContextTest extends ContextTest {
    @Override
    protected Context getContext() {
        return DependencyInjection.newThreadSafeContext();
    }
}
