package com.github.czyzby.uedi.stereotype;

/** Should be implemented by all classes that must have only one instance in the current context. Classes implementing
 * this interface are scanned for and fully initiated: their non-primitive, empty, non-transient fields are injected.
 * The first available constructor is chosen; constructors can have any arguments, as constructor injections are
 * supported (except for the circular ones).
 *
 * @author MJ */
public interface Singleton {
}
