# Flow protocol of the Compiler 

Tokens for Tokenizer
```
<WHITESPACE>
<EOF>
"->"
"=>"
"!="
"=="
"<="
">="
"||"
"&&"
'{'
'}'
'['
']'
'('
')'
'.'
';'
','
':'
'='
'+'
'-'
'/'
'*'
'!'
'<'
'>'
'@'
'#'
"class"
"interface"
"void"
"int"
"byte"
"short"
"long"
"float"
"double"
"boolean"
"char"
"fn"
"if"
"else"
"new"
"extends"
"implements"
"return"
"var"
"import"
<word>
<string>
<number>
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
HIRAssign
```

build classes and function shells

//TODO fill steps here

HIR to Expression
- converts HIR to Expression
- links generics (TODO)
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
AssignExpression
```

Symbol Stage:
- resolving variables/variable references
- class/unit/package paths 
- static fields and method calls 
- true/false

```
ConstructExpression -> StellaClassConstructExpression
StringExpression
NumberExpression
DotNotation -> DotNotation, StaticFieldExpression, StaticMethodExpression,
               (StellaPackageExpression, StellaClassExpression, UnitExpression -> StaticMethodExpression, StaticFieldExpression)
StellaClassExpression -> StaticMethodExpression, StaticFieldExpression
CallNotation
BranchExpression
BlockExpression
VarDefExpression
SymbolExpression-> BooleanExpression, VarExpression, 
                    (StellaClassExpression, StellaPackageExpression, UnitExpression -> StaticMethodExpression, StaticFieldExpression)
AssignExpression -> LocalVariableAssign
```

Infer Stage: 
- type inference
- solve fields and methods
- some type checking
- in this stage every expression has to have a valid type that 
  can be called using `.getType`

//TODO call super methods

```
StringExpression
NumberExpression
LocalVariableAssign
DotNotation -> GetFieldExpression, GetMethodExpression
CallNotation -> MethodCallExpression
GetMethodExpression -> MethodCallExpression
BranchExpression
BlockExpression
VarDefExpression
BooleanExpression 
VarExpression 
StaticFieldExpression
StaticMethodExpression
StellaClassConstructExpression -> InstanceCreationExpression
```


Check Stage:
- check types
- check void types
- TODO tests if fields, methods and classes are accessible
- TODO check duplicate fields, methods and classes
- TODO check duplicate imports 
- TODO check if `void` is used as a non-returning type

```
StringExpression
LocalVariableAssign
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
