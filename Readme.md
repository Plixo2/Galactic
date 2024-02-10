
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

The Main class of the projects compiles the File in `/resources/standalone/HelloWorld.stella` and builds it to `/resources/build.jar`.
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
    println("Hello World!")
}
```
Output:
```cmd
Hello World!
```


