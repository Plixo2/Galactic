package de.plixo.atic;

import de.plixo.atic.common.JsonUtil;
import de.plixo.atic.tir.expressions.*;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import test.ClassDebugger;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

@Deprecated
public class Compile {

//    public void compile(Expression expression) {
//
//        if (true) {
//            return;
//        }
//
//        ClassNode cn =  newClass("test/OutClass");
//        var methodNode = staticMethod("method");
//        cn.methods.add(methodNode);
//
//        var ins = methodNode.instructions = new InsnList();
//        compile(expression, ins);
//        ins.add(new InsnNode(RETURN));
//
//        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
//        cn.accept(cw);
//        byte[] b = cw.toByteArray();
//        ClassDebugger.printClass(b);
//        var outFile = new File("resources/test/OutClass.class");
//        JsonUtil.makeFile(outFile);
//        FileUtils.writeByteArrayToFile(outFile, b);
//    }
//
//    private ClassNode newClass(String name) {
//        var classNode = new ClassNode(ASM9);
//        classNode.version = 61;
//        classNode.name = name;
//        classNode.methods.add(defaultConstructor(classNode));
//        classNode.access = ACC_PUBLIC;
//        classNode.superName = "java/lang/Object";
//        classNode.signature = null;
//
//        return classNode;
//    }
//
//    private MethodNode staticMethod(String name) {
//        var methodNode = new MethodNode(ASM9, ACC_PUBLIC | ACC_STATIC, name, "()V", null,
//                new String[]{});
//        return methodNode;
//    }
//
//    private MethodNode defaultConstructor(ClassNode node) {
//        var methodNode = new MethodNode(ASM9, ACC_PUBLIC, "<init>", "()V", null, new String[]{});
//        var ins = methodNode.instructions = new InsnList();
//        var start = new LabelNode(new Label());
//        ins.add(start);
//        ins.add(new VarInsnNode(ALOAD, 0));
//        ins.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
//        ins.add(new InsnNode(RETURN));
//        var end = new LabelNode(new Label());
//        ins.add(end);
//        var variableNode = new LocalVariableNode("this", "L" + node.name + ";", null, start, end,
//                0);
//        methodNode.localVariables.add(variableNode);
//        return methodNode;
//    }
//
//    private void compile(Expression expression, InsnList list) {
//        switch (expression) {
//            case BlockExpression blockExpression -> {
//                var start = new LabelNode(new Label());
//                list.add(start);
//                for (var subExpr : blockExpression.expressions()) {
//                    compile(subExpr, list);
//                }
//                var end = new LabelNode(new Label());
//                list.add(end);
//            }
//            case ConstructExpression constructExpression -> {
//                var aClass = constructExpression.contructedClassType();
//                var name = aClass.path().asSlashString();
//                list.add(new TypeInsnNode(NEW, name));
//                list.add(new InsnNode(DUP));
//                for (var subExpr : constructExpression.expressions()) {
//                    compile(subExpr, list);
//                }
//                var method = constructExpression.constructor();
//                list.add(new MethodInsnNode(INVOKESPECIAL, name, method.name,
//                        method.getDescriptor()));
//            }
//            case ObjectFieldExpression objectFieldExpression -> {
//                compile(objectFieldExpression.object(), list);
//                var field = objectFieldExpression.field();
//                var owner = field.owner.path().asSlashString();
//                var descriptor = field.getDescriptor();
//                list.add(new FieldInsnNode(GETFIELD, owner, field.name, descriptor));
//            }
//            case NumberExpression numberExpression -> {
//                var value = numberExpression.value().doubleValue();
//                list.add(new LdcInsnNode(value));
//            }
//            case StaticFieldExpression staticFieldExpression -> {
//                var field = staticFieldExpression.field();
//                var owner = field.owner.path().asSlashString();
//                var descriptor = field.getDescriptor();
//                list.add(new FieldInsnNode(GETSTATIC, owner, field.name, descriptor));
//            }
//            case MethodInvokeExpression methodInvokeExpression -> {
//                int kind = INVOKEVIRTUAL;
//                var method = methodInvokeExpression.method();
//                if (method.isStatic()) {
//                    kind = INVOKESTATIC;
//                } else {
//                    var object = methodInvokeExpression.object();
//                    assert object != null;
//                    compile(object, list);
//                }
//
//                for (var argument : methodInvokeExpression.arguments()) {
//                    compile(argument, list);
//                }
//
//                var descriptor = method.getDescriptor();
//               // var owner = method.owner.path().asSlashString();
//               // list.add(new MethodInsnNode(kind, owner, method.name, descriptor));
//            }
////            case ArrayConstructExpression arrayConstructExpression -> {
////
////            }
//            default -> {
//                System.err.println(expression + " is not implemented");
//            }
////            default -> throw new IllegalStateException("Unexpected value: " + expression);
//        }
//    }
//
//
//    private static class ClassNode2 extends ClassNode {
//        public ClassNode2() {
//            super(ASM9);
//        }
//
//        @Override
//        public MethodVisitor visitMethod(int access, String name, String desc, String signature,
//                                         String[] exceptions) {
//            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//            Printer p = new Textifier(Opcodes.ASM9) {
//                @Override
//                public void visitMethodEnd() {
//                    var printWriter = new PrintWriter(System.out, false);
//                    print(printWriter);
//                    printWriter.flush();
////                    printWriter.println();
//                }
//            };
//            return new TraceMethodVisitor(mv, p);
//        }
//    }

//    private static String toString(InsnList list) {
//        var builder = new StringBuilder();
//        list.iterator().forEachRemaining(ref -> {
//            builder.append(toString(ref)).append(",");
//        });
//        return builder.toString();
//    }


//    private static String toString(AbstractInsnNode abstractInsnNode) {
//        return switch (abstractInsnNode) {
//            case FieldInsnNode field -> {
//                String str = "field{";
//                str += "name: " + field.name + ",";
//                str += "desc: " + field.desc + ",";
//                str += "owner: " + field.owner + ",";
//                yield str + "}";
//            }
//            case LabelNode labelNode -> {
//                String str = "label {";
//                str += labelNode.getLabel();
//                yield str + "}";
//            }
//            case LdcInsnNode load -> {
//                String str = "LdcInsn {";
//                str += load.cst;
//                yield str + "}";
//            }
//            case LineNumberNode n -> "line " + n.line;
//            default -> throw new IllegalStateException("Unexpected value: " + abstractInsnNode);
//        };
//    }

    private static InputStream getInputStream(Class<?> clazz) {
        String classFile = "/" + clazz.getName().replace('.', '/') + ".class";
        return clazz.getResourceAsStream(classFile);
    }
}
