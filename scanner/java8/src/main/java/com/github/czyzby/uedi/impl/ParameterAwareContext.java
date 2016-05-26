package com.github.czyzby.uedi.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.github.czyzby.uedi.scanner.ClassScanner;
import com.github.czyzby.uedi.stereotype.Provider;
import com.github.czyzby.uedi.stereotype.impl.ImprovedReflectionProvider;
import com.github.czyzby.uedi.stereotype.impl.MockMember;
import com.github.czyzby.uedi.stereotype.impl.Providers;

/** Uses Java 8 {@link Parameter} reflection utility to extract parameter names. Requires -parameters compiler flag to
 * work properly.
 *
 * @author MJ */
public class ParameterAwareContext extends DefaultContext {
    public ParameterAwareContext(final ClassScanner classScanner) {
        super(classScanner);
    }

    @Override
    public boolean isParameterAware() {
        return true;
    }

    @Override
    protected Provider<?> newFactoryMethodWrapper(final Object factory, final Method method) {
        return new ImprovedReflectionProvider(this, factory, method);
    }

    @Override
    protected Object createObject(final Constructor<?> constructor, final Class<?>[] parameterTypes) {
        try {
            if (parameterTypes.length == 0) {
                return constructor.newInstance(Providers.EMPTY_ARRAY);
            }
            final Object[] dependencies = new Object[parameterTypes.length];
            final Parameter[] parameters = constructor.getParameters();
            for (int index = 0, length = dependencies.length; index < length; index++) {
                dependencies[index] = get(parameterTypes[index], null, new MockMember(parameters[index].getName()));
            }
            return constructor.newInstance(dependencies);
        } catch (final Exception exception) {
            throw new RuntimeException("Unable to create an instance of: " + constructor.getDeclaringClass(),
                    exception);
        }
    }
}
