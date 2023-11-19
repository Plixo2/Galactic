package de.plixo.atic.tir.parsing;

public class TIRFunctionParsing {

//    public static void addFunctionShell(Unit unit, HIRMethod function) {
//        var name = function.methodName();
//        unit.addMethod(new Method(name, unit, function));
//    }
//
//    public static void addFunctionParameters(Method method, Context context) {
//        var hir = method.hir();
//        var returnType = TIRTypeParsing.parse(hir.returnType(), context);
//        hir.parameters().forEach(param -> {
//            var type = TIRTypeParsing.parse(param.type(), context);
//            method.addParameter(param.name(), type);
//        });
//        method.setReturnType(returnType);
//    }
//
//    public static void addFunctionBody(Method method, Context context) {
//        method.parameters().forEach(param -> param.addTo(context));
//
//        method.setExpression(TIRExpressionParsing.parse(method.hir().expression(), context));
//    }
}
