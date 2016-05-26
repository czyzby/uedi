package com.github.czyzby.uedi.stereotype;

/** Allows to hook up initiation methods to existing components. On contrary to constructors, initiation methods are
 * invoked when the components are fully filled, with all their fields injected. If the component is pretty complex and
 * requires additional setup after the complete injection or it relies on full initiation of other components, it should
 * implement this method.
 *
 * <p>
 * While all components created using the context can implement this interface and will be properly initiated, the
 * initiation order is guaranteed to be preserved only in case of classes that are scanned for and initiated together
 * (singletons, providers, factories, properties).
 *
 * @author MJ
 * @see Destructible */
public interface Initiated {
    /** @return order of initiation method, which allows to control the order of component initiation. If there are
     *         other components created at the same time (through component scanning, for example), initiation methods
     *         will be invoked in ascending order. */
    int getInitiationOrder();

    /** Will be invoked when the component is constructed.
     *
     * @throws Exception will be rethrown as a {@link RuntimeException} during context initiation. */
    void initiate() throws Exception;
}
