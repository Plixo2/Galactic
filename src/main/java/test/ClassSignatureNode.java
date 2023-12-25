package test;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.List;

public class ClassSignatureNode extends SignatureVisitor {

    public String classType;
    public List<GenericType> genericTypes = new ArrayList<>();

    protected ClassSignatureNode() {
        super(Opcodes.ASM9);
    }

    public @Nullable ClassSignatureNode superClass;

    private ClassSignatureNode nextClassBoundNode;

    @Override
    public void visitFormalTypeParameter(String name) {
        genericTypes.add(new GenericType(name, nextClassBoundNode = new ClassSignatureNode()));
        super.visitFormalTypeParameter(name);
    }


    @Override
    public String toString() {
        return "ClassSignatureNode{\n" + "classType='" + classType + "'" + "\n, genericTypes=" +
                genericTypes + ",\n superClass=" + superClass + "\n}";
    }

    @Override
    public SignatureVisitor visitClassBound() {
        return nextClassBoundNode;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        superClass = new ClassSignatureNode();
        return superClass;
    }

    @Override
    public void visitClassType(String name) {
        classType = name;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        throw new NullPointerException("not supported");
    }

    @Override
    public SignatureVisitor visitParameterType() {
        throw new NullPointerException("not supported");
    }


    @Override
    public SignatureVisitor visitReturnType() {
        throw new NullPointerException("not supported");
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        throw new NullPointerException("not supported");
    }

    @Override
    public SignatureVisitor visitInterface() {
        throw new NullPointerException("not supported");
    }

    @Override
    public void visitInnerClassType(String name) {
        throw new NullPointerException("not supported");
    }

    @AllArgsConstructor
    public static class GenericType {
        public String name;
        public ClassSignatureNode classBound;

        @Override
        public String toString() {
            return "GenericType{" + "name='" + name + '\'' + ", classBound=" + classBound + '}';
        }
    }
}
