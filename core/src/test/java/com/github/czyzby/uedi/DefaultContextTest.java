package com.github.czyzby.uedi;

import com.github.czyzby.uedi.impl.DefaultContext;

public class DefaultContextTest extends ContextTest {
    @Override
    protected Context getContext() {
        return new DefaultContext(MockScanner.getClassScanner());
    }
}
