package com.github.czyzby.uedi;

import java.lang.reflect.Member;

import com.github.czyzby.uedi.scanner.ClassScanner;
import com.github.czyzby.uedi.stereotype.Destructible;
import com.github.czyzby.uedi.stereotype.Property;
import com.github.czyzby.uedi.stereotype.Provider;

/** Manages a collection of object providers. Creates, initiates and fills instances of components. Provides
 * reflection-base dependency injection with automatic component scan.
 *
 * @author MJ */
public interface Context {
    /** @return class scanner used to automatically find classes that should be included in the context.
     * @see #scan(Class) */
    ClassScanner getClassScanner();

    /** @param classScanner used to automatically find classes that should be included in the context.
     * @see #scan(Class) */
    void setClassScanner(ClassScanner classScanner);

    /** @param root package of this class will become the scanning root. All classes implementing interfaces from
     *            {@link com.github.czyzby.uedi.scanner} package will be found, initiated and included in the
     *            context. */
    void scan(Class<?> root);

    /** Allows to manually register a new singleton component in the context.
     *
     * @param component a fully initiated component instance. Will be registered in the context.
     * @see com.github.czyzby.uedi.stereotype.Singleton */
    void add(Object component);

    /** @param type might be available in the context.
     * @return true if there is a provider, factory or singleton matching this type. */
    boolean isAvailable(Class<?> type);

    /** Allows to fill compontent's fields with values from the context. Note that this method only fills the component
     * - it does not include it in the context for others to inject.
     *
     * @param component all of its non-primitive, empty, non-transient fields will be injected.
     * @return this context, for chaining.
     * @see #add(Object) */
    Context initiate(Object component);

    /** Allows to manually register a new provider component in the context.
     *
     * @param provider a fully initiated provider used to supply an instance of a chosen class. Will be registered in
     *            the context.
     * @see Provider
     * @see #add(Object) */
    void addProvider(Provider<?> provider);

    /** @param type providers associated with this type will be removed and context will no longer be aware of this
     *            class. Note that if {@link #isMapSuperTypes() mapping super types} is turned on, the providers might
     *            not be removed completely and still be mapped to the super classes or interfaces implemented by the
     *            types they return.
     * @see #addProvider(Provider)
     * @see #addFactory(Object)
     * @see #clear(Class) */
    void remove(Class<?> type);

    /** @param type providers associated with this type will be replaced.
     * @param provider from now on, this provider will be used to provide instances of selected type.
     * @param <Type> type of provided value.
     * @see #addProvider(Provider) */
    <Type> void replace(Class<Type> type, Provider<? extends Type> provider);

    /** Allows to manually register a new factory component in the context.
     *
     * @param factory an instance of a fully initiated factory, which public methods will be converted to
     *            {@link Provider providers}. Will be registered in the context.
     * @see com.github.czyzby.uedi.stereotype.Factory */
    void addFactory(Object factory);

    /** @param name unique ID of the registered property.
     * @return current value of the property.
     * @see Property */
    String getProperty(String name);

    /** Allows to manually register a new property provider in the context.
     *
     * @param property will accessible through string field injections and {@link #getProperty(String)} method.
     * @see Property */
    void addProperty(Property property);

    /** Allows to modify current value of a {@link Property}. The property must be already registered.
     *
     * @param key unique key of the property.
     * @param value the new property value.
     * @see #addProperty(Property)
     * @throws NullPointerException if property is not registered. */
    void setProperty(String key, String value);

    /** @param type required type of component.
     * @param <Component> class of requested component or one of interfaces or one of its super classes.
     * @return a new instance of the selected class created and initiated by the context. Does not invoke existing
     *         providers or factories - use {@link #get(Class)} instead. Note that the instance will not be added to the
     *         context and cannot be injected into other components unless explicitly added to the context.
     * @see #add(Object)
     * @see #get(Class) */
    <Component> Component create(Class<Component> type);

    /** @param type required type of component.
     * @param <Component> class of requested component or one of interfaces or one of its super classes.
     * @return an instance of the selected class or null if unavailable in context. */
    <Component> Component get(Class<Component> type);

