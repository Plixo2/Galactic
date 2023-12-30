"Typed" is the main phase of the parser, turning the high level representation into a typed representation. (see [Parsing]
(parsing/TIRExpressionParsing.java))
The symbols and types are resolved in different steps (lowering). (see [Symbols](lowering/Symbols.java)/[Infer](lowering/Infer.java))
The last step is to check the types and classes.  (see [Check](lowering/Check.java))
