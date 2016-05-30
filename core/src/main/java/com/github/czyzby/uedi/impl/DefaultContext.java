package com.github.czyzby.uedi.impl;

import java.io.Closeable;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.github.czyzby.uedi.Context;
import com.github.czyzby.uedi.scanner.ClassScanner;
import com.github.czyzby.uedi.stereotype.Destructible;
import com.github.czyzby.uedi.stereotype.Factory;
import com.github.czyzby.uedi.stereotype.Initiated;
import com.github.czyzby.uedi.stereotype.Named;
import com.github.czyzby.uedi.stereotype.Property;
import com.github.czyzby.uedi.stereotype.Provider;
import com.github.czyzby.uedi.stereotype.Singleton;
import com.github.czyzby.uedi.stereotype.impl.PropertyProvider;
import com.github.czyzby.uedi.stereotype.impl.ProviderManager;
import com.github.czyzby.uedi.stereotype.impl.Providers;
import com.github.czyzby.uedi.stereotype.impl.ReflectionProvider;
import com.github.czyzby.uedi.stereotype.impl.SingletonProvider;
import com.github.czyzby.uedi.stereotype.impl.StringProvider;

/** Core implementation of context management. Not thread-safe.
 *
 * @author MJ
 * @see ConcurrentContext */
public class DefaultContext extends AbstractContext {
    /** These interfaces are ignored while assigning components to their class tree. Components cannot be injected by
     * these types. Do NOT clear this set. Add new classes if necessary. */
    public static final Set<Class<?>> META_INTERFACES = Collections
            .newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
    /** These methods will be ignored when processing factories. */
    public static final HashSet<String> FORBIDDEN_METHOD_NAMES = new HashSet<String>();

    private final Map<Class<?>, Provider<?>> context = createMap();
    private final Set<Destructible> destructibles = createSet();
    private final StringProvider propertyProvider = getPropertyProvider();

    static {
        // Meta interfaces used by the SDI framework:
        META_INTERFACES.add(Destructible.class);
        META_INTERFACES.add(Factory.class);
        META_INTERFACES.add(Initiated.class);
        META_INTERFACES.add(Named.class);
        META_INTERFACES.add(Property.class);
        META_INTERFACES.add(Provider.class);
        META_INTERFACES.add(Singleton.class);

        // Common Java utility interfaces:
        META_INTERFACES.add(Comparable.class);
        META_INTERFACES.add(Serializable.class);
        META_INTERFACES.add(Closeable.class);
        META_INTERFACES.add(Cloneable.class);
        META_INTERFACES.add(Iterable.class);
        META_INTERFACES.add(Map.Entry.class);

        // Forbidden method names:
        FORBIDDEN_METHOD_NAMES.add("toString");
        FORBIDDEN_METHOD_NAMES.add("wait");
        FORBIDDEN_METHOD_NAMES.add("clone");
        FORBIDDEN_METHOD_NAMES.add("equals");
        FORBIDDEN_METHOD_NAMES.add("finalize");
        FORBIDDEN_METHOD_NAMES.add("notify");
        FORBIDDEN_METHOD_NAMES.add("notifyAll");
        FORBIDDEN_METHOD_NAMES.add("hashCode");
        FORBIDDEN_METHOD_NAMES.add("getClass");
    }

    /** @param classScanner can be null, but {@link #scan(Class)} method will not work correctly. */
    public DefaultContext(final ClassScanner classScanner) {
        super(classScanner);
        addCoreProviders();
    }

    /** @return default provider of {@link String} instances. */
    protected StringProvider getPropertyProvider() {
        return new PropertyProvider(this.<String, Property> createMap());
    }

    /** Registers {@link Context} (so it can be injected) and binds {@link PropertyProvider} to {@link String}
     * injections. */
    protected void addCoreProviders() {
        context.put(String.class, propertyProvider);
        context.put(Context.class, new SingletonProvider<Context>(this));
    }

    /** @return constructs a map that might be accessed and modified concurrently.
     * @param <K> type of used keys.
     * @param <V> type of stored values. */
    protected <K, V> Map<K, V> createMap() {
        return new HashMap<K, V>();
    }

    /** @return constructs a set that might be accessed and modified concurrently.
     * @param <V> type of stored values. */
    protected <V> Set<V> createSet() {
        return new HashSet<V>();
    }

    @Override
    public void add(final Object component) {
        processProvider(new SingletonProvider<Object>(component));
    }

    @Override
    public boolean isAvailable(final Class<?> type) {
        return context.containsKey(type);
    }

    @Override
    public String getProperty(final String name) {
        return propertyProvider.hasProperty(name) ? propertyProvider.getProperty(name).getValue() : null;
    }

