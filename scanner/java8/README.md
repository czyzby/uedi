# UEDI Java 8

UEDI enhanced with Java 8 features. Thanks to the new `Parameter` class from reflection API, constructor and method parameter names can now be extracted and processed to resolve ambiguous dependencies. This makes it even more convenient to drop annotations. The application must be compiled with `-parameters` flag, though.

Use `ExtendedInjection` instead of `DependencyInjection` to quickly construct parameter-aware `Context` instances.

### Dependency

Gradle dependency:
```
  compile "com.github.czyzby:uedi-java8:$uediVersion"
```

You should also include the additional compiler flag:
```
compileJava {
  options.compilerArgs << '-parameters'
}
```

Note that `uedi-java8` already comes with the `uedi-core` library - an implementation of the `uedi-api`. `Context` instances provided by this library are parameter-aware.

## Usage

Creation of an instance of parameter-aware `Context` with automatic classpath scanning:

```
Context context = ExtendedInjection.newContext();
```