    /** @param id unique ID of the provider, singleton or factory method that should be used. This matches value
     *            returned by {@link com.github.czyzby.uedi.stereotype.Named#getName() getName()} (if component is
     *            named) or class name converted to lower camel case. In case of factories, ID always matches the method
     *            name that produces the desired value.
     * @param type required type of component.
     * @param <Component> class of requested component or one of interfaces or one of its super classes.
     * @return an instance of the selected class or null if unavailable in context. */
    <Component> Component get(String id, Class<Component> type);

    /** Note that this is a part of rather internal API and {@link #get(Class)} is sufficient in most cases.
     *
     * @param type required type of component.
     * @param forObject requested the instance. Will be passed to the provider.
     * @param <Component> class of requested component or one of interfaces or one of its super classes.
     * @return an instance of the selected class or null if unavailable in context.
     * @see #get(Class) */
    <Component> Component get(Class<Component> type, Object forObject);

    /** Note that this is a part of rather internal API and {@link #get(Class)} is sufficient in most cases.
     *
     * @param type required type of component.
     * @param forObject requested the instance. Will be passed to the provider.
     * @param member a reference to a specific field, method or constructor that requested the instance.
     * @param <Component> class of requested component or one of interfaces or one of its super classes.
     * @return an instance of the selected class or null if unavailable in context.
     * @see #get(Class) */
    <Component> Component get(Class<Component> type, Object forObject, Member member);

    /** @param type required type of component.
     * @param alternative will be returned if no component is available.
     * @param <Component> class of requested component or one of interfaces or one of its super classes.
     * @return an instance of the selected class or the alternative if unavailable in context. */
    <Component> Component getOrElse(Class<Component> type, Component alternative);

    /** Invokes {@link Destructible#destroy()} method of all destructible components created by this context. */
    void destroy();

    /** @param component if it is currenly scheduled for destruction, its {@link Destructible#destroy()} method will be
     *            invoked. Will be removed from context. */
    void destroy(Destructible component);

    /** @param fail if true, requested instances of classes that are unavailable in context will not be constructed
     *            using reflection - only singletons and objects with implemented providers or factories will be
     *            supplied. If false, context will try its best to create and initiate an instance of the requested
     *            type. Defaults to true. */
    void setFailIfUnknownType(boolean fail);

    /** @return if true, requested instances of classes that are unavailable in context will not be constructed using
     *         reflection - only singletons and objects with implemented providers or factories will be supplied. If
     *         false, context will try its best to create and initiate an instance of the requested type. Defaults to
     *         true. */
    boolean isFailIfUnknownType();

    /** @param fail if true, requesting instances of classes mapped to multiple providers will result in an exception,
     *            unless this is a injection to field named like the class of the singleton, provider or factory
     *            (converted to lower camel case) or matching ID returned by the
     *            {@link com.github.czyzby.uedi.stereotype.Named#getName() getName()} method if the provider is named.
     *            Note that exception is not thrown if a default provider is present. If set to false, the a new
     *            component instance will be created using the context. Defaults to true. */
    void setFailIfAmbiguousDependency(boolean fail);

    /** @return if true, requesting instances of classes mapped to multiple providers will result in an exception,
     *         unless this is a injection to field named like the class of the singleton, provider or factory (converted
     *         to lower camel case) or matching ID returned by the
     *         {@link com.github.czyzby.uedi.stereotype.Named#getName() getName()} method if the provider is named. Note
     *         that exception is not thrown if a default provider is present. If set to false, the a new component
     *         instance will be created using the context. Defaults to true. */
    boolean isFailIfAmbiguousDependency();

    /** @param process if true, components will have all of their fields injected, including the ones from their super
     *            classes. This might be risky if using third-party abstract classes. Set to false to extract and
     *            initiate fields only from the component class. Defaults to true. */
    void setProcessSuperFields(boolean process);

    /** @return if true, components will have all of their fields injected, including the ones from their super classes.
     *         Defaults to true. */
    boolean isProcessSuperFields();

