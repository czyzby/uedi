# UEDI API

This library defines *API* of UEDI, *without* providing any implementations (or even abstract or utility classes) whatsoever. It basically defines how UEDI should act, what are the supported component types and what should be possible. The project comes with two test suites - one for contexts unaware of method parameters, the other for parameter-aware contexts - which must be passed in order to call the API implementation complete.

### Dependency

While most UEDI libraries already reference this library, here's the Gradle dependency in case you need it:
```
  compile "com.github.czyzby:uedi-api:$uediVersion"
```

Note that `uedi-api` is GWT-compatible. This is the GWT module inherit:
```
  <inherits name="com.github.czyzby.uedi.Uedi" />
```

## Implementations

There are two official API implementations:
- [`uedi-core`](../core): the default, full implementation of the API. Should work on every JVM supporting Java 6+.
- [`gdx-uedi`](https://github.com/czyzby/gdx-lml/tree/master/uedi): uses LibGDX reflection abstraction to make sure that the implementation works on every targeted LibGDX platform, even GWT, Android and iOS. Somewhat limited: does not support mapping the components by their interfaces (as the API lacks interface queries), but injection by interface is still supported thanks to providers and factories.
