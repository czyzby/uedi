package com.github.czyzby.uedi;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.czyzby.uedi.param.ConstructorParameterAware;
import com.github.czyzby.uedi.param.ExtendedRoot;
import com.github.czyzby.uedi.param.MethodParameterAware.FactoryResult;
import com.github.czyzby.uedi.param.NamedAmbiguousA;
import com.github.czyzby.uedi.param.NamedAmbiguousB;

/** Extends {@link Context} test suite with methods testing parameter analysis. Should be extended only by test suites
 * of contexts that are aware of parameter names and can resolve ambiguous method/constructor dependencies using
 * parameters.
 *
 * @author MJ */
public abstract class ExtendedContextTest extends ContextTest {
    @Before
    @Override
    public void scan() {
        super.scan();
        if (!context.isParameterAware()) {
            throw new IllegalStateException("This context will never pass tests from this suite.");
        }
        context.scan(ExtendedRoot.class);
    }

    // Note that all tests use "arg0", "arg1", (...) parameter names to match automatically assigned param names when
    // -parameters flag is not given.

    @Test
    public void shouldResolveAmbiguousConstructorDependenciesByParameterNames() throws Exception {
        final ConstructorParameterAware constructor = context.get(ConstructorParameterAware.class);
        assertTrue(constructor.getA() instanceof NamedAmbiguousA);
    }

    @Test
    public void shouldResolveAmbiguousFactoryMethodDependenciesByParameterNames() {
        final FactoryResult result = context.get(FactoryResult.class); // Invokes MethodParameterAware factory.
        assertTrue(result.getA() instanceof NamedAmbiguousA);
        assertTrue(result.getB() instanceof NamedAmbiguousB);
    }
}
