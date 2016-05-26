package com.github.czyzby.uedi.stereotype;

import java.util.Map;

/** Allows to store string properties and inject them to other components. Only field injection is supported: field name
 * must match the value returned by property's {@link #getKey()} method. More complex properties should be implemented
 * with singletons.
 *
 * <p>
 * Note that classes implementing this interface are treated as singletons, so their are scanned for and fully
 * initiated. Their fields will be injected and they will be available in the context for injection.
 *
 * @author MJ
 * @see Singleton */
public interface Property extends Map.Entry<String, String> {
    /** @return unique ID of the stored property. */
    @Override
    String getKey();

    /** @return current value of the stored property. */
    @Override
    String getValue();

    /** @param value should be assigned as (or converted/serialized to) current property value.
     * @return the old value assigned to the property. */
    @Override
    String setValue(String value);
}
