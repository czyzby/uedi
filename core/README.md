# UEDI Core

Implements interfaces provided by the UEDI API. Provides default `Context` implementation. Does not feature automatic class scanning - use more one of the specialized libraries to enable automatic scanning. However, it does feature `FixedClassScanner`, which allows to mock class scanning by providing a fixed collection of classes that will be included and analyzed.