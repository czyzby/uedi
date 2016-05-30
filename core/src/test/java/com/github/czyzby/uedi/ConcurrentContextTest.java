package com.github.czyzby.uedi;

import com.github.czyzby.uedi.impl.ConcurrentContext;

public class ConcurrentContextTest extends ContextTest {
    @Override
    protected Context getContext() {
        return new ConcurrentContext(MockScanner.getClassScanner());
    }
}