    /** Example usage: {@code context.setFieldsIgnoreFilter(Modifier.TRANSIENT | Modifier.PUBLIC)}.
     *
     * @param filter fields with any of these modifiers will not be injected. Normally, to stop the fields from being
     *            injected, one can use {@code transient} keyword, but to make it even less verbose, this setting can be
     *            applied to ignore fields with any modifiers. For example, one could exclude all package-private
     *            fields, so only private, protected and public fields would be injected. By default, only transient and
     *            static fields are ignored. */
    void setFieldsIgnoreFilter(int filter);

    /** @param signature if the field has this exact signature, it will be ignored even if it passed the
     *            {@link #setFieldsIgnoreFilter(int) filter}. This is a utility for package-private fields, as - by
     *            default - their signature matches 0 or {@code Modifier.FINAL} (if final) and there's no way to filter
     *            using the same mechanism. Defaults to transient modifier. */
    void setFieldsIgnoreSignature(int signature);

    /** @return fields with any of these modifiers will not be injected. */
    int getFieldsIgnoreFilter();

    /** @return fields with modifier matching this exact signature will not be injected. */
    int getFieldsIgnoreSignature();

    /** Example usage: {@code context.setMethodsIgnoreFilter(Modifier.STATIC | Modifier.NATIVE)}.
     *
     * @param filter factory methods with any of these modifiers will not be converted into providers. By default, only
     *            public methods are extracted from factories and additionally native and static methods are filtered
     *            out. */
    void setMethodsIgnoreFilter(int filter);

    /** @param signature factory methods with this exact modifiers will not be converted into providers. By default,
     *            only public methods are extracted from factories and additionally native and static methods are
     *            filtered out. */
    void setMethodsIgnoreSignature(int signature);

    /** @return factory methods with any of these modifiers will not be converted into providers. */
    int getMethodsIgnoreFilter();

    /** @return factory methods with modifier matching this exact signature will not be converted into providers. */
    int getMethodsIgnoreSignature();

    /** @param ignore if true, {@link String} fields will not be injected even if null. By default, string fields are
     *            filled with registered {@link com.github.czyzby.uedi.stereotype.Property properties} with keys
     *            matching their names. Change to false to ignore string fields similarly to fields with primitive
     *            types.
     * @see com.github.czyzby.uedi.stereotype.Property */
    void setIgnoreStrings(boolean ignore);

    /** @return if true, {@link String} fields will not be injected even if null. By default, string fields are filled
     *         with registered {@link com.github.czyzby.uedi.stereotype.Property properties} with keys matching their
     *         names.
     * @see com.github.czyzby.uedi.stereotype.Property */
    boolean isIgnoreStrings();

    /** @param iterations to resolve constructor dependencies, context iterates over all selected constructors and waits
     *            until their dependencies become available in the context. This value become the iterations amount -
     *            after the iterations, the context will either try its best to create new instances of objects using
     *            reflection or throw an exception.
     * @see #setFailIfUnknownType(boolean) */
    void setIterationsAmount(int iterations);

    /** @return the amount of iterations before the context gives up on providing constructor dependencies. */
    int getIterationsAmount();

    /** @return if true, this context is able to extract parameter names. Note that as of Java 8, method parameter names
     *         can be include in class sources using -parameters compiler flag. */
    boolean isParameterAware();

    /** @param process if true, providers (including singleton providers and factory method wrappers) will be mapped by
     *            their whole class tree, including super types and all implemented interface. While very convenient,
     *            this also generates a lot of meta-data and can be avoided by setting this value to false. Defaults to
     *            true. */
    void setMapSuperTypes(boolean process);

    /** @return if true, providers (including singleton providers and factory method wrappers) will be mapped by their
     *         whole class tree, including super types and all implemented interface. While very convenient, this also
     *         generates a lot of meta-data. Defaults to true. */
    boolean isMapSuperTypes();

    /** @param classTree providers of this class will be removed. If {@link #isMapSuperTypes() super types mapping} is
     *            enabled, provides the whole class tree and all implemented interfaces will be removed.
     * @see #remove(Class) */
    void clear(Class<?> classTree);

    /** Removes all providers. */
    void clear();
}
