package test;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

public class TypeSignatureNode extends SignatureVisitor {

    public String classType;
    boolean parseArguments = false;
    protected TypeSignatureNode() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visitClassType(String name) {
        classType = name;
    }

    @Override
    public void visitInnerClassType(String name) {
        throw new NullPointerException("not supported");
    }

    @Override
    public void visitEnd() {
        parseArguments = true;
    }

//    public static class Interface/
}
