package com.github.czyzby.uedi.stereotype;

import java.lang.reflect.Member;

/** On contrary to {@link Factory factories}, providers supply objects of a single type and do not rely on reflection to
 * invoke their methods. Providers should be the preferred object suppliers for objects that are requested rather often,
 * as the offer less overhead than factories.
 *
 * <p>
 * Providers are treated as {@link Singleton singletons} and their fields are injected. In fact, providers themselves
 * are available as injectable components, so one can inject their instance and use their methods directly.
 *
 * @author MJ
 *
 * @param <Type> type of provided objects. */
public interface Provider<Type> {
    /** @return the supported type of provided objects. */
    Class<? extends Type> getType();

    /** @param target instance of object that requested the instance. If the object is not constructed yet,
     *            {@link java.lang.reflect.Constructor constructor} instance will be passed instead. Might be null if
     *            instance was requested directly from the context without passing the purpose data.
     * @param member can be null. Can be a field, constructor, method or parameter that requested the instance. Allows
     *            to process the member that requested the injection by - for example - extracting and analyzing its
     *            name. Can be ignored.
     * @return an instance of the supported type. */
    Type provide(Object target, Member member);
}
