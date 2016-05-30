# UEDI Standard Scanner

Uses [fast-classpath-scanner](https://github.com/lukehutch/fast-classpath-scanner) to scan for the components. Does not rely on reflection to analyze the classes - it processes the Java byte code (the `class` files) instead, preventing from unnecessary class loading.

### Dependency

Gradle dependency:
```
  compile "com.github.czyzby:uedi:$uediVersion"
  compile "com.github.czyzby:uedi-core:$uediVersion"
```

Note that `uedi-core` library is included as an implementation of the `uedi-api`. `uedi` is just an automatic class scanner implementation.

## Usage

Creation of an instance of `Context` with automatic classpath scanning:

```
Context context = DependencyInjection.newContext(new DefaultClassScanner());
```
