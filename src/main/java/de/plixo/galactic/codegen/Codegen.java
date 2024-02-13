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
 * Warning: this is a mess lol
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
            var method = createMethod(methods, context, true);
            classNode.methods.add(method);
        }
        classNode.sourceFile = unit.file().getAbsolutePath();
        classNode.access = ACC_PUBLIC | ACC_STATIC;
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
            classNode.methods.add(createMethod(method, context, false));
        }
        var file = stellaClass.region().left().file();
        if (file != null) {
            classNode.sourceFile = file.getAbsolutePath();
        }
        classNode.access = ACC_PUBLIC | ACC_STATIC;
        classNode.name = stellaClass.getJVMDestination();
        classNode.version = version;
        classNode.superName = stellaClass.superClass.getJVMDestination();
        classNode.interfaces.addAll(
                stellaClass.interfaces.stream().map(Class::getJVMDestination).toList());
        classNode.fields = new ArrayList<>();
        for (var field : stellaClass.fields) {
            var fieldNode =
                    new FieldNode(field.modifier(), field.name(), field.getDescriptor(), null,
                            null);
            classNode.fields.add(fieldNode);
        }
        var out = getJarOutput(classNode, STR."\{classNode.name}.class");
        this.jarOutputs.add(out);
    }


    private JarOutput getJarOutput(ClassNode classNode, String name) {
        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        classNode.accept(cw);
        byte[] b = cw.toByteArray();
        return new JarOutput(name, b);
    }

    private MethodNode createMethod(StellaMethod method, Context normalContext, boolean isStatic) {
        var methodNode = new MethodNode();
        methodNode.access = ACC_PUBLIC;
        if (isStatic) {
            methodNode.access = methodNode.access | ACC_STATIC;
        }
        methodNode.name = method.localName();
        methodNode.signature = null;
        methodNode.exceptions = new ArrayList<>();
        var thisContext = method.thisContext();
        var methodType = method.asMethod();
        methodNode.desc = methodType.getDescriptor();
        var startIndex = 0;
        if (!isStatic) {
            assert thisContext != null;
            startIndex = 1;
        }
        var context = new CompileContext(-1, methodNode.instructions, methodNode, normalContext,
                startIndex);
        LabelNode start;
        context.add(start = new LabelNode());
        var body = method.body();
//        if (body != null) {
//            var line = body.region().left().line();
//            context.add(new LineNumberNode(line, start));
//        }
        method.parameters().forEach(ref -> context.putVariable(ref.variable()));

        if (method.isConstructor()) {
            //TODO
            context.add(new VarInsnNode(ALOAD, 0));
            var superCall = switch (method.owner()) {
                case MethodOwner.ClassOwner(var owningClass) when owningClass.getSuperClass() !=
                        null -> new MethodInsnNode(INVOKESPECIAL,
                        owningClass.getSuperClass().getJVMDestination(), "<init>", "()V", false);
                case MethodOwner.UnitOwner _ ->
                        new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V",
                                false);
                default ->
                        throw new IllegalStateException(STR."Unexpected value: \{method.owner()}");
            };
            context.add(superCall);
        }
        parseExpression(
                Objects.requireNonNull(body, STR."require expression \{method.localName()}"),
                context);


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
        if (body != null) {
            // var line = body.region().right().line();
            //  context.add(new LineNumberNode(line, end));
        }

        methodNode.instructions = context.instructions();
        methodNode.localVariables = new ArrayList<>();
        if (!isStatic) {
            assert thisContext != null;
            var node =
                    new LocalVariableNode("this", thisContext.getDescriptor(), null, start, end, 0);
            methodNode.localVariables.add(node);
        }
        methodNode.localVariables = getLocalVariableNodes(context, start, end);
        return methodNode;
    }


    private static void parseExpression(Expression expression, CompileContext context) {
        var region = expression.region();
        var currentLine = region.minPosition().line() + 1;
        if (currentLine != context.lastLineNumber()) {
            var label = new LabelNode();
            context.add(label);
            context.add(new LineNumberNode(currentLine, label));
            context.lastLineNumber(currentLine);
        }
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
            case WhileExpression whileExpression -> {
                var start = new LabelNode();
                var end = new LabelNode();
                context.add(start);
                parseExpression(whileExpression.condition(), context);
                context.add(new JumpInsnNode(IFEQ, end));
                var body = whileExpression.body();
                parseExpression(body, context);
                var bodyType = body.getType(context.normalContext());
                if (!bodyType.equals(new VoidType())) {
                    context.add(new InsnNode(POP));
                }
                context.add(new JumpInsnNode(GOTO, start));
                context.add(end);
            }
            case FieldExpression getFieldExpression -> {
                parseExpression(getFieldExpression.object(), context);
                var field = getFieldExpression.field();
                if (getFieldExpression.owner() instanceof Class aClass) {
                    var fieldInsnNode =
                            new FieldInsnNode(GETFIELD, aClass.getJVMDestination(), field.name(),
                                    field.getDescriptor());
                    context.add(fieldInsnNode);
                } else {
                    throw new NullPointerException(getFieldExpression.owner().getDescriptor());
                }
            }
            case MethodCallExpression methodCallExpression -> {
                var method = methodCallExpression.method();
                var name = method.name();
                var descriptor = methodCallExpression.method().getDescriptor();
                switch (methodCallExpression.source()) {
                    case StaticMethodExpression staticMethodExpression -> {
                        String owner;
                        switch (method.owner()) {
                            case MethodOwner.UnitOwner(var unit) -> {
                                owner = unit.getJVMDestination();
                            }
                            case MethodOwner.ClassOwner(var owningClass) -> {
                                switch (owningClass) {
                                    case StellaClass stellaClass -> {
                                        owner = stellaClass.getJVMDestination();
                                    }
                                    case JVMLoadedClass loadedClass -> {
                                        owner = loadedClass.getNode().name;
                                    }
                                    default -> throw new IllegalStateException(
                                            STR."Unexpected value: \{owningClass}");
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
                var normalContext = context.normalContext();
                //TODO
                if (!branchExpression.then().getType(normalContext).equals(new VoidType()) &&
                        branchExpression.getType(normalContext).equals(new VoidType())) {
                    context.add(new InsnNode(POP));
                }
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
                    var type = next.getType(context.normalContext());
                    if (!type.equals(new VoidType()) && iterator.hasNext()) {
                        var size = type.JVMSize();
                        if (size == 2) {
                            context.add(new InsnNode(POP2));
                        } else if (size == 1) {
                            context.add(new InsnNode(POP));
                        } else {
                            for (int i = 0; i < size; i++) {
                                context.add(new InsnNode(POP));
                            }
                        }
                    }
                }
            }
            case ArrayLengthExpression arrayLengthExpression -> {
                parseExpression(arrayLengthExpression.expression(), context);
                context.add(new InsnNode(ARRAYLENGTH));
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
            case ThisExpression thisExpression -> {
                var opcode = ALOAD;
                if (thisExpression.type() instanceof PrimitiveType primitive) {
                    opcode = switch (primitive.typeOfPrimitive) {
                        case INT, BOOLEAN, CHAR, BYTE, SHORT -> ILOAD;
                        case LONG -> LLOAD;
                        case FLOAT -> FLOAD;
                        case DOUBLE -> DLOAD;
                    };
                }
                context.add(new VarInsnNode(opcode, 0));
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
            assert variableKey != null : STR."variableKey \{variable} is null";
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
