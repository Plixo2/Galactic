package de.plixo.tir.building;

import com.google.common.collect.Streams;
import de.plixo.hir.expr.*;
import de.plixo.tir.expr.*;
import de.plixo.tir.scoping.Scope;
import de.plixo.tir.tree.Unit;
import de.plixo.typesys.types.FunctionType;
import de.plixo.typesys.types.Primitive;
import de.plixo.typesys.types.StructImplementation;
import de.plixo.typesys.types.Type;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class ExpressionBuilder {

    //the type hint is just a hint where you could get type infos, doesn't have to match with the
    // returned type
    public static Expr build(HIRExpr expr, Type hint, Scope scope, Unit unit) {

        return switch (expr) {
            case HIRAssign hirAssign -> throw new NullPointerException("TODO");
            case HIRBinOp hirBinOp -> throw new NullPointerException("TODO");
            case HIRBlock hirBlock -> buildBlock(hirBlock, hint, scope, unit);
            case HIRBranch hirBranch -> throw new NullPointerException("TODO");
            case HIRConstant hirConstant -> new ConstantExpr(hirConstant.constant);
            case HIRDefinition hirDefinition -> buildDefinition(hirDefinition, hint, scope, unit);
            case HIRField hirField -> buildField(hirField, hint, scope, unit);
            case HIRFunction hirFunction -> buildFunction(hirFunction, hint, scope, unit);
            case HIRIdentifier hirIdentifier -> buildIdentifier(hirIdentifier, hint, scope, unit);
            case HIRIndexed hirIndexed -> throw new NullPointerException("TODO");
            case HIRInvoked hirInvoked -> buildCall(hirInvoked, hint, scope, unit);
            case HIRUnary hirUnary -> throw new NullPointerException("TODO");
        };
    }

    private static FieldExpr buildField(HIRField hirField, @Nullable Type hint, Scope scope,
                                        Unit unit) {
        var expr = build(hirField.object(), hint, scope, unit);
        var type = Objects.requireNonNull(expr.getType());
        var name = hirField.name();
        if (type instanceof StructImplementation implementation) {
            var implType = implementation.get(name);
            if (implType == null) {
                throw new NullPointerException(
                        "Cant find field " + name + " in " + implementation.struct.absolutName());
            }
            return new FieldExpr(expr,name,implementation,implType);
        } else {
            throw new NullPointerException(
                    "Cant get field in " + type);
        }
    }

    private static Expr buildCall(HIRInvoked hirInvoked, @Nullable Type hint, Scope scope,
                                  Unit unit) {
        var calling = ExpressionBuilder.build(hirInvoked.function, hint, scope, unit);
        if (calling instanceof PathExpr.StructPathExpr structInvoking) {
            var structure = structInvoking.structure();
            if (hirInvoked.arguments.size() != structure.fields().size()) {
                throw new NullPointerException("Incompatible num arguments");
            }
            var implementation = new StructImplementation(structure);
            //todo use implementation inside zip, and resolve
            var types =
                    Streams.zip(hirInvoked.arguments.stream(), structure.fields().values().stream(),
                                    (hirExpr, type) -> ExpressionBuilder.build(hirExpr, type, scope, unit))
                            .toList();
            return new ConstructExpr(structure, types, implementation);
        } else {
            //get via type
            throw new NullPointerException("TODO");
        }
    }


    private static Expr buildIdentifier(HIRIdentifier hirIdentifier, @Nullable Type hint,
                                        Scope scope, Unit unit) {
        var id = hirIdentifier.name();
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

        Type typedHint = Primitive.VOID;
        if (varDefinition.typehint() != null) {
            typedHint = TypeBuilder.build(varDefinition.typehint(), unit);
        }
        var expr = ExpressionBuilder.build(varDefinition.expression(), typedHint, scope, unit);
        //TODO typ check


        var variable = new Scope.Variable(varDefinition.name(), typedHint);

        scope.addVariable(variable);
        return new DefinitionExpr(variable);
    }

    /**
     * Bad code lol
     */
    private static FunctionExpr buildFunction(HIRFunction hirFunction, @Nullable Type hint,
                                              Scope scope, Unit unit) {

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
                    types.add(TypeBuilder.build(ref.typeHint(), unit));
                }
                case VALUE, TYPE_VALUE -> {
                    throw new NullPointerException("TODO");
                }
            }
        });

        Type returnType = null;
        if (hirFunction.returnType != null) {
            returnType = TypeBuilder.build(hirFunction.returnType, unit);
        }
        if (hint instanceof FunctionType functionType) {
            if (returnType == null) {
                returnType = functionType.returnType();
            }
            if (functionType.arguments().size() == types.size()) {
                for (int i = 0; i < functionType.arguments().size(); i++) {
                    var type = functionType.arguments().get(i);
                    if (types.get(i) == null) {
                        types.set(i, type);
                    }
                }
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

        var subScope = new Scope(scope, returnType);
        var functionExpr = new FunctionExpr(subScope, returnType, args);
        functionExpr.setBody(ExpressionBuilder.build(hirFunction.body, returnType, scope, unit));
        return functionExpr;
    }

    private static BlockExpr buildBlock(HIRBlock block, @Nullable Type hint, Scope scope,
                                        Unit unit) {
        var subScope = new Scope(scope, hint);
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
}
