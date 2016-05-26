package com.github.czyzby.uedi;

public class ParameterAwareContextTest extends ExtendedContextTest {
    @Override
    protected Context getContext() {
        return DependencyInjection.newContext();
    }
}
