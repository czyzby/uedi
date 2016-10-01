# Unsettlingly Easy Dependency Injection

*If you happen to waste your time reinventing the wheel, at least make it freaking square.*

Since you probably already noticed which language dominates on this repository (*hint: Java*) and read the "dependency injection", part you're probably asking yourself "why". That is a very good question, and this a very silly answer: *because I can*.

Inspired by a [Stack Overflow question](https://stackoverflow.com/questions/35447966/why-do-java-ioc-frameworks-favour-annotation-based-injections) asking why Java IoC/DI frameworks favor annotations, I started wondering if a simple, robust DI framework could be usable without the whole annotation boilerplate. Since I had a free evening, I decided to test it out - and it turns out it ***can***. Say hello to **Unsettingly Easy Dependency Injection framework**, the land of POJOs and no annotations.

## How does it work

The rules are pretty simple:

- There are four main types of components: 
  - `Singleton`: interface with no methods. Ensures that there is only one instance of the selected class in the context.
  - `Provider`: forces to select the provided class and implement method that returns its instance. Allows the context to supply objects other than singletons.
  - `Factory`: interface with no methods. All of the public (non-native, non-static) methods of classes implementing `Factory` are converted into `Providers`. Arguments of the methods are automatically injected by the context when an instance supplied by the factory is requested. If the argument's type is `Object` or matches the type of object that requested the injection, the instance that invoked the factory will be injected. While very convenient, factories are reflection-based (contrary to `Providers`), so there's some slight runtime overhead.
  - `Property`: forces implementing `Map.Entry` interface. Allows to manage and inject `String` objects.
- Component scan is automatic. Pass a root class and all types implementing the mentioned interfaces in its (sub)package(s) will be found and initiated.
- To have more control over the context creation, you can optionally implement any of these additional interfaces:
  - `Named`: allows to choose the exact ID of the component. If no ID is provided, normally class name (converted to `lowerCamelCase`) is used.
  - `Initiated`: allows to invoke additional actions after the object is fully initiated. Think of this as an [initialization block](http://docs.oracle.com/javase/tutorial/java/javaOO/initial.html), executed when all object's fields are processed and filled. You can also choose initiation priority to control the exact order of initiations (honored among all components during scanning).
  - `Destructible`: similarly to `Initiated`, this interface allows to control the destruction of the context and hook up additional methods that are invoked *en masse* when the context is destroyed. (This about resources closing.) Be careful though, as context will keep references to `Destructible` objects that it created unless `destroy(Destructible)` is called.
  - `Default`: providers and factories implementing this interface will be chosen as the default values when resolving ambiguous references without appropriate names. You have to be careful to set only a *single* default provider/factory/singleton as the default one for the selected type, as they can override each other.
- Component scanning begins with finding the classes implementing any of the component interfaces; abstract classes are ignored. Then context tries to create instances of the found classes using the first available constructor (resolving its parameter dependencies). Depending on the settings, if all objects cannot be constructed after some iterations, context will either give up and create dependencies using reflection (assuming there will never be any providers for these classes in the context) or fail and throw an exception (assuming there are circular dependencies). Then components fields are injected with values provided by the context (singletons and results of providers and factories). Then initiation methods are sorted by their order and invoked. And that's it.
- `Providers` (including singleton and factory providers) are mapped to their whole class tree and all interfaces. So, for example, if your factory method returns `ArrayList`, it will be also used to provide values injected into `List`-type fields. This is very convenient, as you don't have to know the exact implementation of injected values or explicitly map providers to chosen classes.
- To classify field as injectable, it must meet certain conditions:
  - It cannot store primitive value.
  - If `Property` injection is turned off, the field cannot store `String` value.
  - It cannot be filled. If not `null` value is assigned to the field, it is assumed that it was already injected by the constructor or was explicitly initiated and works as it should.
  - It cannot be `static` or `transient`. (Even more ignored modifiers can be set with type filters and signatures. For example, you could ignore all package-private fields, limiting boilerplate to minimum. See `Context` API.)
- If all conditions are met, the value will be injected from the context. If multiple providers are mapped to the same class, field's name must match ID (`Named`'s name or `lowerCameCase` class name) of the property/provider/singleton/factory method. When using Java 8 library version, named method and constructor parameters can be used to resolve type collisions if the `-parameters` compiler flag is applied.

## Blah, blah, samples

How does a world without annotations look? Certainly more readable. There are some examples of the same functionalities implemented using pseudo-code API of an imaginary framework based on pretty much every other Java DI mechanism, compared to similar UEDI snippets:

- Simple injection:

```Java
// The Java way:
@Component
public class SomeComponent {
  private static final String IMPORTANT = "OK";

  private final ConstructorDependency constructorDependency;
  @Inject private CircularDependency circularDependency;

  private float someRandomPrimitiveValue;
  private SomeType someRandomNulledContainer;
  private final SomeType someRandomInitiatedContainer = new SomeType();
  
  @Inject
  public SomeComponent(ConstructorDependency constructorDependency) {
    this.constructorDependency = constructorDependency;
  }
}

// The UEDI way:
public class SomeComponent implements Singleton {
  private static final String IMPORTANT = "OK";

  private final ConstructorDependency constructorDependency;
  private CircularDependency circularDependency;

  private float someRandomPrimitiveValue;
  private transient SomeType someRandomNulledContainer;
  private final SomeType someRandomInitiatedContainer = new SomeType();
  
  public SomeComponent(ConstructorDependency constructorDependency) {
    this.constructorDependency = constructorDependency;
  }
}
```

- Factories (assuming automatic component scanning and no awkward registration phase in the Java way - which happens more than often):
```Java
// The Java way:
@Provider
public class SomeFactory {
  /** @param someConsumer requested SomeType instance. */
  @Inject
  @Provider
  public SomeType getSomeType(@Inject SomeDependency dependency, @Consumer SomeConsumer someConsumer) {
    return new SomeType(dependency, extractSomeData(someConsumer));
  }
  
  private String extractSomeData(SomeConsumer someConsumer) {
    return someConsumer.getSomeData();
  }
}

// The UEDI way:
public class SomeFactory implements Factory {
  /** @param someConsumer requested SomeType instance. */
  public SomeType getSomeType(SomeDependency dependency, SomeConsumer someConsumer) {
    return new SomeType(dependency, extractSomeData(someConsumer));
  }
  
  private String extractSomeData(SomeConsumer someConsumer) {
    return someConsumer.getSomeData();  // Private methods are not converted to providers.
  }
}
```

- Ambiguous field injections:

```Java
public interface Ambiguous {
}

// The Java way:
@Component
@Named("one")
public class One implements Ambiguous {
}
@Component
@Named("two")
public class Two implements Ambiguous {
}
@Component
public class Consumer {
  @Inject @Named("one") private Ambiguous one;
  @Inject @Named("two") private Ambiguous two;
}

// The UEDI way:
public class Three implements Ambiguous, Named, Singleton {
  public String getName() { return "three"; }
}
public class Four implements Ambiguous, Named, Singleton {
  public String getName() { return "four"; }
}
public class Consumer implements Singleton {
  private Ambiguous three;
  private Ambiguous four;
}
```

- Ambiguous method/constructor injections:
```Java
// The Java way:
@Provider
public class SomeFactory {
  @Provider
  @Named("one")
  public Ambiguous getOne() {
    return new One();
  }
  @Provider
  @Named("two")
  public Ambiguous getTwo() {
    return new Two();
  }
}
@Component
public class Consumer {
  @Inject
  public Consumer(@Named("one") Ambiguous one, @Named("two") Ambiguous two) {
  }
}

// The UEDI way (Java 8):
public class SomeFactory implements Factory {
  public Ambiguous three() {
    return new Three();
  }
  public Ambiguous getFour() {
    return new Four();
  }
}
public class Consumer implements Singleton {
  public Consumer(Ambiguous three, Ambiguous four) {
  }
}
```

- Scopes, criteria, lazy injections, dinosaurs:

OK, I give up.

### The obvious bias

Yes, I know, there are valid reasons to use annotations. And examples might be exaggerated: with the new Java 8 `Parameter` API, we'll see less and less annotations in DI frameworks. This was just a small project of one man: a proof of concept and a somewhat valuable experience. That took about a day of work or so.

I *will* be happy to help with the API or improve some features if anyone decides to use it, though.

## FAQ

> How to get started?

```Java
Context context = DependencyInjection.newContext(classScanner);
context.scan(Root.class);
context.get(SomeComponent.class).doSomethingImportant();
```

> Is it really unsettlingly easy?

Provided that you feel comfortable with Java - yes. Most of your components will be *plain old Java objects* with an extra interface or two.

> Is it tested?

There are multiple unit tests, yes. It wasn't tried out in a bigger application yet though. I'm eager to try it out with some other lightweight Java frameworks that could use simple dependency injection support - like [Vert.x](http://vertx.io/).

> Is it reflection-based?

Yes. And no. You can choose to register components manually (avoiding component scan overhead) and prefer non-reflection-based `Providers` over `Factories` if you really want to. While you *do* need reflection to set up the context with *automatic* component scan, the cost is usually minimal and worth the benefits - and once you're up and running, usually no reflection will be used at all. UEDI just makes it easier to connect and initiate your class structure and tries to be as little invasive as possible.

> Is it documented?

You've already read most of the official documentation, I'm afraid. Although it seems that 100% of public methods have javadocs, so there's also that.

> Is it fast?

Probably. When using singletons and reflectionless (is that even a word?) providers, getting a value from the context comes down to one read from a hash map and invocation of one provider method. Factories - while convenient - provide component instances using reflection, so there should be some little overhead unfortunately. Probably not noticeable though (especially on modern JVMs), unless you create thousands of objects. Scanning the context might take some time, depending on how big it is, but then again - it should still take a fraction of second. To be honest, most other functionalities are just plain old Java. I'd say this is basically as fast as runtime dependency injection gets - just because it does not come packed with all these fancy features and avoids a lot of additional operations and checks thanks to that.

> Why should I use UEDI over (...)?

You probably should not, but then again - can you resist letting go of the annotations boilerplate? I guess it's safe to say that every popular DI framework gives more possibilities (which their scopes, criteria, lazy injections, annotations parameters and whatnot), but using POJOs has a certain charm.

> Well, is there any reason to use UEDI at all?

It's lightweight, tiny, straightforward, less verbose than anything I know of and requires little to no setting up and learning. You basically have to memorize about 8 interfaces (half of which you're likely to use rarely) and remember to mark nulled non-primitive fields with `transient`, so they won't be injected. That's it. UEDI *just works*.

> Is it prone to refactoring and obfuscation?

If there are ambiguous providers - more than I'd like to admit. Since ambiguous dependencies are resolved using fields names (or methods/constructors parameters), refactoring those might affect application behavior. Using `Default` values and avoiding injection of ambiguous dependencies solves this issue, while keeping the application "resistant" to refactoring. For the most part.

> What if you forget `volatile` keyword?

What if you forget `@Inject`, `@Named`, `@Scope` or some criteria annotation? Bugs happen.

> What if I need the `volatile` keyword for its original purpose, but still want to use UEDI?

The default Java serialization seems to be used rarely than ever these days (not counting the legacy code), as solutions like Kryo are faster and libraries like Jackson are usually more convenient for modern applications. However, you can modify the way fields are filtered using the `setFieldsIgnoreFilter(int)` and `setFieldsIgnoreSignature(int)`. Changing the first will cause all fields with the chosen modifiers to be rejected - so by setting this value to `0` (no filters) or `Modifier.STATIC` (only static fields), you can turn on injection to `transient` fields. Then you can modify the ignored field signature with the second method - I suggest changing it to `Modifier.TRANSIENT` (or `0`) , so only package-private `transient` (or not) fields are ignored. This is a very pleasant DSL.
```
private transient SomeService willBeInjectedWithTheSettingsIMentionedButWillStillNotBeSerialized;
// If you went with Modifier.TRANSIENT signature:
transient SomeValue startsAsNullButWillNotBeInjectedAsItIsTransientAndPackagePrivate;
// If you went with 0 signature:
SomeValue startsAsNullAndWillBeSerializedButWillNotBeInjectedAsItIsPackagePrivate;
```

> Any example projects?

Look through the [unit tests](api/src/test/java/com/github/czyzby/uedi), they should give you an idea of how to manage UEDI context.

### Dependencies

Library dependencies using Gradle syntax:
- `"com.github.czyzby:uedi-api:$uediVersion"`: contains only the necessary interfaces.
- `"com.github.czyzby:uedi-core:$uediVersion"`: base for all other libraries. Default implementation of `uedi-api`.
- `"com.github.czyzby:uedi-fallback:$uediVersion"`: Java 6-compatible. Provides `FallbackClassScanner`. Uses crude reflection-based scanner to find components. Use when absolutely necessary.
- `"com.github.czyzby:uedi:$uediVersion"`: default library if you don't have access to Java 8. Provides `DefaultClassScanner`. Java 7-compatible. Uses [fast-classpath-scanner](https://github.com/lukehutch/fast-classpath-scanner) to scan for components without reflection.
- `"com.github.czyzby:uedi-java8:$uediVersion"`: adds supports for Java 8 features. Provides `StandardClassScanner`. Uses [fast-classpath-scanner](https://github.com/lukehutch/fast-classpath-scanner). Thanks to `-parameters` compiler flag, you're able to resolve ambiguous dependencies in constructors and methods. Features highly scalable non-blocking collections in the concurrent context variant. This is NOT implementation-agnostic: this library depends directly on `uedi-core` and cannot be used with LibGDX UEDI implementation, for example.
- `"com.github.czyzby:uedi-android:$uediVersion"`: implements `AndroidClassScanner`, which uses "native" API to go through available classes.
- `"com.github.czyzby:uedi-jtransc:$uediVersion"`: implements `JTranscClassScanner`, which uses "native" API to go through available classes.

Use whichever library matches your targeted Java version or platform. Go through each library project for additional data (in their `README` files).

See [gdx-uedi](https://github.com/czyzby/gdx-lml/tree/master/uedi) for LibGDX-specific `uedi-api` implementation that works on every targeted LibGDX platform.

#### License

Dedicated to public domain.

#### Working with the sources

```
$ git clone https://github.com/czyzby/uedi.git
   [If using Eclipse:]
$ gradle eclipse
   [If using IntelliJ:]
$ gradle idea
   [Import to your IDE of choice.]

   [Pushing to Maven Local:]
$ gradle installAll
```
