package de.plixo.galactic.types;

import de.plixo.galactic.tir.stellaclass.StellaClass;
import org.objectweb.asm.tree.ClassNode;

public sealed interface ClassSource {
    record AticSource(StellaClass stellaClass) implements ClassSource {
    }
    record JVMSource(ClassNode classNode) implements ClassSource {
    }
}
