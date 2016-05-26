package com.github.czyzby.uedi.stereotype;

/** Classes implementing this interface will be scanned for and fully initiated. All public methods of factories that do
 * not return {@code void} or {@link Void} will be turned into {@link Provider providers}, supplying objects of classes
 * matching their return type. Any non-primitive parameters of factories' methods will be injected from the context; if
 * parameter type matches {@link Object}, the instance of class that uses the factory will be injected.
 *
 * <p>
 * While convenient, factories' methods are invoked using reflection, which comes with an overhead. If objects of
 * certain type are required more often than most, one should consider using a dedicated {@link Provider} for such
 * class.
 *
 * <p>
 * Note that factories should not contains multiple public methods with the same exact names, as they would all be
 * mapped to the same ID. Factory names should be kept unique across the application.
 *
 * <p>
 * Factories are treated as {@link Singleton singletons} and their fields are injected. In fact, factories themselves
 * are available as injectable components, so one can inject their instance and use their methods directly.
 *
 * @author MJ */
public interface Factory {
}
