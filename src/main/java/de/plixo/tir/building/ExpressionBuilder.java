package de.plixo.tir.building;

import com.google.common.collect.Streams;
import de.plixo.hir.expr.*;
import de.plixo.tir.expr.*;
import de.plixo.tir.scoping.Scope;
import de.plixo.tir.tree.Unit;
import de.plixo.typesys.types.FunctionType;
import de.plixo.typesys.types.Primitive;
import de.plixo.typesys.types.Type;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ExpressionBuilder {

    public static Expr build(HIRExpr expr, Type hint, Scope scope, Unit unit) {

        return switch (expr) {
            case HIRAssign hirAssign -> throw new NullPointerException("TODO");
            case HIRBinOp hirBinOp -> throw new NullPointerException("TODO");
            case HIRBlock hirBlock -> buildBlock(hirBlock, hint, scope, unit);
            case HIRBranch hirBranch -> throw new NullPointerException("TODO");
            case HIRConstant hirConstant -> new ConstantExpr(hirConstant.constant);
            case HIRDefinition hirDefinition -> buildDefinition(hirDefinition, hint, scope, unit);
            case HIRField hirField -> throw new NullPointerException("TODO");
            case HIRFunction hirFunction -> buildFunction(hirFunction, hint, scope, unit);
            case HIRIdentifier hirIdentifier -> buildIdentifier(hirIdentifier, hint, scope, unit);
            case HIRIndexed hirIndexed -> throw new NullPointerException("TODO");
            case HIRInvoked hirInvoked -> throw new NullPointerException("TODO");
            case HIRUnary hirUnary -> throw new NullPointerException("TODO");
        };
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
                return new ConstRefExpr(constant);
            }
            throw new NullPointerException("TODO");
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
