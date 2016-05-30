# UEDI GWT Scanner

Compatible with Java 6 and GWT 2.6.1. Provides `GwtClassScanner`, which uses custom source generator to prepare a pool of classes compatible with UEDI. Note that it performs class scanning at *compile time*, which should significantly reduce meta-data and scanning overhead at runtime.

Make sure to include this property in your GWT definition file:
```
  <set-configuration-property name="uedi.root" value="your.root.package" />
```

The scanner would work even without it, but adding your root package allows to filter the classpath at compile time - resulting in even less overhead at runtime.

Note that this scanner provides **ONLY** the class instances. GWT still lacks reflection implementation, which needs to be provided by a third-party library (like *LibGDX*).

### Dependency

Gradle dependency:
```
  compile "com.github.czyzby:uedi-gwt:$uediVersion"
```

Since GWT transpiles *Java* code into *JavaScript*, library sources are also required:

```
  compile "com.github.czyzby:uedi-gwt:$uediVersion:sources"
  compile "com.github.czyzby:uedi-api:$uediVersion:sources"
```

Make sure to include a GWT inherit and property in your module definition:
```
  <inherits name="com.github.czyzby.uedi.UediGwt" />
  <set-configuration-property name="uedi.root" value="your.root.package" />
```

`java.lang.reflect.Member` interface emulation is included for compatability.

GWT does **not** support reflection by default, so `uedi-core` dependency will not work out of the box without a third-party library emulating reflection API.

## Usage

Creating a GWT-compatible automatic class scanner:

```
ClassScanner classScanner = new GwtClassScanner();
```
