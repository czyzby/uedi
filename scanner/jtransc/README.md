# UEDI JTransc Scanner

Uses internal [JTransc](https://github.com/jtransc/jtransc/) API to scan available classes.

### Dependency

Gradle dependency:
```
  compile "com.github.czyzby:uedi-jtransc:$uediVersion"
```

Note that `uedi-jtransc` includes only the automatic `ClassScanner` implementation and does **not** depend on `uedi-core`, which actually implements the `uedi-api`. The standard `uedi-core` might *just work* on JTransc (which claims to support Java reflection).

## Usage

Creation of an instance of `ClassScanner` which automatically finds UEDI components:

```
ClassScanner classScanner = new JTranscClassScanner();
```
