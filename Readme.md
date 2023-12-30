
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

This Project requires Java 21 (Preview) or higher to run.

Open the Project as a Gradle project and run the [Main](src/main/java/de/plixo/galactic/Main.java) 
class with `--enable-preview` as vm flag.

The Main class opens the Project in `/resource/project` and builds it to `/resource/build.jar`.
This jar can be executed with `java -jar build.jar` and will print `Hello World!` to the console.


## Examples:

### Hello World
`Core.stella`
```
import @java System java.lang.System

fn println(msg: java.lang.String) -> void = {
    System.out.println(msg)
}
```

`Main.stella`
```
import * Core

import @java String java.lang.String
import @java Consumer java.util.function.Consumer
import @java Function java.util.function.Function
import @java Object java.lang.Object
import @java ArrayList java.util.ArrayList
import @java String java.lang.String

fn main(args: [String]) -> void = {
    var list = new ArrayList
    list.add("Hello")
    list.add("World!")
    list.forEach(fn(obj: Object) -> void implements Consumer = {
        println(obj as String)
    })
}
```
Output:
```cmd
Hello
World!
```

### Class Example:
```
import * Core

import @java Consumer java.util.function.Consumer
import @java Object java.lang.Object
import @java Integer java.lang.Integer
import @java String java.lang.String

fn test() -> void = {
    var c = new StringLengthComparator { 100 }
    println(String.valueOf(c.compare("Hell0", "World")))
}

class StringLengthComparator implements java.util.Comparator {
    someField: int
    fn compare(s1: Object, s2: Object) -> int = {
        var s1String = s1 as String
        var s2String = s2 as String
        Integer.compare(s1String.length(), s2String.length())
    }
}
```
Output:
```cmd
-1
```
