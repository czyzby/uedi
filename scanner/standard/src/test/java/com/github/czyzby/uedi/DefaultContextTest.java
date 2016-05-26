package com.github.czyzby.uedi;

public class DefaultContextTest extends ContextTest {
    @Override
    protected Context getContext() {
        return DependencyInjection.newContext();
    }
}
