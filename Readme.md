
<h1 align="center">Galactic Compiler</h1>

<details>
<summary>Internals</summary>

* Main Entry File [Universe](src/main/java/de/plixo/galactic/Universe.java)
* [Lexer](src/main/java/de/plixo/galactic/lexer)
* [Parser](src/main/java/de/plixo/galactic/parsing)
* [High Level](src/main/java/de/plixo/galactic/high_level)
* [Typed](src/main/java/de/plixo/galactic/typed)
* [Code Generation](src/main/java/de/plixo/galactic/codegen)

</details>

## Quick Start

This project requires Java 21 (preview) or higher to run.

Open the project as a Gradle project and run the [Main](src/main/java/de/plixo/galactic/Main.java) 
class with `--enable-preview` as vm flag.

The Main class compiles the File in `/resources/standalone/HelloWorld.stella` and builds it to `/resources/build.jar`.
See the [Config](resources/config.toml) file, to configure the build process.


This jar can be executed with `java -jar build.jar` and will print `Hello World!` to the console.

Folders can also be opened. The Module Systems works like in java, so you can import classes from other files and packages. 
 

> [!Note]
> Anonymous function are not on main yet, generics are not implemented and there is no way of calling the super methods yet.


## Examples:
For more examples see the [tests](resources/tests) folder, or view the standard libraries in the [library](resources/library) folder.

### Hello World
`Main.stella`
```
import @java String java.lang.String

fn main(args: [String]) = {
    ~"Hello World!"

    print("args: ")
    #for var i = 0, i < args.length, i = i + 1 {
        print(args.get(i) & " ")
    }
    println()
}
```
Output:
```cmd
Hello World!
args: ...
```

### How this works:

#### Extensions
The `~"Hello World!"` is syntactic sugar for `("Hello World!").bitComplement()`.
The `bitComplement` function is an extension method defined in the [Core](resources/library/Core.stella) library, which is automatically 
imported:

```
fn bitComplement() -> void extends Object = {
    println(this)
}
```

Extensions are also the reason you can call `println()` on every object, or test for null with `isNull()`.

#### Macros
The `#for` loop is a macro, which is expanded to a while loop at compile time.
The Macros can use their own grammar, building on the existing Language, to parse Expressions and convert them back into tokens.
As a User you cant define your own macros yet, but the standard library uses them to provide a more convenient syntax.