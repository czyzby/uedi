package com.github.czyzby.uedi.test.ambiguous;

import com.github.czyzby.uedi.stereotype.Named;
import com.github.czyzby.uedi.stereotype.Singleton;

public class NamedAmbiguous implements Named, Ambiguous, Singleton {
    public static final String NAME = "named";

    @Override
    public String getName() {
        return NAME;
    }
}
