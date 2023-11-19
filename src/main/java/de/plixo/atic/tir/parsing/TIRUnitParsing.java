package de.plixo.atic.tir.parsing;

import de.plixo.atic.hir.items.HIRClass;
import de.plixo.atic.hir.items.HIRMethod;
import de.plixo.atic.hir.items.HIRStaticMethod;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.tir.aticclass.Parameter;
import de.plixo.atic.tir.path.Unit;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class TIRUnitParsing {
    public static AticMethod parse(Unit unit, HIRStaticMethod hirMethod, Context context) {
        var method = hirMethod.hirMethod();
        var parameters = method.parameters().stream().map(ref -> new Parameter(ref.name(),
                    TIRTypeParsing.parse(ref.type(), context))).toList();
            var returnType = TIRTypeParsing.parse(method.returnType(), context);
            var aticMethod =
                    new AticMethod(unit, ACC_PUBLIC, method.methodName(), parameters, returnType,
                            method);
        return aticMethod;
    }
}
