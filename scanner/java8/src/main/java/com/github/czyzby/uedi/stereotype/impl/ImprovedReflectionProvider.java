package com.github.czyzby.uedi.stereotype.impl;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.github.czyzby.uedi.Context;
import com.github.czyzby.uedi.stereotype.Default;
import com.github.czyzby.uedi.stereotype.Named;
import com.github.czyzby.uedi.stereotype.impl.DelegateProvider;
import com.github.czyzby.uedi.stereotype.impl.MockMember;
import com.github.czyzby.uedi.stereotype.impl.Providers;
import com.github.czyzby.uedi.stereotype.impl.ReflectionProvider;

/** Wraps around a method,converting it into a provider. Uses Java 8 {@link Parameter} utility to resolve ambiguous
 * injections.
 *
 * @author MJ
 * @see ReflectionProvider */
public class ImprovedReflectionProvider implements DelegateProvider<Object>, Named {
    private static final Member[] EMPTY_MEMBER_ARRAY = new Member[0];

    private final Context context;
    private final Method method;
    private final Class<?> type;
    private final Object owner;
    private final Parameter[] parameterDescriptions;
    private final Member[] parameterMembers;
    private final Object[] parameters;
    private final String name;
    private final boolean isDefault;

    public ImprovedReflectionProvider(final Context context, final Object owner, final Method method) {
        this.context = context;
        this.owner = owner;
        this.method = method;
        type = method.getReturnType();
        parameterDescriptions = method.getParameters();
        parameters = parameterDescriptions.length == 0 ? Providers.EMPTY_ARRAY
                : new Object[parameterDescriptions.length];
        parameterMembers = parameterDescriptions.length == 0 ? EMPTY_MEMBER_ARRAY
                : new Member[parameterDescriptions.length];
        for (int index = 0, length = parameters.length; index < length; index++) {
            parameterMembers[index] = new MockMember(parameterDescriptions[index].getName());
        }
        name = Providers.getName(method);
        isDefault = owner instanceof Default;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Object getWrappedObject() {
        return owner;
    }

    @Override
    public Class<? extends Object> getType() {
        return type;
    }

    @Override
    public Object provide(final Object target, final Member member) {
        try {
            if (parameters.length == 0) {
                return method.invoke(owner, parameters);
            }
            final Class<?> targetType = target == null ? null : target.getClass();
            for (int index = 0, length = parameters.length; index < length; index++) {
                final Class<?> parameterType = parameterDescriptions[index].getType();
                parameters[index] = parameterType == Object.class || parameterType == targetType ? target
                        : context.get(parameterType, owner, parameterMembers[index]);
            }
            return method.invoke(owner, parameters);
        } catch (final RuntimeException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new RuntimeException("Unable to invoke method: '" + method.getName() + "' of component: " + owner,
                    exception);
        }
    }
}
