
<h1 align="center">Galactic Compiler</h1>



* Main Entry File [Universe](src/main/java/de/plixo/galactic/Universe.java)
* [Lexer](src/main/java/de/plixo/galactic/lexer/Readme.md)
* [Parser](src/main/java/de/plixo/galactic/parsing/Readme.md)
* [High Level](src/main/java/de/plixo/galactic/high_level/Readme.md)
* [Typed](src/main/java/de/plixo/galactic/typed/Readme.md)
* [Code Generation](src/main/java/de/plixo/galactic/codegen/Readme.md)


# Examples:

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
        println(String.valueOf(obj))
    })
}

```