    @Override
    public void setProperty(final String key, final String value) {
        if (propertyProvider.hasProperty(key)) {
            propertyProvider.getProperty(key).setValue(value);
        } else {
            addProperty(new Property() {
                private String property = value;

                @Override
                public String setValue(final String value) {
                    return property = value;
                }

                @Override
                public String getValue() {
                    return property;
                }

                @Override
                public String getKey() {
                    return key;
                }
            });
        }
    }

    @Override
    public void addProperty(final Property property) {
        propertyProvider.addProperty(property);
    }

    @Override
    public void addDestructible(final Destructible destructible) {
        destructibles.add(destructible);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Component> Component get(final Class<Component> type, final Object forObject, final Member member) {
        if (!context.containsKey(type)) {
            if (isFailIfUnknownType()) {
                throw new RuntimeException("Unknown component type: " + type.getName());
            }
            return create(type);
        }
        return (Component) context.get(type).provide(forObject, member);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Component> Component create(final Class<Component> type) {
        final Constructor<?> constructor = getConstructor(type);
        final Object component = createObject(constructor, constructor.getParameterTypes());
        initiate(component);
        return (Component) component;
    }

    @Override
    public void destroy() {
        final PriorityQueue<Destructible> sortedDestructibles = getPrioritySortedDestructionQueue();
        sortedDestructibles.addAll(destructibles);
        destructibles.clear();
        try {
            while (!sortedDestructibles.isEmpty()) {
                sortedDestructibles.poll().destroy();
            }
        } catch (final Exception exception) {
            throw new RuntimeException("Unable to destroy context.", exception);
        }
    }

    @Override
    public void destroy(final Destructible component) {
        if (component != null) {
            destructibles.remove(component);
            try {
                component.destroy();
            } catch (final Exception exception) {
                throw new RuntimeException("Unable to destroy: " + component, exception);
            }
        }
    }

    @Override
    protected void processClasses(final Iterable<Class<?>> classes) {
        try {
            final PriorityQueue<Initiated> componentsToInitiate = gatherComponents(gatherConstructors(classes));
            while (!componentsToInitiate.isEmpty()) {
                componentsToInitiate.poll().initiate();
            }
        } catch (final RuntimeException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new RuntimeException("Unable to create components.", exception);
        }
    }

    /** @param constructors list of gathered constructors. Will be used to create the components.
     * @return sorting collection of components to initiate. Should be initiated.
     * @throws Exception due to reflection issues. */
    protected PriorityQueue<Initiated> gatherComponents(final List<Constructor<?>> constructors) throws Exception {
        final PriorityQueue<Initiated> componentsToInitiate = getPrioritySortedInitiationQueue();
        final List<Object> components = createComponents(constructors, componentsToInitiate);
        for (final Object component : components) {
            injectFields(component);
        }
        return componentsToInitiate;
    }

    /** @param constructors list of gathered constructors of classes to initiate.
     * @param componentsToInitiate a reference to sorting collection of components to initiate. Should be filled.
     * @return list of constructed components.
     * @throws Exception due to reflection issues. */
    protected List<Object> createComponents(final List<Constructor<?>> constructors,
            final PriorityQueue<Initiated> componentsToInitiate) throws Exception {
        final List<Object> components = new ArrayList<Object>();
        for (int index = 0, iterations = getIterationsAmount(); !constructors.isEmpty()
                && index < iterations; index++) {
            for (final Iterator<Constructor<?>> iterator = constructors.iterator(); iterator.hasNext();) {
                final Constructor<?> constructor = iterator.next();
                final Object component;
                if (constructor.getParameterTypes().length == 0) {
                    component = constructor.newInstance(Providers.EMPTY_ARRAY);
                } else {
                    final Class<?>[] parameterTypes = constructor.getParameterTypes();
                    if (isAnyProviderMissing(parameterTypes)) {
                        continue;
                    }
                    component = createObject(constructor, parameterTypes);
                }
                processScannedComponent(component, componentsToInitiate);
                components.add(component);
                iterator.remove();
            }
        }
        if (!constructors.isEmpty()) {
            if (isFailIfUnknownType()) {
                final List<String> classNames = new ArrayList<String>();
                for (final Constructor<?> constructor : constructors) {
                    classNames.add(constructor.getDeclaringClass().getName());
                }
                throw new RuntimeException(
                        "Unknown or circular dependencies detected. Unable to create instances of: " + classNames);
            }
            for (final Constructor<?> constructor : constructors) {
                final Object component = createObject(constructor, constructor.getParameterTypes());
                processScannedComponent(component, componentsToInitiate);
                components.add(component);
            }
        }
        return components;
    }

    /** @param constructor will be used to construct the instance.
     * @param parameterTypes will be used to extract constructor parameters from the context.
     * @return an instance of the class.
     * @throws RuntimeException due to reflection issues. */
    protected Object createObject(final Constructor<?> constructor, final Class<?>[] parameterTypes) {
        try {
            if (parameterTypes.length == 0) {
                return constructor.newInstance(Providers.EMPTY_ARRAY);
            }
            final Object[] dependencies = new Object[parameterTypes.length];
            for (int index = 0, length = dependencies.length; index < length; index++) {
                dependencies[index] = get(parameterTypes[index], null, constructor);
            }
            return constructor.newInstance(dependencies);
        } catch (final Exception exception) {
            throw new RuntimeException("Unable to create an instance of: " + constructor.getDeclaringClass(),
                    exception);
        }
    }

    /** @param types array of requested types.
     * @return true if context currently has no provider that could supply an instance of any of the passed classes. */
    protected boolean isAnyProviderMissing(final Class<?>... types) {
        for (final Class<?> type : types) {
            if (!context.containsKey(type)) {
                return true;
            }
        }
        return false;
    }

    /** @param component its interfaces will be inspected. Depending on its type, it might be initiated, scheduled for
     *            destruction or registered as a factory, provider or property.
     * @param componentsToInitiate will be used to schedule initiations. */
    protected void processScannedComponent(final Object component,
            final PriorityQueue<Initiated> componentsToInitiate) {
        processProvider(new SingletonProvider<Object>(component));
        if (component instanceof Destructible) {
            destructibles.add((Destructible) component);
        }
        if (component instanceof Factory) {
            processFactory(component);
        }
        if (component instanceof Initiated) {
            componentsToInitiate.add((Initiated) component);
        }
        if (component instanceof Property) {
            propertyProvider.addProperty((Property) component);
        }
        if (component instanceof Provider<?>) {
            processProvider((Provider<?>) component);
        }
    }

    /** @param classes will have their constructors extracted. Should not contain interfaces or abstract classes.
     * @return a collection of constructors allowing to create passed classes' instances. */
    protected List<Constructor<?>> gatherConstructors(final Iterable<Class<?>> classes) {
        final List<Constructor<?>> constructors = new LinkedList<Constructor<?>>();
        for (final Class<?> componentClass : classes) {
            constructors.add(getConstructor(componentClass));
        }
        return constructors;
    }

    /** @param componentClass is requested to be constructed.
     * @return the first found constructor for the class. */
    protected Constructor<?> getConstructor(final Class<?> componentClass) {
        final Constructor<?>[] constructors = componentClass.getConstructors();
        if (constructors.length == 0) {
            throw new RuntimeException("No public constructors found for component class: " + componentClass);
        }
        return constructors[0];
    }

    /** @return priority queue sorting initiated components in ascending order. */
    protected PriorityQueue<Initiated> getPrioritySortedInitiationQueue() {
        return new PriorityQueue<Initiated>(16, new Comparator<Initiated>() {
            @Override
            public int compare(final Initiated o1, final Initiated o2) {
                return o1.getInitiationOrder() - o2.getInitiationOrder();
            }
        });
    }

    /** @return priority queue sorting destructibles in ascending order. */
    protected PriorityQueue<Destructible> getPrioritySortedDestructionQueue() {
        return new PriorityQueue<Destructible>(16, new Comparator<Destructible>() {
            @Override
            public int compare(final Destructible o1, final Destructible o2) {
                return o1.getDestructionOrder() - o2.getDestructionOrder();
            }
        });
    }

    /** Note: this method should be invoked only with externally registered components.
     *
     * @param component will be initiated.
     * @see #processScannedComponent(Object, PriorityQueue) */
    @Override
    protected void processComponent(final Object component) {
        injectFields(component);
        if (component instanceof Initiated) {
            try {
                ((Initiated) component).initiate();
            } catch (final Exception exception) {
                throw new RuntimeException("Unable to initiate component: " + component, exception);
            }
        }
        if (component instanceof Destructible) {
            destructibles.add((Destructible) component);
        }
    }

    /** @return direct reference to component providers. */
    protected Map<Class<?>, Provider<?>> getComponentProviders() {
        return context;
    }

    /** @param component its injectable fields will be filled with values provided by the context.
     * @see #isInjectable(Field, Object) */
    protected void injectFields(final Object component) {
        Class<?> processedClass = component.getClass();
        try {
            while (processedClass != null && processedClass != Object.class) {
                for (final Field field : processedClass.getDeclaredFields()) {
                    if (isInjectable(field, component)) {
                        field.set(component, get(field.getType(), component, field));
                    }
                }
                if (!isProcessSuperFields()) {
                    break;
                }
                processedClass = processedClass.getSuperclass();
            }
        } catch (final Exception exception) {
            throw new RuntimeException("Unable to inject fields of component: " + component, exception);
        }
    }

    /** @param field reflected field data.
     * @param component owner of the field.
     * @return true if the field is empty, accepted by the modifier filter, does not match modifier signature, not
     *         primitive and - if strings are ignored - not a string.
     * @throws Exception due to reflection issues. */
    protected boolean isInjectable(final Field field, final Object component) throws Exception {
        if (field.isSynthetic() || field.getType().isPrimitive()
                || isIgnoreStrings() && field.getType() == String.class) {
            return false;
        }
        final int modifier = field.getModifiers();
        if ((modifier & getFieldsIgnoreFilter()) != 0 || modifier == getFieldsIgnoreSignature()) {
            return false;
        }
        field.setAccessible(true);
        return field.get(component) == null;
    }

    @Override
    protected void processProvider(final Provider<?> provider) {
        if (!isMapSuperTypes()) {
            putProvider(provider.getType(), provider);
            return;
        }
        final Queue<Class<?>> classesToProcess = new LinkedList<Class<?>>();
        final Set<Class<?>> processedClasses = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
        classesToProcess.add(provider.getType());
        while (!classesToProcess.isEmpty()) {
            final Class<?> processed = classesToProcess.poll();
            if (processedClasses.contains(processed)) {
                continue;
            }
            processedClasses.add(processed);
            putProvider(processed, provider);
            final Class<?> parent = processed.getSuperclass();
            if (parent != null && parent != Object.class) {
                classesToProcess.add(parent);
            }
            for (final Class<?> implemented : processed.getInterfaces()) {
                if (!processedClasses.contains(implemented) && !META_INTERFACES.contains(implemented)) {
                    classesToProcess.add(implemented);
                }
            }
        }
    }

    /** @param key provided class type.
     * @param provider will be assigned as a provider of the chosen class instances. */
    protected void putProvider(final Class<?> key, final Provider<?> provider) {
        final Provider<?> currentProvider = context.get(key);
        if (currentProvider == null) { // Unique - setting as the default provider:
            context.put(key, provider);
        } else if (currentProvider instanceof ProviderManager<?>) { // Already ambiguous - adding another provider:
            ((ProviderManager<?>) currentProvider).addProvider(provider);
        } else {
            @SuppressWarnings({ "rawtypes", "unchecked" }) // Ambiguous - switching to manager:
            final ProviderManager<?> manager = new ProviderManager(createMap(), key, this);
            // Registering existing providers:
            manager.addProvider(currentProvider);
            manager.addProvider(provider);
            // Replacing current provider with the manager:
            context.put(key, manager);
        }
    }

    @Override
    public void remove(final Class<?> type) {
        context.remove(type);
    }

    @Override
    public <Type> void replace(final Class<Type> type, final Provider<? extends Type> provider) {
        remove(type);
        putProvider(type, provider);
    }

    @Override
    protected void processFactory(final Object factory) {
        // Registering public methods as providers:
        for (final Method method : factory.getClass().getMethods()) {
            if (isValidFactoryMethod(method)) {
                processProvider(newFactoryMethodWrapper(factory, method));
            }
        }
    }

    /** @param method cannot be synthetic, return void, have a forbidden name or have any filtered modifiers.
     * @return true if the method is valid and should be converted to a provider. */
    protected boolean isValidFactoryMethod(final Method method) {
        final int modifiers = method.getModifiers();
        return !method.isSynthetic() && (modifiers & getMethodsIgnoreFilter()) == 0
                && modifiers != getMethodsIgnoreSignature() && method.getReturnType() != void.class
                && method.getReturnType() != Void.class && !FORBIDDEN_METHOD_NAMES.contains(method.getName());
    }

    /** @param factory owner of the method.
     * @param method should be wrapped.
     * @return method wrapped in a {@link Provider} implementation. */
    protected Provider<?> newFactoryMethodWrapper(final Object factory, final Method method) {
        return new ReflectionProvider(this, factory, method);
    }

    @Override
    public boolean isParameterAware() {
        return false;
    }

    @Override
    public void clear(final Class<?> classTree) {
        if (!isMapSuperTypes()) {
            remove(classTree);
            return;
        }
        final Queue<Class<?>> classesToProcess = new LinkedList<Class<?>>();
        final Set<Class<?>> processedClasses = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
        classesToProcess.add(classTree);
        while (!classesToProcess.isEmpty()) {
            final Class<?> processed = classesToProcess.poll();
            if (processedClasses.contains(processed)) {
                continue;
            }
            processedClasses.add(processed);
            remove(processed);
            final Class<?> parent = processed.getSuperclass();
            if (parent != null && parent != Object.class) {
                classesToProcess.add(parent);
            }
            for (final Class<?> implemented : processed.getInterfaces()) {
                if (!processedClasses.contains(implemented) && !META_INTERFACES.contains(implemented)) {
                    classesToProcess.add(implemented);
                }
            }
        }
    }

    @Override
    public void clear() {
        context.clear();
        addCoreProviders();
    }
}
