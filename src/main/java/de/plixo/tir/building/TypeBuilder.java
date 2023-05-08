package de.plixo.tir.building;

import de.plixo.hir.typedef.HIRClassType;
import de.plixo.hir.typedef.HIRFunctionType;
import de.plixo.hir.typedef.HIRType;
import de.plixo.tir.tree.Unit;
import de.plixo.typesys.types.FunctionType;
import de.plixo.typesys.types.Primitive;
import de.plixo.typesys.types.StructImplementation;
import de.plixo.typesys.types.Type;

public class TypeBuilder {
    public static Type build(HIRType hirType, Unit unit) {
        return switch (hirType) {
            case HIRClassType hirClassType -> {

                if(hirClassType.generics().isEmpty()) {
                    if (hirClassType.path().size() == 1) {
                        var name = hirClassType.path().get(0);
                        var primitive = Primitive.get(name);
                        if (primitive != null) {
                            yield primitive;
                        }
                    }
                }


                var classObj = unit.findImport(hirClassType.path());
                if (classObj == null) {
                    throw new NullPointerException("Cant find " + hirClassType.path());
                }
                var implementation = new StructImplementation(classObj);
                if (hirClassType.generics().size() != classObj.generics().size()) {
                    throw new NullPointerException("Incompatible generic size");
                }
                for (int i = 0; i < hirClassType.generics().size(); i++) {
                    var impl = TypeBuilder.build(hirClassType.generics().get(i), unit);
                    var genericType = classObj.generics().get(i);
                    implementation.implement(genericType, impl);
                }
                yield implementation;
            }
            case HIRFunctionType hirFunctionType -> {
                Type returnType = Primitive.VOID;
                if (hirFunctionType.returnType() != null) {
                    returnType = TypeBuilder.build(hirFunctionType.returnType(), unit);
                }
                var params = hirFunctionType.parameters().stream().map(ref -> TypeBuilder.build(ref,
                        unit)).toList();
               yield new FunctionType(returnType,params);
            }
        };
    }
}
