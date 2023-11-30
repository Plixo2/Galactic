# Flow protocol of the Compiler 

Tokens for Tokenizer
```
COMMENT
WHITESPACE
USE
CLASS
INTERFACE
VOID
INT
BYTE
SHORT
LONG
FLOAT
DOUBLE
BOOLEAN
CHAR
FUNCTION
IF
NEW
ELSE
EXTENDS
IMPLEMENTS
RETURN
VAR
NATIVE
ASSIGN_ARROW
SINGLE_EXPR_ARROW
PARENTHESES_O
PARENTHESES_C
BRACES_O
BRACES_C
BRACKET_O
BRACKET_C
SEPARATOR
DOT
COLON
SEMICOLON
NON_EQUALS
EQUALS
AT
SMALLER_EQUALS
GREATER_EQUALS
OR
AND
NOT
PLUS
MINUS
MUL
DIV
GREATER
SMALLER
HASH
NUMBER
STRING
KEYWORD
ASSIGN
END_OF_FILE
```
Grammar for Parser inside resources/cfg.txt

HIR Stage from AST Nodes
```
HIRBlock
HIRBranch
HIRCallNotation
HIRConstruct
HIRDotNotation
HIRIdentifier
HIRNumber
HIRString
HIRVarDefinition
```

build classes and function shells


HIR to Expression
- converts HIR to Expression
- resolves generics (TODO)
```
ConstructExpression
StringExpression
BranchExpression
VarDefExpression
NumberExpression
DotNotation
CallNotation
SymbolExpression
BlockExpression
```

Symbol Stage:
- resolving variables/variable references
- class/unit/package paths 
- static fields and method calls 
- true/false

```
ConstructExpression -> AticClassConstructExpression
StringExpression
NumberExpression
DotNotation -> DotNotation, StaticFieldExpression, StaticMethodExpression,
               (AticPackageExpression, AticClassExpression, UnitExpression -> StaticMethodExpression, StaticFieldExpression)

CallNotation
BranchExpression
BlockExpression
VarDefExpression
SymbolExpression-> BooleanExpression, VarExpression, 
                    (AticClassExpression, AticPackageExpression, UnitExpression -> StaticMethodExpression, StaticFieldExpression)
```

Infer Stage: 
- type inference
- solve fields and methods
- some type checking
- in this stage every expression has to have a valid type that 
  can be called using `.getType`


```
StringExpression
NumberExpression
DotNotation -> GetFieldExpression, GetMethodExpression
CallNotation -> MethodCallExpression
GetMethodExpression -> MethodCallExpression, GetMethodExpression
BranchExpression
BlockExpression
VarDefExpression
BooleanExpression 
VarExpression 
StaticFieldExpression
StaticMethodExpression
AticClassConstructExpression -> InstanceCreationExpression
```


Check Stage:
- check types
- check void types
- TODO tests if fields, methods and classes are accessible

```
StringExpression
NumberExpression
BooleanExpression
GetFieldExpression
MethodCallExpression
BranchExpression
BlockExpression
VarDefExpression
VarExpression
StaticFieldExpression
InstanceCreationExpression
```

CheckFlow Stage:
- check return flow 
