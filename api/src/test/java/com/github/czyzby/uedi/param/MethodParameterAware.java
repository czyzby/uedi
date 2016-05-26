package com.github.czyzby.uedi.param;

import com.github.czyzby.uedi.stereotype.Factory;

public class MethodParameterAware implements Factory {
    public FactoryResult produce(final AmbiguousBase arg0, final AmbiguousBase arg1) {
        return new FactoryResult(arg0, arg1);
    }

    public static class FactoryResult {
        AmbiguousBase a;
        AmbiguousBase b;

        public FactoryResult(final AmbiguousBase a, final AmbiguousBase b) {
            this.a = a;
            this.b = b;
        }

        public AmbiguousBase getA() {
            return a;
        }

        public AmbiguousBase getB() {
            return b;
        }
    }
}
