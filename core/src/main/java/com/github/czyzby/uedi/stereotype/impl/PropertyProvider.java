package com.github.czyzby.uedi.stereotype.impl;

import java.lang.reflect.Member;
import java.util.Map;

import com.github.czyzby.uedi.stereotype.Default;
import com.github.czyzby.uedi.stereotype.Property;

/** Provides string properties using {@link Property} API.
 *
 * @author MJ */
public class PropertyProvider implements Default, StringProvider {
    private final Map<String, Property> properties;

    /** @param properties will be used to store properties mapped by their keys. Should be thread-safe is used in
     *            multi-threaded environment. */
    public PropertyProvider(final Map<String, Property> properties) {
        this.properties = properties;
    }

    @Override
    public boolean hasProperty(final String key) {
        return properties.containsKey(key);
    }

    @Override
    public Property getProperty(final String key) {
        return properties.get(key);
    }

    @Override
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
