package de.plixo.atic.types;

import de.plixo.atic.tir.aticclass.AticClass;
import org.objectweb.asm.tree.ClassNode;

public sealed interface ClassSource {
    record AticSource(AticClass aticClass) implements ClassSource {
    }
    record JVMSource(ClassNode classNode) implements ClassSource {
    }
}
