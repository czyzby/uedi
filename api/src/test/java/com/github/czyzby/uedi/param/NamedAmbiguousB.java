package com.github.czyzby.uedi.param;

import com.github.czyzby.uedi.stereotype.Named;
import com.github.czyzby.uedi.stereotype.Singleton;

public class NamedAmbiguousB implements AmbiguousBase, Named, Singleton {
    @Override
    public String getName() {
        return "arg1"; // Matches second argument name if -parameters compiler flag is not used.
    }
}
