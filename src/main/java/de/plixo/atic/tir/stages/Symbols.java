package de.plixo.atic.tir.stages;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.Scope;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.expressions.*;
import de.plixo.atic.tir.path.Package;
import de.plixo.atic.tir.path.Unit;

public class Symbols implements Tree<Context> {


    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new NullPointerException(
                "Expression of type " + expression.getClass().getSimpleName() +
                        " not implemented for Symbol stage");
    }

    @Override
    public Expression parseAssign(AssignExpression expression, Context context) {
        var left = parse(expression.left(), context);
        var value = parse(expression.right(), context);
        if (left instanceof VarExpression varExpression) {
            return new LocalVariableAssign(varExpression.variable(), value);
        } else {
            throw new NullPointerException("not supported yet");
        }
    }

    @Override
    public Expression parseConstructExpression(ConstructExpression expression, Context context) {

        var parsed = expression.arguments().stream().map(ref -> {
            context.pushScope();
            var parse = parse(ref, context);
            context.popScope();
            return parse;
        }).toList();

        //TODO: Change from getType to direct test
        var type = expression.getType(context);
        if (type instanceof AticClass aticClass) {
            return new AticClassConstructExpression(aticClass, parsed);
        } else {
            throw new NullPointerException("not supported yet");
        }
    }

    @Override
    public Expression parseStringExpression(StringExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseNumberExpression(NumberExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseBooleanExpression(BooleanExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseDotNotation(DotNotation expression, Context context) {
        context.pushScope();
        var parsed = parse(expression.object(), context);
        context.popScope();

        var id = expression.id();
        return switch (parsed) {
            case UnitExpression unitExpression -> {
                var unit = unitExpression.unit();
                var pathElement = unit.locate(id);
                yield switch (pathElement) {
                    case Unit subUnit -> new UnitExpression(subUnit);
                    case Package aPackage -> new AticPackageExpression(aPackage);
                    case AticClass aClass -> new AticClassExpression(aClass);
                    case null, default -> {
                        throw new NullPointerException(
                                "Symbol " + id + " not found on unit " + unit.name());
                    }
                };
            }
            case AticPackageExpression packageExpression -> {
                var thePackage = packageExpression.thePackage();
                var pathElement = thePackage.locate(id);
                yield switch (pathElement) {
                    case Unit unit -> new UnitExpression(unit);
                    case Package aPackage -> new AticPackageExpression(aPackage);
                    case AticClass aClass -> new AticClassExpression(aClass);
                    case null, default -> {
                        throw new NullPointerException(
                                "Symbol " + id + " not found on package " + thePackage.name());
                    }
                };
            }
            case AticClassExpression aticClassExpression -> {
                var aticClass = aticClassExpression.theClass();
                var possibleField = aticClass.getField(id, context);
                if (possibleField != null) {
                    yield new StaticFieldExpression(aticClass, possibleField);
                }
                var possibleMethods = aticClass.getMethods(id, context);
                if (!possibleMethods.isEmpty()) {
                    yield new StaticMethodExpression(aticClass, possibleMethods);
                }
                throw new NullPointerException("Symbol " + id + " not found on class " + aticClass);
            }
            default -> new DotNotation(parsed, id);
        };
    }

    @Override
    public Expression parseCallNotation(CallNotation expression, Context context) {
        context.pushScope();
        var parsed = parse(expression.object(), context);
        context.popScope();

        var arguments = expression.arguments().stream().map(ref -> {
            context.pushScope();
            var parse = parse(ref, context);
            context.popScope();
            return parse;
        }).toList();
        return new CallNotation(parsed, arguments);
    }

    @Override
    public Expression parseBranchExpression(BranchExpression expression, Context context) {
        context.pushScope();
        var parsedCondition = parse(expression.condition(), context);
        context.popScope();


        context.pushScope(new Scope(context.scope()));
        var parsedThen = parse(expression.then(), context);
        context.popScope();
        Expression parsedElse = null;
        if (expression.elseExpression() != null) {
            context.pushScope(new Scope(context.scope()));
            parsedElse = parse(expression.elseExpression(), context);
            context.popScope();
        }
        return new BranchExpression(parsedCondition, parsedThen, parsedElse);
    }


    @Override
    public Expression parseBlockExpression(BlockExpression blockExpression, Context context) {
        context.pushScope();
        var parsed =
                blockExpression.expressions().stream().map(ref -> parse(ref, context)).toList();
        context.popScope();
        return new BlockExpression(parsed);
    }

    @Override
    public Expression parseVarDefExpression(VarDefExpression varDefExpression, Context context) {
        var scope = context.scope();
        var name = varDefExpression.name();
        var existingVariable = scope.getVariable(name);
        if (existingVariable != null) {
            throw new NullPointerException("Variable " + name + " already exists");
        }
        if (!isValidVariableName(name, context)) {
            throw new NullPointerException("Variable " + name + " is not a valid name");
        }
        var variable = new Scope.Variable(name, 0, null, null);
        scope.addVariable(variable);
        context.pushScope();
        var parsed = parse(varDefExpression.expression(), context);
        context.popScope();
        return new VarDefExpression(name, varDefExpression.hint(), parsed, variable);
    }

    @Override
    public Expression parseSymbolExpression(SymbolExpression expression, Context context) {
        var id = expression.id();
        if (id.equals("true") || id.equals("false")) {
            return new BooleanExpression(Boolean.parseBoolean(id));
        }
        var variable = context.scope().getVariable(id);
        if (variable != null) {
            return new VarExpression(variable);
        } else {
            var pathElement = context.locate(id);
            return switch (pathElement) {
                case Unit unit -> new UnitExpression(unit);
                case Package aPackage -> new AticPackageExpression(aPackage);
                case AticClass aClass -> new AticClassExpression(aClass);
                case null, default -> {
                    var aClass = context.locateImported(id);
                    if (aClass != null) {
                        yield new AticClassExpression(aClass);
                    }
                    throw new NullPointerException("Symbol " + id + " not found");
                }
            };
        }
    }

    private static boolean isValidVariableName(String name, Context context) {
        return !name.equals("true") && !name.equals("false") && context.locate(name) == null;
    }
}
