package de.plixo.galactic.types;

import de.plixo.galactic.typed.stellaclass.StellaClass;
import org.objectweb.asm.tree.ClassNode;

public sealed interface ClassSource {
    record StellaSource(StellaClass stellaClass) implements ClassSource {
    }

    record JVMSource(ClassNode classNode) implements ClassSource {
    }
}
