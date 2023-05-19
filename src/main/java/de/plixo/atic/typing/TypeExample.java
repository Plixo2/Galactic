package de.plixo.atic.typing;


public class TypeExample {
    private void stuff() {

    /*

        Struct genericClass = new Struct("GenericClass");
        var genericType = new GenericType("T");
        genericClass.genericTypes.add(genericType);
        genericClass.members.put("object", genericType);

        Struct classHolder = new Struct("ClassHolder");
        var genericTypeSub = new GenericType("W");
        classHolder.genericTypes.add(genericTypeSub);
        var structImpl = new StructImplementation(genericClass);
        structImpl.implement(genericType, genericTypeSub);
        classHolder.members.put("genericClass", structImpl);

        Type expected;
        {
            var holdImpl = new StructImplementation(classHolder);
            holdImpl.implement(genericTypeSub, new Primitive("bool"));
            expected = holdImpl;
        }

        for (int i = 0; i < 2; i++) {
            var holdImpl = new StructImplementation(classHolder);
            var type = new SolvableType();
            holdImpl.implement(genericTypeSub, type);
            if (i == 0) {
                new TypeQuery(holdImpl, expected).mutate();
            }
            System.out.println("holdImpl = " + holdImpl);
            var implObjClass = holdImpl.get("genericClass");
            System.out.println("implObjClass = " + implObjClass);
            if (implObjClass instanceof StructImplementation classImpl) {
                var expectString = classImpl.get("object");
                if (i == 1) {
                    var solvableType = new SolvableType();
                    new TypeQuery(expectString, solvableType).mutate();
                    new TypeQuery(solvableType, new Primitive("float")).mutate();
                    System.out.println("holdImpl = " + holdImpl);
                }
                System.out.println("expectString = " + expectString);

            }
        }

     */
    }

}