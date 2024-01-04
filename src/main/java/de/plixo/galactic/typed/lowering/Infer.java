package de.plixo.galactic.typed.lowering;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.MethodCollection;
import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.*;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static de.plixo.galactic.exception.FlairKind.*;

/**
 * The Infer stage is responsible for inferring types, methods and fields. It's the second stage of Expression parsing
 */
public class Infer implements Tree<Context, Type> {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new FlairException(STR."Expression of type \{expression.getClass()
                .getSimpleName()} not implemented for Infer stage");
    }


    @Override
    public Expression parseAssign(AssignExpression expression, Context context,
                                  @Nullable Type hint) {
        var left = parse(expression.left(), context, null);
        var value = parse(expression.right(), context, left.getType(context));
        if (left instanceof FieldExpression fieldExpression) {
            if (fieldExpression.field().isStatic()) {
                throw new FlairCheckException(left.region(), FORMAT,
                        STR."Cannot access a static field \{fieldExpression.field()
                                .name()} on an object");
            }
            return new PutFieldExpression(expression.region(), fieldExpression.field(),
                    fieldExpression.object(), value);
        } else {
            throw new FlairCheckException(left.region(), FORMAT,
                    STR."Cannot assign to \{left.getClass().getSimpleName()}");
        }
    }

    @Override
    public Expression parsePutStaticExpression(PutStaticFieldExpression expression, Context context,
                                               @Nullable Type hint) {
        return new PutStaticFieldExpression(expression.region(), expression.field(),
                expression.value());
    }

    @Override
    public Expression parseLocalVariableAssign(LocalVariableAssign expression, Context context,
                                               @Nullable Type hint) {
        var parsed = parse(expression.expression(), context, expression.variable().getType());
        return new LocalVariableAssign(expression.region(), expression.variable(), parsed);
    }

    @Override
    public Expression parseStaticMethodExpression(StaticMethodExpression expression,
                                                  Context context, @Nullable Type hint) {
        return expression;
    }

    @Override
    public Expression parseStaticFieldExpression(StaticFieldExpression expression, Context context,
                                                 @Nullable Type hint) {
        return expression;
    }

    @Override
    public Expression parseStellaClassConstructExpression(StellaClassConstructExpression expression,
                                                          Context context, @Nullable Type hint) {
        var region = expression.region();

        var constructType = expression.constructType();
        var constructors = constructType.getMethods("<init>", context);
        var accessFilter = constructors.filter(ref -> ref.isPublic() && !ref.isStatic());
        var owningFilter = accessFilter.filter(
                ref -> ref.owner().equals(new MethodOwner.ClassOwner(constructType)));
        var sizeFilter =
                owningFilter.filter(ref -> ref.arguments().size() == expression.arguments().size());
        if (sizeFilter.isEmpty()) {
            throw new FlairCheckException(region, SIGNATURE,
                    STR."Constructor not found on \{constructType.name()}");
        }
        var expressions = expression.arguments().iterator();
        var method = sizeFilter.methods().getFirst().arguments().iterator();
        var parsedArguments = new ArrayList<Expression>();
        while (expressions.hasNext()) {
            var currentExpression = expressions.next();
            var currentType = method.next();
            parsedArguments.add(parse(currentExpression, context, currentType));
        }

        var types = parsedArguments.stream().map(ref -> ref.getType(context)).toList();
        var signature = new Signature(new VoidType(), types);
        var bestMatch = sizeFilter.findBestMatch(signature, context);

        if (bestMatch == null) {
            throw new FlairCheckException(region, SIGNATURE,
                    STR."Constructor not found on \{constructType.name()}");
        }
        return new InstanceCreationExpression(region, bestMatch, constructType, parsedArguments);
    }

    @Override
    public Expression parseBlockExpression(BlockExpression expression, Context context,
                                           @Nullable Type hint) {
        var expressions = expression.expressions().iterator();
        var parsedExpressions = new ArrayList<Expression>();
        while (expressions.hasNext()) {
            var next = expressions.next();
            var isLast = !expressions.hasNext();
            parsedExpressions.add(parse(next, context, isLast ? hint : null));
        }
        return new BlockExpression(expression.region(), parsedExpressions);
    }

    @Override
    public Expression parseBranchExpression(BranchExpression expression, Context context,
                                            @Nullable Type hint) {
        var parsedCondition = parse(expression.condition(), context, PrimitiveType.BOOLEAN);
        var typeHint = expression.elseExpression() == null ? null : hint;
        var parsedThen = parse(expression.then(), context, typeHint);
        Expression parsedElse = null;
        if (expression.elseExpression() != null) {
            parsedElse = parse(expression.elseExpression(), context, typeHint);
        }
        return new BranchExpression(expression.region(), parsedCondition, parsedThen, parsedElse);
    }

    @Override
    public Expression parseCallNotation(CallNotation expression, Context context,
                                        @Nullable Type hint) {
        var parsed = parse(expression.object(), context, null);

        if (parsed instanceof ExtensionBitExpression(
                var region, var staticMethodExpression, var object
        )) {
//            parsed = new StaticMethodExpression(region, staticMethodExpression.owner(),
//                    staticMethodExpression.methods().map(ref -> {
//                        var types = new ArrayList<Type>();
//                        types.add(object.getType(context));
//                        types.addAll(ref.arguments());
//                        return new Method(ref.modifier(), ref.name(), ref.returnType(), types,
//                                ref.owner());
//                    }));
            parsed = staticMethodExpression;
            var argsPlus = new ArrayList<Expression>();
            argsPlus.add(object);
            argsPlus.addAll(expression.arguments());
            expression = new CallNotation(region, parsed, argsPlus);
        }

        if (parsed instanceof MethodCallExpression.MethodSource source) {
            var argsSize = expression.arguments().size();
            var sizeFilter = source.methods().filter(ref -> ref.arguments().size() == argsSize);
            if (sizeFilter.isEmpty()) {
                throw new FlairCheckException(parsed.region(), SIGNATURE,
                        STR."Method with \{argsSize} types not found");
            }
            var parsedArguments = new ArrayList<Expression>();
            var expressions = expression.arguments().iterator();
            var method = sizeFilter.methods().getFirst().arguments().iterator();
            while (expressions.hasNext()) {
                var currentType = method.next();
                var currentExpression = expressions.next();
                parsedArguments.add(parse(currentExpression, context, currentType));
            }

            var types = parsedArguments.stream().map(ref -> ref.getType(context)).toList();
            var signature = new Signature(null, types);
            var bestMatch = sizeFilter.findBestMatch(signature, context);
            if (bestMatch == null) {
                var searched = sizeFilter.methods().stream().map(Method::getDescriptor).toList();
                throw new FlairCheckException(parsed.region(), SIGNATURE,
                        STR."Method with types \{types} not found. Searched: \{searched}");
            }
            return new MethodCallExpression(parsed.region(), source, bestMatch,
                    source.getCallType(context), parsedArguments);
        } else {
            throw new FlairCheckException(parsed.region(), UNEXPECTED_TYPE,
                    "Can only call methods");
        }
    }

    @Override
    public Expression parseMethodCallExpression(MethodCallExpression expression, Context context,
                                                Type hint) {
        return expression;
    }

    @Override
    public Expression parseStringExpression(StringExpression expression, Context context,
                                            @Nullable Type hint) {
        return expression;
    }

    @Override
    public Expression parseBooleanExpression(BooleanExpression expression, Context context,
                                             @Nullable Type hint) {
        return expression;
    }

    @Override
    public Expression parseNumberExpression(NumberExpression expression, Context context,
                                            @Nullable Type hint) {
        return expression;
    }

    @Override
    public Expression parseVarExpression(VarExpression expression, Context context,
                                         @Nullable Type hint) {
        if (expression.variable() instanceof Scope.ClosureVariable closureVariable) {
            var field = closureVariable.field();
            var object = new VarExpression(expression.region(), closureVariable.fieldOwner());
            return new FieldExpression(expression.region(), object, closureVariable.owner(), field);
        }
        return expression;
    }

    @Override
    public Expression parseCastExpression(CastExpression expression, Context context,
                                          @Nullable Type hint) {
        var parsed = parse(expression.object(), context, null);
        return new CastExpression(expression.region(), parsed, expression.type());
    }

    @Override
    public Expression parseCastCheckExpression(CastCheckExpression expression, Context context,
                                               @Nullable Type hint) {
        var parsed = parse(expression.object(), context, null);
        return new CastCheckExpression(expression.region(), parsed, expression.type());
    }


    @Override
    public Expression parseVarDefExpression(VarDefExpression expression, Context context,
                                            @Nullable Type hint) {
        var parsed = parse(expression.expression(), context, expression.hint());

        var variable = expression.variable();
        if (variable == null) {
            throw new FlairException("Variable not found");
        }
        if (expression.hint() != null) {
            variable.setType(expression.hint());
        } else {
            variable.setType(parsed.getType(context));
        }


        return new VarDefExpression(expression.region(), expression.name(), expression.hint(),
                parsed, variable);
    }

    @Override
    public Expression parseThisExpression(ThisExpression thisExpression, Context context,
                                          Type hint) {
        return thisExpression;
    }

    @Override
    public Expression parseDotNotation(DotNotation expression, Context context,
                                       @Nullable Type hint) {
        var parsed = parse(expression.object(), context, null);

        var region = expression.region();
        var id = expression.id();
        var parsedType = parsed.getType(context);
        if (parsedType instanceof Class aClass) {
            var dotNotation = aClass.getDotNotation(region, parsed, id, context);
            if (dotNotation != null) {
                return dotNotation;
            }
        }
        var symbolExpression = context.unit().getImportedStaticMethod(id);
        if (!symbolExpression.isEmpty()) {
            var possibleMethods = symbolExpression.stream().filter(ref -> {
                assert ref.isStatic();
                var thisContext = ref.thisContext();
                if (thisContext == null) {
                    return false;
                }
                return Type.isAssignableFrom(thisContext, parsedType, context);
            }).toList();

            if (possibleMethods.isEmpty()) {
                throw new FlairCheckException(region, UNEXPECTED_TYPE,
                        STR."Method \{id} is not an Extension for type \{parsedType}");
            }
            var collection = new MethodCollection(id, possibleMethods.stream().map(
                    StellaMethod::asMethod).toList());
            var methodExpression =
                    new StaticMethodExpression(region, collection);
            return new ExtensionBitExpression(region, methodExpression, parsed);
        }
        throw new FlairCheckException(region, UNEXPECTED_TYPE,
                STR."Symbol \{id} not found on Object from class \{parsedType}");
    }
}
