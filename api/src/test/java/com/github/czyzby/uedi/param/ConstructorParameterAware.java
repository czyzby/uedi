package com.github.czyzby.uedi.param;

import com.github.czyzby.uedi.stereotype.Singleton;

public class ConstructorParameterAware implements Singleton {
    private final AmbiguousBase a;

    public ConstructorParameterAware(final AmbiguousBase arg0) {
        a = arg0;
    }

    public AmbiguousBase getA() {
        return a;
    }
}
