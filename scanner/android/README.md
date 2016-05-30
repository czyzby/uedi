# UEDI Android Scanner

Uses "native" Android API to scan for the components. Relies on reflection to scan the classes, but I'm afraid that's the only (reliable) way of doing this on that platform.

### Dependency

Gradle dependency:
```
  compile "com.github.czyzby:uedi-android:$uediVersion"
  compile "com.github.czyzby:uedi-core:$uediVersion"
```

Note that `uedi-core` library is included as an implementation of the `uedi-api`. `uedi-android` is just an automatic class scanner implementation.

## Usage

Creation of an instance of `Context` with automatic classpath scanning:

```
ApplicationInfo applicationInfo = getApplicationInfo(); // TODO Get an instance of AI.
Context context = DependencyInjection.newContext(new AndroidClassScanner(applicationInfo));
```
