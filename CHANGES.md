## 0.2

Currently available as snapshot release.

**+** Automatic class scanning for Android (`uedi-android`), [JTransc](http://jtransc.com/) (`uedi-jtransc`) and GWT (`uedi-gwt`). Note that GWT scanner does **NOT** provide reflection implementation, it simply allows to find UEDI components in the classpath.

**+** `PropertyProvider` API extracted to `StringProvider` interface.

**+** `Context.addDestructible(Destructible)`.

**+** `uedi-core` tests with `FixedClassScanner`, mocking automatic classpath scanning with a fixed pool of classes.

**=** Fixed `FixedClassScanner`.

**-** `uedi-fallback` and `uedi` now do not depend on `uedi-core` (which contains `Context` implementation). They depend on `uedi-api` (which contains only the interfaces) and provide `ClassScanner` implementations. `uedi-core` has to be explicitly marked as a dependency when using these scanners.

## 0.1

Initial library version.

**+** Support for `Singleton`, `Provider`, `Property` and `Factory` component annotations.

**+** Support for `Initiated`, `Destructible`, `Named` and `Default` component setting annotations.

**+** Initial `Context` implementation. 

**+** Automatic class scanning for Java 6 (`uedi-fallback`, based on reflection), 7 (`uedi`, analyzes bytecode) and 8 (`uedi-java8`, analyzes bytecode, resolves ambiguous dependencies in method/constructor parameters).
