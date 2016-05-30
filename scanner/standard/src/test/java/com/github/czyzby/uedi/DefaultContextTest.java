package com.github.czyzby.uedi;

import com.github.czyzby.uedi.impl.DefaultContext;
import com.github.czyzby.uedi.scanner.impl.DefaultClassScanner;

public class DefaultContextTest extends ContextTest {
    @Override
    protected Context getContext() {
        return new DefaultContext(new DefaultClassScanner());
    }
}
