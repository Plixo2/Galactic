package de.plixo.atic.tir.building;

import de.plixo.atic.hir.typedef.HIRClassType;
import de.plixo.atic.hir.typedef.HIRFunctionType;
import de.plixo.atic.hir.typedef.HIRType;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.typing.types.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TypeBuilder {
    public static Type build(HIRType hirType, Unit unit, @Nullable Type currentSelf,
                             GenericCollection generics) {
        return switch (hirType) {
            case HIRClassType hirClassType -> {
                if (hirClassType.generics().isEmpty()) {
                    if (hirClassType.path().size() == 1) {
                        var name = hirClassType.path().get(0);
                        var generic = generics.getGeneric(name);
                        if (generic != null) {
                            yield generic;
                        }
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
                    var impl = TypeBuilder.build(hirClassType.generics().get(i), unit, currentSelf,
                            generics);
                    var genericType = classObj.generics().get(i);
                    implementation.implement(genericType, impl);
                }
                yield implementation;
            }
            case HIRFunctionType hirFunctionType -> {
                Type returnType = Primitive.VOID;
                if (hirFunctionType.returnType() != null) {
                    returnType = TypeBuilder.build(hirFunctionType.returnType(), unit, currentSelf,
                            generics);
                }
                var params = hirFunctionType.parameters().stream()
                        .map(ref -> TypeBuilder.build(ref, unit, currentSelf, generics)).toList();

                var owner = currentSelf;
                if (owner != null) {
                    if (hirFunctionType.owner() != null) {
                        throw new NullPointerException("Cant specify an owner in an struct");
                    }
                } else {
                    if (hirFunctionType.owner() != null) {
                        owner = TypeBuilder.build(hirFunctionType.owner(), unit, null,
                                GenericCollection.empty());
                    } else {
                        owner = Primitive.VOID;
                    }
                }

                yield new FunctionType(returnType, params, owner);
            }
        };
    }

    public record GenericCollection(List<GenericType> genericTypes) {
        public static GenericCollection empty() {
            return new GenericCollection(new ArrayList<>());
        }

        public @Nullable GenericType getGeneric(String name) {
            for (var genericType : genericTypes) {
                if (genericType.name().equals(name)) {
                    return genericType;
                }
            }
            return null;
        }
    }
}
