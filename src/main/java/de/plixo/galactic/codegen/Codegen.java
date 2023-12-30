package de.plixo.galactic.codegen;

import de.plixo.galactic.boundary.JVMLoadedClass;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.typed.path.Unit;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.typed.stellaclass.StellaClass;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

/**
 * Code generator for the JVM.
 */
@RequiredArgsConstructor
public class Codegen {
    private final int version;
    private final List<JarOutput> jarOutputs = new ArrayList<>();

    public GeneratedCode getOutput() {
        return new GeneratedCode(jarOutputs);
    }

    public void addUnit(Unit unit, Context context) {
        var classNode = new ClassNode();
        for (var methods : unit.staticMethods()) {
            var method = createMethod(methods, context);
            method.access = method.access | ACC_STATIC;
            classNode.methods.add(method);
        }
        classNode.access = ACC_PUBLIC;
        classNode.name = unit.getJVMDestination();
        classNode.version = version;
        classNode.superName = "java/lang/Object";
        var out = getJarOutput(classNode, STR."\{classNode.name}.class");
        this.jarOutputs.add(out);
    }

    public void addClass(StellaClass stellaClass, Context context) {
        var classNode = new ClassNode();
        for (var methodImpl : stellaClass.methods()) {
            var method = methodImpl.stellaMethod();
            classNode.methods.add(createMethod(method, context));
        }
        classNode.access = ACC_PUBLIC | ACC_STATIC;
        classNode.name = stellaClass.getJVMDestination();
        classNode.version = version;
        classNode.superName = stellaClass.superClass.getJVMDestination();
        classNode.interfaces.addAll(stellaClass.interfaces.stream()
                .map(Class::getJVMDestination).toList());

        var out = getJarOutput(classNode, STR."\{classNode.name}.class");
        this.jarOutputs.add(out);
    }

    private JarOutput getJarOutput(ClassNode classNode, String name) {
        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        classNode.accept(cw);
        byte[] b = cw.toByteArray();
        return new JarOutput(name, b);
    }

    private MethodNode createMethod(StellaMethod method, Context normalContext) {
        var methodNode = new MethodNode();
        methodNode.access = ACC_PUBLIC;
        methodNode.name = method.localName();
        methodNode.desc = method.asMethod().getDescriptor();
        methodNode.signature = null;
        methodNode.exceptions = new ArrayList<>();
        var context = new CompileContext(methodNode.instructions, methodNode, normalContext);
        LabelNode start;
        context.add(start = new LabelNode());

        if (method.thisVariable() != null) {
            context.putVariable(method.thisVariable());
        }
        method.parameters().forEach(ref -> context.putVariable(ref.variable()));

        if (method.body == null && method.localName().equals("<init>")) {
            //TODO
             context.add(new VarInsnNode(ALOAD, 0));
              var superCall =
                     new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
              context.add(superCall);
              context.add(new InsnNode(RETURN));
        } else {
            parseExpression(
                    Objects.requireNonNull(method.body,
                            STR."require expression \{method.localName()}"),
                    context);
        }

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
                            case MethodOwner.ClassOwner(var stellaClass) -> {
                                switch (stellaClass) {
                                    case StellaClass ignored -> {
                                        throw new NullPointerException(
                                                "classes are not compiled yet");
                                    }
                                    case JVMLoadedClass loadedClass -> {
                                        owner = loadedClass.getNode().name;
                                    }
                                    default -> throw new IllegalStateException(
                                            STR."Unexpected value: \{stellaClass}");
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
                var src = instanceCreationExpression.type().getJVMDestination();
                var descriptor = instanceCreationExpression.constructor().getDescriptor();
                context.add(new TypeInsnNode(NEW, src));
                context.add(new InsnNode(DUP));
                instanceCreationExpression.expressions().forEach(e -> parseExpression(e, context));
                context.add(new MethodInsnNode(INVOKESPECIAL, src, "<init>", descriptor, false));
            }
            case PutFieldExpression putFieldExpression -> {
                parseExpression(putFieldExpression.object(), context);
                parseExpression(putFieldExpression.value(), context);
                var field = putFieldExpression.field();
                var owner = field.owner();
                context.add(new FieldInsnNode(PUTFIELD, owner.getJVMDestination(), field.name(),
                        field.getDescriptor()));
            }
            case PutStaticFieldExpression putStaticFieldExpression -> {
                parseExpression(putStaticFieldExpression.value(), context);
                var field = putStaticFieldExpression.field();
                var owner = field.owner();
                context.add(new FieldInsnNode(PUTSTATIC, owner.getJVMDestination(), field.name(),
                        field.getDescriptor()));
            }
            case CastExpression castExpression -> {
                parseExpression(castExpression.object(), context);
                var type = castExpression.type();
                if (!(type instanceof Class aClass)) {
                    throw new NullPointerException(type.getDescriptor());
                }
                context.add(new TypeInsnNode(CHECKCAST, aClass.getJVMDestination()));
            }
            case CastCheckExpression castCheckExpression -> {
                parseExpression(castCheckExpression.object(), context);
                var type = castCheckExpression.type();
                if (!(type instanceof Class aClass)) {
                    throw new NullPointerException(type.getDescriptor());
                }
                context.add(new TypeInsnNode(INSTANCEOF, aClass.getJVMDestination()));
            }
            case null -> {
                throw new NullPointerException("expression is null");
            }
            default -> throw new IllegalStateException(STR."Unexpected value: \{expression}");
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
