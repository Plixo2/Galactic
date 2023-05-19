package de.plixo.atic.tir.building;

import com.google.common.collect.Streams;
import de.plixo.atic.common.Constant;
import de.plixo.atic.hir.expr.*;
import de.plixo.atic.tir.expr.*;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.typing.TypeQuery;
import de.plixo.atic.typing.types.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpressionBuilder {

    //the type hint is just a hint where you could get type infos, doesn't have to match with the
    // returned type
    public static Expr build(HIRExpr expr, Type hint, Scope scope, Unit unit) {

        return switch (expr) {
            case HIRAssign hirAssign -> buildAssign(hirAssign, hint, scope, unit);
            case HIRBinOp hirBinOp -> buildBinOp(hirBinOp, hint, scope, unit);
            case HIRBlock hirBlock -> buildBlock(hirBlock, hint, scope, unit);
            case HIRBranch hirBranch -> buildBranch(hirBranch, hint, scope, unit);
            case HIRConstant hirConstant -> new ConstantExpr(hirConstant.constant());
            case HIRDefinition hirDefinition -> buildDefinition(hirDefinition, hint, scope, unit);
            case HIRField hirField -> buildField(hirField, hint, scope, unit);
            case HIRFunction hirFunction -> buildFunction(hirFunction, hint, scope, unit, false);
            case HIRIdentifier hirIdentifier -> buildIdentifier(hirIdentifier, hint, scope, unit);
            case HIRIndexed hirIndexed -> throw new NullPointerException("not impl");
            case HIRInvoked hirInvoked -> buildCall(hirInvoked, hint, scope, unit);
            case HIRUnary hirUnary -> buildUnary(hirUnary, hint, scope, unit);
            case HIRReturn hirReturn -> buildReturn(hirReturn, hint,scope,unit);
        };
    }
    private static ReturnExpr buildReturn(HIRReturn hirReturn, @Nullable Type hint, Scope scope,
                                  Unit unit) {
        Expr value = null;
        if (hirReturn.object() != null) {
            value = ExpressionBuilder.build(hirReturn.object(),hint,scope,unit);
        }
        var returnExpr = new ReturnExpr(value);
        new TypeQuery(returnExpr.getType(), scope.returnType()).assertEquality();
        return returnExpr;
    }
    private static Expr buildAssign(HIRAssign assign, @Nullable Type hint, Scope scope, Unit unit) {
        var object = ExpressionBuilder.build(assign.object(), hint, scope, unit);
        if (object instanceof VariableExpr variableExpr) {
            var value =
                    ExpressionBuilder.build(assign.value(), variableExpr.getType(), scope, unit);
            new TypeQuery(value.getType(), variableExpr.getType()).assertEquality();
            return new VarAssignExpr(value, variableExpr.variable());
        } else if (object instanceof FieldExpr expr) {
            var structExpr = ExpressionBuilder.build(assign.value(), expr.getType(), scope, unit);
            var fieldType = expr.fieldType();
            new TypeQuery(fieldType, structExpr.getType()).assertEquality();
            var typeQuery = new TypeQuery(expr.structImplementation(), scope.owner());
            if (!typeQuery.test()) {
                throw new NullPointerException(
                        "can only reassign a owned value. current owner: " + scope.owner());
            } else {
                typeQuery.mutate();
            }
            return new FieldAssignExpr(structExpr, expr.structure(), expr.field(), fieldType,
                    expr.structImplementation());
        } else {
            throw new NullPointerException("only assign local variables or fields");
        }
    }

    private static UnaryExpr buildUnary(HIRUnary hirUnary, @Nullable Type hint, Scope scope,
                                        Unit unit) {
        var type = switch (hirUnary.operator()) {
            case SUBTRACT -> Primitive.FLOAT;
            case LOGIC_NEGATE -> Primitive.BOOL;
            default -> hint;
        };
        var expr = ExpressionBuilder.build(hirUnary.left(), type, scope, unit);
        //validate
        expr.getType();
        return new UnaryExpr(expr, hirUnary.operator());
    }

    private static BranchExpr buildBranch(HIRBranch hirBranch, @Nullable Type hint, Scope scope,
                                          Unit unit) {
        var expr = ExpressionBuilder.build(hirBranch.condition, Primitive.BOOL, scope, unit);
        new TypeQuery(expr.getType(), Primitive.BOOL).assertEquality();
        var bodyScope = new Scope(scope, scope.returnType(), scope.owner(), scope.scopeLevel());
        var body = ExpressionBuilder.build(hirBranch.body, hint, bodyScope, unit);
        Expr elseBody = null;
        Scope elseBodyScope = null;
        if (hirBranch.elseBody != null) {
            elseBodyScope = new Scope(scope, scope.returnType(), scope.owner(), scope.scopeLevel());
            elseBody = ExpressionBuilder.build(hirBranch.elseBody, body.getType(), elseBodyScope,
                    unit);
        }
        return new BranchExpr(expr, body, bodyScope, elseBody, elseBodyScope);
    }

    private static Expr buildBinOp(HIRBinOp hirBin, @Nullable Type hint, Scope scope, Unit unit) {
        var left = ExpressionBuilder.build(hirBin.left(), hint, scope, unit);
        var right = ExpressionBuilder.build(hirBin.right(), hint, scope, unit);
        var binExpr = new BinExpr(left, right, hirBin.operator());

        var type = left.getType();
        var rightType = right.getType();
        if (new TypeQuery(type, Primitive.FLOAT).test() ||
                new TypeQuery(type, Primitive.INT).test()) {
            new TypeQuery(rightType, type).assertEquality();
            var allowed = switch (binExpr.operator()) {
                case ADD, NOT_EQUALS, MULTIPLY, DIVIDE, EQUALS, SMALLER_EQUALS, SMALLER, GREATER, GREATER_EQUALS, SUBTRACT ->
                        true;
                case AND, LOGIC_NEGATE, OR -> false;
            };
            if (!allowed) {
                throw new NullPointerException(
                        binExpr.operator() + " is here not allowed float -" + " float");
            }
        } else if (new TypeQuery(type, Primitive.BOOL).test()) {
            new TypeQuery(rightType, Primitive.BOOL).assertEquality();
            var allowed = switch (binExpr.operator()) {
                case AND, EQUALS, NOT_EQUALS, LOGIC_NEGATE, OR -> true;
                case ADD, MULTIPLY, DIVIDE, SMALLER_EQUALS, SMALLER, GREATER, GREATER_EQUALS, SUBTRACT ->
                        false;
            };
            if (!allowed) {
                throw new NullPointerException(
                        binExpr.operator() + " is here not allowed bool - " + "bool");
            }
        } else if (type instanceof StructImplementation implementation) {
            var funcName = binExpr.operator().name().toLowerCase();
            var implType = implementation.get(funcName);
            if (implType == null) {
                throw new NullPointerException("Cant find field " + funcName + " in " +
                        implementation.struct().absolutName());
            }
            var fieldExpr = new FieldExpr(left, funcName, implementation, implType, implementation);
            if (implType instanceof FunctionType functionType) {
                if (functionType.arguments().size() != 1) {
                    throw new NullPointerException("Incompatible num arguments");
                }
                new TypeQuery(functionType.arguments().get(0), rightType).assertEquality();
                return new CallExpr(fieldExpr, functionType, List.of(right),
                        functionType.getReturnType());
            } else {
                throw new NullPointerException(
                        "Cant only call the overloaded operator " + funcName + " in " +
                                implementation.struct().absolutName());
            }
        } else {
            throw new NullPointerException("cant perform binary expressions for " + type);
        }
        //performs testing
        //binExpr.operator().checkAndGetType(type, rightType);
        return binExpr;
    }

    private static Expr buildField(HIRField hirField, @Nullable Type hint, Scope scope, Unit unit) {
        var expr = build(hirField.object(), hint, scope, unit);
        var type = Objects.requireNonNull(expr.getType());
        var name = hirField.name();
        if (type instanceof StructImplementation implementation) {
            var implType = implementation.get(name);
            if (implType == null) {
                throw new NullPointerException(
                        "Cant find field " + name + " in " + implementation.struct().absolutName());
            }
            return new FieldExpr(expr, name, implementation, implType, implementation);
        } else {
           // var constant = unit.findConstant(name);
//            if (constant.type() instanceof FunctionType functionType) {
             //    return new ConstantRefExpr(constant);
//            } else {
//            }
            throw new NullPointerException("Cant get field in " + type);
        }
    }

    private static Expr buildCall(HIRInvoked hirInvoked, @Nullable Type hint, Scope scope,
                                  Unit unit) {
        var calling = ExpressionBuilder.build(hirInvoked.function(), hint, scope, unit);
        if (calling instanceof PathExpr.StructPathExpr structInvoking) {
            var structure = structInvoking.structure();
            var uninitialized = structure.uninitialized();
            if (hirInvoked.arguments().size() != uninitialized.size()) {
                throw new NullPointerException("Incompatible num arguments");
            }
            var implementation = new StructImplementation(structure);
            for (GenericType generic : structure.generics()) {
                implementation.implement(generic, new SolvableType());
            }
            //todo use implementation inside zip, and resolve
            var types = Streams.zip(hirInvoked.arguments().stream(),
                    uninitialized.stream(), (hirExpr, type) -> {
                        var implType = implementation.get(type.name());
                        var expr = ExpressionBuilder.build(hirExpr, implType, scope, unit);
                        var typeQuery = new TypeQuery(expr.getType(), implType);
                        typeQuery.assertEquality();
                        return expr;
                    }).toList();

            return new ConstructExpr(structure, types, implementation);
        } else {
            var exprType = calling.getType();
            if (exprType instanceof FunctionType functionType) {
                if (functionType.arguments().size() != hirInvoked.arguments().size()) {
                    throw new NullPointerException("Incompatible num arguments");
                }
                var types = Streams.zip(hirInvoked.arguments().stream(),
                        functionType.arguments().stream(), (hirExpr, type) -> {
                            var expr = ExpressionBuilder.build(hirExpr, type, scope, unit);
                            new TypeQuery(expr.getType(), type).assertEquality();
                            return expr;
                        }).toList();
                return new CallExpr(calling, functionType, types, functionType.getReturnType());
            } else {
                throw new NullPointerException("Cant call a " + exprType);
            }

        }
    }


    private static Expr buildIdentifier(HIRIdentifier hirIdentifier, @Nullable Type hint,
                                        Scope scope, Unit unit) {
        var id = hirIdentifier.name();
        if (id.equals("false")) {
            return new ConstantExpr(new Constant.BoolConstant(false));
        } else if (id.equals("true")) {
            return new ConstantExpr(new Constant.BoolConstant(true));
        }

        var variable = scope.get(id);
        if (variable != null) {
            return new VariableExpr(variable);
        } else {
            var constant = unit.findConstant(id);
            if (constant != null) {
                return new ConstantRefExpr(constant);
            }
            var path = unit.findPath(id);
            if (path != null) {
                return path;
            }
            throw new NullPointerException("cant find " + id);
        }
    }

    private static DefinitionExpr buildDefinition(HIRDefinition definition, @Nullable Type hint,
                                                  Scope scope, Unit unit) {

        var varDefinition = definition.definition();

        Type type = null;
        if (varDefinition.typehint() != null) {
            type = TypeBuilder.build(varDefinition.typehint(), unit, null,
                    TypeBuilder.GenericCollection.empty());
        }
        var expr = ExpressionBuilder.build(varDefinition.expression(), type, scope, unit);
//        if (!new TypeQuery(typedHint, Primitive.VOID).test()) {
        if (type != null) {
            var typeQuery = new TypeQuery(expr.getType(), type);
            typeQuery.assertEquality();
        }
        type = expr.getType();
//        }
        //TODO typ check or code flow analysis


        var variable = new Scope.Variable(varDefinition.name(), type, Scope.AllocationType.STACK,
                Scope.DefineType.DYNAMIC);

        scope.addVariable(variable);
        System.out.println("Defined " + varDefinition.name() + " with type " + type);
        return new DefinitionExpr(variable, expr);
    }

    /**
     * Bad code lol
     */
    public static FunctionExpr buildFunction(HIRFunction hirFunction, @Nullable Type hint,
                                             Scope scope, Unit unit, boolean constructShellOnly) {

        var names = new ArrayList<String>();
        var types = new ArrayList<Type>();

        hirFunction.arguments.forEach(ref -> {
            switch (ref.defineType()) {
                case NOTHING -> {
                    names.add(ref.name());
                    types.add(null);
                }
                case TYPE -> {
                    assert ref.typeHint() != null;
                    names.add(ref.name());
                    types.add(TypeBuilder.build(ref.typeHint(), unit, null,
                            TypeBuilder.GenericCollection.empty()));
                }
                case VALUE, TYPE_VALUE -> throw new NullPointerException("TODO");
            }
        });

        Type returnType = null;
        Type owner = null;
        if (hirFunction.owner != null) {
            owner = TypeBuilder.build(hirFunction.owner, unit, null,
                    TypeBuilder.GenericCollection.empty());
        }
        if (hirFunction.returnType != null) {
            returnType = TypeBuilder.build(hirFunction.returnType, unit, null,
                    TypeBuilder.GenericCollection.empty());
        }
        if (hint instanceof FunctionType functionType) {
            if (returnType == null) {
                returnType = functionType.getReturnType();
            }
            if (functionType.arguments().size() == types.size()) {
                for (int i = 0; i < functionType.arguments().size(); i++) {
                    var type = functionType.arguments().get(i);
                    if (types.get(i) == null) {
                        types.set(i, type);
                    }
                }
            }
            if (owner == null) {
                owner = functionType.owner();
            }
        }

        if (returnType == null) {
            returnType = Primitive.VOID;
        }
        var args = Streams.zip(names.stream(), types.stream(), (n, t) -> {
            if (t == null) {
                throw new NullPointerException("missing type information for " + n);
            }
            return new FunctionExpr.Variable(n, t);
        }).toList();

        if (owner == null) {
            owner = Primitive.VOID;
        }
        var subScope = new Scope(scope, returnType, owner, scope.scopeLevel() + 1);
        var functionExpr = new FunctionExpr(subScope, returnType, args, owner);
        if (!constructShellOnly) {
            subScope.overwriteVariable(new Scope.Variable("self", owner, Scope.AllocationType.SELF,
                    Scope.DefineType.FINAL));
            functionExpr.arguments().forEach(ref -> subScope.addVariable(
                    new Scope.Variable(ref.name(), ref.type(), Scope.AllocationType.INPUT,
                            Scope.DefineType.FINAL)));


            var body = ExpressionBuilder.build(hirFunction.body, returnType, subScope, unit);
            testReturnStatements(body);
            functionExpr.setBody(body);

            var exprReturn = body.getType();
            var typeQuery = new TypeQuery(returnType, exprReturn);
            if (!typeQuery.test()) {
                if (!new TypeQuery(Primitive.VOID, returnType).test()) {
                    throw new NullPointerException(returnType + "->" + exprReturn);
                }
            } else {
                typeQuery.mutate();
            }
        }


        return functionExpr;
    }

    private static BlockExpr buildBlock(HIRBlock block, @Nullable Type hint, Scope scope,
                                        Unit unit) {
        var subScope = new Scope(scope, hint, scope.owner(), scope.scopeLevel());
        var list = new ArrayList<Expr>();
        var statements = block.statements.iterator();
        while (statements.hasNext()) {
            var statement = statements.next();
            Type suggested = Primitive.VOID;
            if (!statements.hasNext()) {
                suggested = hint;
            }
            list.add(ExpressionBuilder.build(statement, suggested, subScope, unit));
        }
        return new BlockExpr(list, subScope);
    }

    private static boolean testReturnStatements(Expr expr) {
        return switch (expr) {
            case BinExpr binExpr -> false;
            case BlockExpr blockExpr -> {
                var iterator = blockExpr.expressions().iterator();
                while (iterator.hasNext()) {
                    var subExpr = iterator.next();
                    boolean isLast = !iterator.hasNext();
                    if (testReturnStatements(subExpr)) {
                        if (isLast) {
                           yield true;
                        } else {
                            throw new NullPointerException("statements does return early");
                        }
                    }
                }
                yield false;
            }
            case BranchExpr branchExpr -> {
                if (branchExpr.elseBody() != null) {
                    yield testReturnStatements(branchExpr.elseBody()) && testReturnStatements(branchExpr.body());
                }
                yield false;
            }
            case CallExpr callExpr -> false;
            case ConstantExpr constantExpr -> false;
            case ConstantRefExpr constantRefExpr -> false;
            case ConstructExpr constructExpr -> false;
            case DefinitionExpr definitionExpr -> false;
            case FieldAssignExpr fieldAssignExpr -> false;
            case FieldExpr fieldExpr -> false;
            case FunctionExpr functionExpr -> false;
            case ReturnExpr returnExpr -> true;
            case UnaryExpr unaryExpr -> false;
            case VarAssignExpr varAssignExpr -> false;
            case VariableExpr variableExpr -> false;
            case PathExpr.PackagePathExpr packagePathExpr -> false;
            case PathExpr.StructPathExpr structPathExpr -> false;
            case PathExpr.UnitPathExpr unitPathExpr -> false;
        };
    }
}
