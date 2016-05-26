package com.github.czyzby.uedi.stereotype.impl;

import java.lang.reflect.Member;
import java.util.Map;

import com.github.czyzby.uedi.stereotype.Default;
import com.github.czyzby.uedi.stereotype.Property;
import com.github.czyzby.uedi.stereotype.Provider;

/** Provides string properties using {@link Property} API.
 *
 * @author MJ */
public class PropertyProvider implements Provider<String>, Default {
    private final Map<String, Property> properties;

    /** @param properties will be used to store properties mapped by their keys. Should be thread-safe is used in
     *            multi-threaded environment. */
    public PropertyProvider(final Map<String, Property> properties) {
        this.properties = properties;
    }

    /** @param key unique ID of the property.
     * @return true if a property instance was registered to the key. */
    public boolean hasProperty(final String key) {
        return properties.containsKey(key);
    }

    /** @param key unique ID of the property.
     * @return {@link Property} instance mapped to the ID or null if not registered. */
    public Property getProperty(final String key) {
        return properties.get(key);
    }

    /** @param property will be mapped to its {@link Property#getKey() key}. */
    public void addProperty(final Property property) {
        properties.put(property.getKey(), property);
    }

    @Override
    public Class<? extends String> getType() {
        return String.class;
    }

    @Override
    public String provide(final Object target, final Member member) {
        final String key = Providers.getName(member);
        if (properties.containsKey(key)) {
            return properties.get(key).getValue();
        }
        return null;
    }
}
