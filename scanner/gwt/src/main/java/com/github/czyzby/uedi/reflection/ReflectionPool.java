package com.github.czyzby.uedi.reflection;

/** Reflection-aware object that allows to access all classes (relevant for UEDI) available in the GWT reflection pool.
 * Default implementation is auto-generated.
 *
 * @author MJ */
public interface ReflectionPool {
    /** @return all classes available in GWT reflection pool implementing one of UEDI scanned interfaces. */
    Class<?>[] getReflectedClasses();
}