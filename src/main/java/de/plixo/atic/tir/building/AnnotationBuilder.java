package de.plixo.atic.tir.building;

import de.plixo.atic.exceptions.reasons.GeneralFailure;
import de.plixo.atic.hir.item.HIRAnnotation;
import de.plixo.atic.tir.expr.ConstantExpr;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.tir.tree.CompileAnnotations;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.typing.types.Primitive;

public class AnnotationBuilder {

    public static Unit.Annotation buildAnnotation(Unit unit, HIRAnnotation annotation) {
        var list = annotation.arguments().stream().map(ref -> {
            var scope = new Scope(null, Primitive.VOID, Primitive.VOID, 0);
            var expr = ExpressionBuilder.build(ref, Primitive.INT, scope, unit);
            if (!(expr instanceof ConstantExpr constantExpr)) {
                throw new GeneralFailure(ref.region(),
                        "only parse constants in annotations").create();
            }
            return constantExpr;
        }).toList();
        var buildAnnotation = new Unit.Annotation(annotation.region(), annotation.name(), list);
        if (!CompileAnnotations.isBuildIn(buildAnnotation)) {
            throw new GeneralFailure(annotation.region(),"custom annotations not yet supported").create();
        }
        return buildAnnotation;
    }
}
