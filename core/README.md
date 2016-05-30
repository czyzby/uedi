# UEDI Core

Implements interfaces provided by the UEDI API. Provides default `Context` implementation. Does not feature automatic class scanning - use more one of the specialized libraries to enable automatic scanning. However, it does feature `FixedClassScanner`, which allows to mock class scanning by providing a fixed collection of classes that will be included and analyzed. Uses standard Java reflection API, which should be supported by every major JVM implementation.

### Dependency

Gradle dependency:
```
  compile "com.github.czyzby:uedi-core:$uediVersion"
```

## Usage

If for whatever reason you don't need or want automatic class scanning, a `Context` instance can be easily obtained like this:

```
Context context = DependencyInjection.newContext(new FixedClassScanner(SomeClass.class,
    OtherComponentClass.class));
```

All the classes that you list in the `FixedClassScanner` constructor will be processed when the `scan(Class<?> root)` context method is called. This approach can be also used to mock `ClassScanner`, which is pretty helpful during testing.

Note that if you require automatic class scanning, [some implementations](../scanner) are already provided.
