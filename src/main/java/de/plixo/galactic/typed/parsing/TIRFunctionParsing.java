package de.plixo.galactic.typed.parsing;

import de.plixo.galactic.boundary.JVMLoader;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.typed.lowering.Infer;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.typed.stellaclass.Parameter;
import de.plixo.galactic.typed.stellaclass.StellaClass;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static de.plixo.galactic.typed.Scope.INPUT;
import static de.plixo.galactic.typed.Scope.THIS;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class TIRFunctionParsing {
    public static Expression parse(FunctionExpression expression, Context context,
                                   @Nullable Type hint, Infer infer) {
        var region = expression.region();
        var owningUnit = context.unit();
        var classes = owningUnit.classes().size();
        var localName = STR."lambda_\{classes}";

        var interfaceHint = expression.interfaceTypeHint();
        if (interfaceHint == null && hint != null) {
            interfaceHint = hint;
        }
        if (!(interfaceHint instanceof Class theClass) || !theClass.isInterface()) {
            if (interfaceHint == null) {
                throw new FlairCheckException(region, FlairKind.UNKNOWN_TYPE,
                        "Type Hint missing for interface type");
            }
            throw new FlairCheckException(region, FlairKind.TYPE_MISMATCH,
                    "Expected interface type");
        }

        var defaultSuperClass = JVMLoader.asJVMClass(context.language().defaultSuperClass(),
                context.loadedBytecode());
        var stellaClass = new StellaClass(region, localName, owningUnit, null, defaultSuperClass);
        owningUnit.addClass(stellaClass);
        stellaClass.interfaces.add(theClass);
        var toImplement = stellaClass.functionalInterfaceMethod(context);
        if (toImplement == null) {
            throw new FlairCheckException(region, FlairKind.TYPE_MISMATCH,
                    "Interface type is not a functional interface");
        }
        var classOwner = new MethodOwner.ClassOwner(stellaClass);

        var expressions = new ArrayList<Expression>();
        var thisVariable = new Scope.Variable("this", INPUT | THIS, stellaClass);
        StellaMethod initMethod;
        {
            var captures = expression.usedVariables();
            var parameters = new ArrayList<Parameter>();
            var constructBlock = new ArrayList<Expression>();
            for (var capture : captures) {
                parameters.add(new Parameter(capture.name(), capture.getType(), capture.outsideClosure()));
                expressions.add(new VarExpression(region, capture.outsideClosure()));
                var field = capture.field();
                field.type(capture.getType());
                field.owner(stellaClass);
                var fieldOwner = new VarExpression(region, thisVariable);
                capture.owner(stellaClass);
                capture.fieldOwner(thisVariable);
                stellaClass.fields.add(field);

                var putFieldExpression = new PutFieldExpression(region, field, fieldOwner,
                        new VarExpression(region, capture.outsideClosure()));
                constructBlock.add(putFieldExpression);
            }

            initMethod = new StellaMethod(ACC_PUBLIC, "<init>", parameters, new VoidType(), null,
                    classOwner);
            initMethod.thisVariable(thisVariable);
            stellaClass.addMethod(initMethod, context);
            initMethod.body = new BlockExpression(region, constructBlock);
        }

        var implParams = new ArrayList<Parameter>();
        Type returnType;
        {
            if (expression.returnTypeHint() != null) {
                returnType = expression.returnTypeHint();
                if (!Type.isAssignableFrom(returnType, toImplement.returnType(), context)) {
                    throw new FlairCheckException(region, FlairKind.TYPE_MISMATCH,
                            STR."Expected \{toImplement.returnType()} but got \{returnType}");
                }
            } else {
                returnType = toImplement.returnType();
            }

            var implArgs = toImplement.arguments();
            var arguments = implArgs.iterator();
            if (expression.inputVariable().size() != implArgs.size()) {
                throw new FlairCheckException(region, FlairKind.TYPE_MISMATCH,
                        STR."Expected \{implArgs.size()} arguments, but got \{expression.inputVariable()
                                .size()}");
            }
            for (var functionalParameter : expression.inputVariable()) {
                var expectedType = arguments.next();
                var annotatedType = functionalParameter.type();
                var type = annotatedType != null ? annotatedType : expectedType;
                functionalParameter.variable().setType(type);
                implParams.add(new Parameter(functionalParameter.name(), type, functionalParameter.variable()));
                if (annotatedType != null && expectedType != null) {
                    if (!Type.isAssignableFrom(annotatedType, expectedType, context)) {
                        throw new FlairCheckException(region, FlairKind.TYPE_MISMATCH,
                                STR."Expected \{expectedType} but got \{annotatedType}");
                    }
                }
            }
        }


        var impl = new StellaMethod(ACC_PUBLIC, toImplement.name(), implParams, returnType, null,
                classOwner);
        impl.body = infer.parse(expression.expression(), context, returnType);
        context.language().checkStage().parse(impl.body, context, 0);
        impl.thisVariable(thisVariable);
        stellaClass.addMethod(impl, context);
        assert stellaClass.implementationLeft(context).isEmpty();

        return new InstanceCreationExpression(region, initMethod.asMethod(), stellaClass,
                expressions);

    }
}
