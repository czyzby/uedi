package com.github.czyzby.uedi;

import com.github.czyzby.uedi.impl.ConcurrentContext;
import com.github.czyzby.uedi.scanner.impl.StandardClassScanner;

public class ConcurrentContextTest extends ContextTest {
    @Override
    protected Context getContext() {
        return new ConcurrentContext(new StandardClassScanner());
    }
}
