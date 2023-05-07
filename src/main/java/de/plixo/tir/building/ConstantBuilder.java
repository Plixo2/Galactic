package de.plixo.tir.building;

import de.plixo.hir.item.HIRConst;
import de.plixo.tir.scoping.Scope;
import de.plixo.tir.tree.Unit;
import de.plixo.typesys.TypeQuery;
import de.plixo.typesys.types.Primitive;
import de.plixo.typesys.types.Type;

public class ConstantBuilder {

    public static Unit.Constant build(HIRConst hirConst, Unit unit) {

        Type type = null;
        if (hirConst.typehint != null) {
            type = TypeBuilder.build(hirConst.typehint, unit);
        } else {
            throw new NullPointerException("TODO");
        }
//        if (hirConst.expression instanceof HIRFunction function) {
//            throw new NullPointerException("TODO");
//            //var returnType = new Primitive(Primitive.PrimitiveType.VOID);
//            //if (type instanceof FunctionType functionType) {
//            //    if (functionType.returnType() instanceof Primitive primitive) {
//            //
//            //    }
//            //}
//            //expressionType = function.arguments;
//        } else {
//            //evaluate to constant
//            //maybe delay all the function and evaluate later
//            throw new NullPointerException("TODO");
//        }
        return new Unit.Constant(unit, hirConst.name, type, hirConst.expression);
    }

    public static void addExpressions(Unit unit, Unit.Constant constant) {
        var expr = ExpressionBuilder.build(constant.todo(), constant.type(),
                new Scope(null, Primitive.VOID), unit);
        constant.setExpr(expr);
        var type = expr.getType();
        var typed = constant.type();
        var typeQuery = new TypeQuery(typed, type);
        if (!typeQuery.test()) {
            throw new NullPointerException("Type does not match in constant");
        }
    }
}
