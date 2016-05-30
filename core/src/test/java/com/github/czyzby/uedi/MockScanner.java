package com.github.czyzby.uedi;

import com.github.czyzby.uedi.error.circular.CircularErrorA;
import com.github.czyzby.uedi.error.circular.CircularErrorB;
import com.github.czyzby.uedi.scanner.ClassScanner;
import com.github.czyzby.uedi.scanner.impl.FixedClassScanner;
import com.github.czyzby.uedi.test.Root;
import com.github.czyzby.uedi.test.TestComponent;
import com.github.czyzby.uedi.test.TestFactory;
import com.github.czyzby.uedi.test.TestProperty;
import com.github.czyzby.uedi.test.TestProvider;
import com.github.czyzby.uedi.test.TestSingleton;
import com.github.czyzby.uedi.test.ambiguous.Ambiguous;
import com.github.czyzby.uedi.test.ambiguous.AmbiguousA;
import com.github.czyzby.uedi.test.ambiguous.AmbiguousB;
import com.github.czyzby.uedi.test.ambiguous.AmbiguousInjector;
import com.github.czyzby.uedi.test.ambiguous.ListDefaultProvider;
import com.github.czyzby.uedi.test.ambiguous.ListFactory;
import com.github.czyzby.uedi.test.ambiguous.NamedAmbiguous;
import com.github.czyzby.uedi.test.classpath.AbstractClassImplementingSingleton;
import com.github.czyzby.uedi.test.classpath.AbstractClassUser;
import com.github.czyzby.uedi.test.classpath.InterfaceExtendingSingleton;
import com.github.czyzby.uedi.test.classpath.InterfaceUser;
import com.github.czyzby.uedi.test.custom.CustomFactory;
import com.github.czyzby.uedi.test.custom.CustomSingleton;
import com.github.czyzby.uedi.test.inject.AbstractWithFields;
import com.github.czyzby.uedi.test.inject.Built;
import com.github.czyzby.uedi.test.inject.CircularA;
import com.github.czyzby.uedi.test.inject.CircularB;
import com.github.czyzby.uedi.test.inject.ConstructorDependency;
import com.github.czyzby.uedi.test.inject.Ignored;
import com.github.czyzby.uedi.test.inject.InjectFactory;
import com.github.czyzby.uedi.test.inject.InjectProperty;
import com.github.czyzby.uedi.test.inject.InjectProvider;
import com.github.czyzby.uedi.test.inject.Injected;
import com.github.czyzby.uedi.test.inject.Injector;
import com.github.czyzby.uedi.test.inject.Provided;
import com.github.czyzby.uedi.test.inject.UsingAbstractWithFields;
import com.github.czyzby.uedi.test.lifecycle.Counter;
import com.github.czyzby.uedi.test.lifecycle.DestroyedA;
import com.github.czyzby.uedi.test.lifecycle.DestroyedB;
import com.github.czyzby.uedi.test.lifecycle.DestroyedC;
import com.github.czyzby.uedi.test.lifecycle.InitiatedA;
import com.github.czyzby.uedi.test.lifecycle.InitiatedB;
import com.github.czyzby.uedi.test.lifecycle.InitiatedC;

/** Provides mock-up {@link ClassScanner}.
 *
 * @author MJ */
public class MockScanner {
    private MockScanner() {
    }

    /** @return {@link FixedClassScanner} that mocks automatic classpath scanning. */
    public static ClassScanner getClassScanner() {
        return new FixedClassScanner(CircularErrorA.class, CircularErrorB.class, Root.class, TestComponent.class,
                TestFactory.class, TestProperty.class, TestProvider.class, TestSingleton.class, Ambiguous.class,
                AmbiguousA.class, AmbiguousB.class, AmbiguousInjector.class, ListDefaultProvider.class,
                ListFactory.class, NamedAmbiguous.class, AbstractClassImplementingSingleton.class,
                AbstractClassUser.class, InterfaceExtendingSingleton.class, InterfaceUser.class, CustomFactory.class,
                CustomSingleton.class, AbstractWithFields.class, Built.class, CircularA.class, CircularB.class,
                ConstructorDependency.class, Ignored.class, Injected.class, InjectFactory.class, Injector.class,
                InjectProperty.class, InjectProvider.class, Provided.class, UsingAbstractWithFields.class,
                Counter.class, DestroyedA.class, DestroyedB.class, DestroyedC.class, InitiatedA.class, InitiatedB.class,
                InitiatedC.class);
    }
}
