package com.github.czyzby.uedi.stereotype;

/** If this interface is implemented by a {@link Provider}, {@link Singleton} or {@link Factory}, instances of objects
 * provided by them will be preferred over the others when resolving ambiguous dependencies without appropriate names.
 * For example, when a field is named "someValue", it will look of a {@link Named} provider mapped to "someValue", then
 * for a provider with class named "SomeValue", and finally fallback to the default value if no specific provider is
 * found for the selected type.
 *
 * @author MJ */
public interface Default {
}
