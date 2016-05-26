# UEDI Standard Scanner

Uses [fast-classpath-scanner](https://github.com/lukehutch/fast-classpath-scanner) to scan for the components. Does not rely on reflection to analyze the classes - it processes the Java byte code (the `class` files) instead, preventing from unnecessary class loading.
