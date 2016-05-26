package com.github.czyzby.uedi.stereotype;

/** Components can implement this interface to help resolving ambiguous dependencies. When a field requires injection of
 * a value and there are multiple providers mapped to its type, its name will be extracted and used to find a suitable
 * provider.
 *
 * <p>
 * This interface can be implemented by {@link Singleton singletons} and {@link Provider providers}. {@link Property
 * Properties} already supply their name with {@link Property#getKey()} method and {@link Factory factories's} methods
 * are mapped individually by their names.
 *
 * @author MJ */
public interface Named {
    /** @return the unique ID of the component. */
    String getName();
}
