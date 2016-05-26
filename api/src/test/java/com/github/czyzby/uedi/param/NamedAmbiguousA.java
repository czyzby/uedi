package com.github.czyzby.uedi.param;

import com.github.czyzby.uedi.stereotype.Named;
import com.github.czyzby.uedi.stereotype.Singleton;

public class NamedAmbiguousA implements AmbiguousBase, Named, Singleton {
    @Override
    public String getName() {
        return "arg0"; // Matches first argument name if -parameters compiler flag is not used.
    }
}
