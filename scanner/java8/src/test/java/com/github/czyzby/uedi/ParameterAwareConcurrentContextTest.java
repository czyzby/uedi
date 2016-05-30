package com.github.czyzby.uedi;

public class ParameterAwareConcurrentContextTest extends ExtendedContextTest {
    @Override
    protected Context getContext() {
        return ExtendedInjection.newThreadSafeContext();
    }
}
