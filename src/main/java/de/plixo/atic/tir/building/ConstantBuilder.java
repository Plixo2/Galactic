package de.plixo.atic.tir.building;

import de.plixo.atic.hir.expr.HIRFunction;
import de.plixo.atic.hir.item.HIRConst;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.typing.TypeQuery;
import de.plixo.atic.typing.types.FunctionType;
import de.plixo.atic.typing.types.Primitive;
import de.plixo.atic.typing.types.Type;

import java.util.ArrayList;

public class ConstantBuilder {

    public static Unit.Constant build(HIRConst hirConst, Unit unit) {

        Type type;
        if (hirConst.typehint() != null) {
            type = TypeBuilder.build(hirConst.typehint(), unit, null, TypeBuilder.GenericCollection.empty());
        } else {
            var expression = hirConst.expression();
            if (expression instanceof HIRFunction hirFunction) {
                var expr = ExpressionBuilder.buildFunction(hirFunction,
                        new FunctionType(Primitive.VOID, new ArrayList<>(), Primitive.VOID),
                        new Scope(null, Primitive.VOID, Primitive.VOID,0), unit, true);
                type = expr.getType();
            } else {
                throw new NullPointerException("give the constant a type hint");
            }
        } return new Unit.Constant(unit, hirConst.name(), type, hirConst.expression());
    }

    public static void addExpressions(Unit unit, Unit.Constant constant) {
        var expr = ExpressionBuilder.build(constant.todo(), constant.type(),
                new Scope(null, Primitive.VOID, Primitive.VOID, 0), unit);
        constant.setExpr(expr);
        var type = expr.getType();
        var typed = constant.type();
        var typeQuery = new TypeQuery(typed, type);
        if (!typeQuery.test()) {
            throw new NullPointerException("Type does not match in constant");
        } else {
            typeQuery.mutate();
        }
    }
}
