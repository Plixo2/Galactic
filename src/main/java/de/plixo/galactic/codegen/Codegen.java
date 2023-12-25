package de.plixo.galactic.codegen;

import de.plixo.galactic.boundary.JVMLoadedClass;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.Scope;
import de.plixo.galactic.tir.expressions.*;
import de.plixo.galactic.tir.path.Unit;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import de.plixo.galactic.tir.stellaclass.StellaClass;
import de.plixo.galactic.tir.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class Codegen {
    private final List<JarOutput> jarOutputs = new ArrayList<>();

    public GeneratedCode getOutput() {
        return new GeneratedCode(jarOutputs);
    }

    public void addUnit(Unit unit, Context context) {
        var classNode = new ClassNode();
        for (var methods : unit.staticMethods()) {
            classNode.methods.add(createStaticMethod(methods, context));
        }
        classNode.access = ACC_PUBLIC;
        classNode.name = unit.getJVMDestination();
        classNode.version = 52;
        classNode.superName = "java/lang/Object";
        var out = getJarOutput(classNode, classNode.name + ".class");
        this.jarOutputs.add(out);
    }

    private JarOutput getJarOutput(ClassNode classNode, String name) {
        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        classNode.accept(cw);
        byte[] b = cw.toByteArray();
        return new JarOutput(name, b);
    }

//    public void makeJarFile(@Nullable String main) throws IOException {
//        var file = "resources/out.jar";
//        Manifest manifest = new Manifest();
//        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
//        if (main != null) manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, main);
//        JarOutputStream target = new JarOutputStream(new FileOutputStream(file), manifest);
//      //  addJar(new File("resources/out/project"), target, "");
//        target.close();
//        System.out.println("JAR file created: " + file);
//    }
//
//
//    private void addJar(File source, JarOutputStream target, String subName) throws IOException {
//        if (source.isDirectory()) {
//            var folderName = subName + source.getName();
//            JarEntry entry = new JarEntry(folderName + "/");
//            entry.setTime(source.lastModified());
//            target.putNextEntry(entry);
//            target.closeEntry();
//            for (File nestedFile : Objects.requireNonNull(source.listFiles())) {
//                addJar(nestedFile, target, folderName + "/");
//            }
//        } else {
//            var name = FilenameUtils.getBaseName(source.getAbsolutePath());
//            var fileName = subName + name + ".class";
//            JarEntry entry = new JarEntry(fileName);
//            entry.setTime(source.lastModified());
//            target.putNextEntry(entry);
//            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source))) {
//                byte[] buffer = new byte[1024];
//                while (true) {
//                    int count = in.read(buffer);
//                    if (count == -1) break;
//                    target.write(buffer, 0, count);
//                }
//                target.closeEntry();
//            }
//        }
//    }

    private MethodNode createStaticMethod(StellaMethod method, Context normalContext) {
        var methodNode = new MethodNode();
        methodNode.access = ACC_PUBLIC | ACC_STATIC;
        methodNode.name = method.localName();
        methodNode.desc = method.asMethod().getDescriptor();
        methodNode.signature = null;
        methodNode.exceptions = new ArrayList<>();
        var context = new CompileContext(methodNode.instructions, methodNode, normalContext);
        LabelNode start;
        context.add(start = new LabelNode());

        method.parameters().forEach(ref -> context.putVariable(ref.variable()));

        parseExpression(Objects.requireNonNull(method.body, "require expression"), context);

        var returned = method.returnType();
        if (Type.isSame(returned, new VoidType())) {
            context.add(new InsnNode(RETURN));
        } else {
            if (returned instanceof PrimitiveType primitive) {
                switch (primitive.typeOfPrimitive) {
                    case INT, BOOLEAN, CHAR, BYTE, SHORT -> context.add(new InsnNode(IRETURN));
                    case LONG -> context.add(new InsnNode(LRETURN));
                    case FLOAT -> context.add(new InsnNode(FRETURN));
                    case DOUBLE -> context.add(new InsnNode(DRETURN));
                }
            } else {
                context.add(new InsnNode(ARETURN));
            }
        }

        LabelNode end;
        context.add(end = new LabelNode());

        methodNode.instructions = context.instructions();
        methodNode.localVariables = getLocalVariableNodes(context, start, end);
        return methodNode;
    }


    private static void parseExpression(Expression expression, CompileContext context) {

        switch (expression) {
            case StringExpression stringExpression -> {
                context.add(new LdcInsnNode(stringExpression.value()));
            }
            case LocalVariableAssign localVariableAssign -> {
                parseExpression(localVariableAssign.expression(), context);
                var target = context.getVariablesIndex(localVariableAssign.variable());
                assert localVariableAssign.variable() != null;
                var type = localVariableAssign.variable().getType();
                var opcode = ASTORE;
                if (type instanceof PrimitiveType primitive) {
                    opcode = switch (primitive.typeOfPrimitive) {
                        case INT, BOOLEAN, CHAR, BYTE, SHORT -> ISTORE;
                        case LONG -> LSTORE;
                        case FLOAT -> FSTORE;
                        case DOUBLE -> DSTORE;
                    };
                }
                context.add(new VarInsnNode(opcode, target));
            }
            case NumberExpression numberExpression -> {
                var type = numberExpression.type();
                var value = numberExpression.value();
                Object val = switch (type) {
                    case INT, BYTE, SHORT, CHAR -> value.intValue();
                    case LONG -> value.longValue();
                    case FLOAT -> value.floatValue();
                    case DOUBLE -> value.doubleValue();
                    case BOOLEAN -> throw new NullPointerException("Boolean not implemented");
                };
                context.add(new LdcInsnNode(val));
            }
            case BooleanExpression booleanExpression -> {
                if (booleanExpression.value()) {
                    context.add(new InsnNode(ICONST_1));
                } else {
                    context.add(new InsnNode(ICONST_0));
                }
            }
            case FieldExpression getFieldExpression -> {
                parseExpression(getFieldExpression.object(), context);
                var field = getFieldExpression.field();
                if (getFieldExpression.owner() instanceof Class aClass) {
                    context.add(
                            new FieldInsnNode(GETFIELD, aClass.getJVMDestination(), field.name(),
                                    field.getDescriptor()));
                } else {
                    throw new NullPointerException(getFieldExpression.owner().getDescriptor());
                }
            }
            case MethodCallExpression methodCallExpression -> {
                var name = methodCallExpression.method().name();
                var descriptor = methodCallExpression.method().getDescriptor();
                switch (methodCallExpression.source()) {
                    case StaticMethodExpression staticMethodExpression -> {
                        var owner = "project/Main";
                        switch (staticMethodExpression.owner()) {
                            case MethodOwner.UnitOwner(var unit) -> {
                                owner = unit.getJVMDestination();
                            }
                            case MethodOwner.ClassOwner(var aticClass) -> {
                                switch (aticClass) {
                                    case StellaClass ignored -> {
                                        throw new NullPointerException(
                                                "classes are not compiled yet");
                                    }
                                    case JVMLoadedClass loadedClass -> {
                                        owner = loadedClass.getNode().name;
                                    }
                                    default -> throw new IllegalStateException(
                                            "Unexpected value: " + aticClass);
                                }
                            }
                        }
                        methodCallExpression.arguments().forEach(e -> parseExpression(e, context));
                        context.add(
                                new MethodInsnNode(INVOKESTATIC, owner, name, descriptor, false));
                    }
                    case GetMethodExpression getMethodExpression -> {
                        parseExpression(getMethodExpression.object(), context);
                        methodCallExpression.arguments().forEach(e -> parseExpression(e, context));
                        var owner = methodCallExpression.calledType();
                        if (owner instanceof Class aClass) {
                            int opCode = INVOKEVIRTUAL;
                            if (aClass.isInterface()) {
                                opCode = INVOKEINTERFACE;
                            }
                            var ownerString = aClass.getJVMDestination();
                            context.add(new MethodInsnNode(opCode, ownerString, name, descriptor,
                                    opCode == INVOKEINTERFACE));
                        } else {
                            throw new NullPointerException(owner.getDescriptor());
                        }

                    }
                    default -> throw new IllegalStateException(
                            "Unexpected value: " + methodCallExpression.source());
                }
            }
            case BranchExpression branchExpression -> {
                var elseTarget = new LabelNode();
                var endTarget = new LabelNode();
                parseExpression(branchExpression.condition(), context);
                context.add(new JumpInsnNode(IFEQ, elseTarget));
                parseExpression(branchExpression.then(), context);
                context.add(new JumpInsnNode(GOTO, endTarget));
                context.add(elseTarget);
                if (branchExpression.elseExpression() != null) {
                    parseExpression(branchExpression.elseExpression(), context);
                }
                context.add(endTarget);

            }
            case BlockExpression blockExpression -> {
                var iterator = blockExpression.expressions().iterator();
                while (iterator.hasNext()) {
                    var next = iterator.next();
                    parseExpression(next, context);
                    if (!next.getType(context.normalContext()).equals(new VoidType()) &&
                            iterator.hasNext()) {
                        context.add(new InsnNode(POP));
                    }
                }
                // blockExpression.expressions().forEach(e -> parseExpression(e, context));
            }
            case VarDefExpression varDefExpression -> {
                context.putVariable(varDefExpression.variable());
                parseExpression(varDefExpression.expression(), context);
                var target = context.getVariablesIndex(varDefExpression.variable());
                var type = varDefExpression.variable().getType();
                var opcode = ASTORE;
                if (type instanceof PrimitiveType primitive) {
                    opcode = switch (primitive.typeOfPrimitive) {
                        case INT, BOOLEAN, CHAR, BYTE, SHORT -> ISTORE;
                        case LONG -> LSTORE;
                        case FLOAT -> FSTORE;
                        case DOUBLE -> DSTORE;
                    };
                }
                context.add(new VarInsnNode(opcode, target));
            }
            case VarExpression varExpression -> {
                var target = context.getVariablesIndex(varExpression.variable());
                var type = varExpression.variable().getType();
                var opcode = ALOAD;
                if (type instanceof PrimitiveType primitive) {
                    opcode = switch (primitive.typeOfPrimitive) {
                        case INT, BOOLEAN, CHAR, BYTE, SHORT -> ILOAD;
                        case LONG -> LLOAD;
                        case FLOAT -> FLOAD;
                        case DOUBLE -> DLOAD;
                    };
                }
                context.add(new VarInsnNode(opcode, target));
            }
            case StaticFieldExpression staticFieldExpression -> {
                var field = staticFieldExpression.field();
                var owner = staticFieldExpression.aClass().getJVMDestination();
                context.add(
                        new FieldInsnNode(GETSTATIC, owner, field.name(), field.getDescriptor()));
            }
            case InstanceCreationExpression instanceCreationExpression -> {
                throw new NullPointerException("not implemented");
            }
            case null, default -> {
                throw new NullPointerException("expression is null");
            }
        }
    }

    private static List<LocalVariableNode> getLocalVariableNodes(CompileContext context,
                                                                 LabelNode start, LabelNode end) {
        var variables = new ArrayList<LocalVariableNode>();
        for (Map.Entry<Scope.Variable, Integer> variable : context.getVariables()) {
            var index = variable.getValue();
            var variableKey = variable.getKey();
            var name = variableKey.name();
            assert variableKey.getType() != null;
            var node =
                    new LocalVariableNode(name, variableKey.getType().getDescriptor(), null, start,
                            end, index);
            variables.add(node);
        }
        return variables;
    }


}
