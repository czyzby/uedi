package com.github.czyzby.uedi.scanner;

/** Common interface for class scanners that allow to find classes implementing certain interfaces.
 *
 * @author MJ */
public interface ClassScanner {
    /** @param root class scanning should start from package of this class.
     * @param interfaces all classes implementing at least one of these interfaces should be found.
     * @return all classes implementing at least one of passed interfaces in the selected package tree. None of the
     *         classes should be abstract or an interface. */
    Iterable<Class<?>> getClassesImplementing(Class<?> root, Class<?>... interfaces);
}
