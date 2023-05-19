package de.plixo.atic.tir.building;

import de.plixo.atic.hir.expr.HIRFunction;
import de.plixo.atic.hir.item.HIRStruct;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.typing.TypeQuery;
import de.plixo.atic.typing.types.FunctionType;
import de.plixo.atic.typing.types.Primitive;
import de.plixo.atic.typing.types.StructImplementation;

import java.util.ArrayList;
import java.util.Objects;

public class StructBuilder {
    public static Unit.Structure build(Unit unit, HIRStruct item) {
        var structure = new Unit.Structure(unit, item.name());
        item.generics().forEach(structure::addGenerics);
        structure.addTodo(item);
        return structure;
    }


    public static void addFields(Unit unit, Unit.Structure structure) {
        var item = Objects.requireNonNull(structure.todo());
        var structType = new StructImplementation(structure);
        var genericCollection = new TypeBuilder.GenericCollection(structType.struct().generics());
        item.fields().forEach(ref -> {
            switch (ref.defineType()) {
                case VALUE -> {
                    var expression = ref.defaultValue();
                    if (expression instanceof HIRFunction hirFunction) {
                        var expr = ExpressionBuilder.buildFunction(hirFunction,
                                new FunctionType(Primitive.VOID, new ArrayList<>(), structType),
                                new Scope(null, Primitive.VOID, Primitive.VOID, 0), unit, true);
                        structure.addField(
                                new Unit.Structure.Field(ref.name(), expr.getType(), null,
                                        expression));
                    } else {
                        throw new NullPointerException("give the field a type hint");
                    }
                }
                case NOTHING -> {
                    structure.addField(new Unit.Structure.Field(ref.name(), Primitive.VOID, null,
                            ref.defaultValue()));
                }
                case TYPE_VALUE -> {
                    assert ref.typeHint() != null;
                    assert ref.defaultValue() != null;
                    var build =
                            TypeBuilder.build(ref.typeHint(), unit, structType, genericCollection);
                    structure.addField(
                            new Unit.Structure.Field(ref.name(), build, null, ref.defaultValue()));
                }
                case TYPE -> {
                    assert ref.typeHint() != null;
                    var build =
                            TypeBuilder.build(ref.typeHint(), unit, structType, genericCollection);
                    structure.addField(new Unit.Structure.Field(ref.name(), build, null, null));
                }
            }
        });
    }

    public static void addDefaults(Unit unit, Unit.Structure structure) {
        structure.fields().forEach((name, field) -> {
            if (field.todo() != null) {
                var scope = new Scope(null, Primitive.VOID, Primitive.VOID, 0);
                var expr = ExpressionBuilder.build(field.todo(), field.type(), scope, unit);
                new TypeQuery(expr.getType(), field.type()).assertEquality();
                field.setExpr(expr);
            }
            field.clearTodo();
        });
    }
}
