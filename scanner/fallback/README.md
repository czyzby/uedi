# UEDI Fallback Scanner

Compatible with Java 6. Use only when necessary: the other solutions are likely to be faster, as they do not rely purely on reflection and do not load all checked classes thanks to their byte code analysis.

### Dependency

Gradle dependency:
```
  compile "com.github.czyzby:uedi-fallback:$uediVersion"
  compile "com.github.czyzby:uedi-core:$uediVersion"
```

Note that `uedi-core` library is included as an implementation of the `uedi-api`. `uedi-fallback` is just an automatic class scanner implementation.

## Usage

Creation of an instance of `Context` with automatic classpath scanning:

```
Context context = DependencyInjection.newContext(new FallbackClassScanner());
```
