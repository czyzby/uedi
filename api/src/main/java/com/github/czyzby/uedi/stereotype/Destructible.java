package com.github.czyzby.uedi.stereotype;

/** Allows to hook up destruction methods to existing components. Useful for closing resources. The destruction order is
 * guaranteed to be preserved among all components when they are destroyed en masse.
 *
 * @author MJ
 * @see com.github.czyzby.uedi.Context#destroy()
 * @see Initiated */
public interface Destructible {
    /** @return order of destruction method, which allows to control the order of component destruction. If there are
     *         other components destroyed at the same time (through context destruction, for example), destruction
     *         methods will be invoked in ascending order. */
    int getDestructionOrder();

    /** Will be invoked when the component is requested to be destroyed or the whole context is being destroyed.
     *
     * @throws Exception will be rethrown as a {@link RuntimeException} during context destruction. */
    void destroy() throws Exception;
}
