package de.plixo.atic.typing;

import de.plixo.atic.typing.types.*;
import lombok.Getter;

import java.util.Objects;

public class TypeQuery {

    @Getter
    private final Type left;
    @Getter
    private final Type right;

    public TypeQuery(Type left, Type right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    public boolean test() {
        return testAndSolve(left, right, false);
    }

    public void mutate() {
        if (!test()) {
            throw new NullPointerException("cant mutate");
        }
        testAndSolve(left, right, true);
    }

    public void assertEquality() {
        if (!test()) {
            throw new NullPointerException("type test failed between " + left + " and " + right);
        }
        mutate();
    }

    // testAndSolve(a,b,m) should behave like testAndSolve(b,a,m)
    private static boolean testAndSolve(Type typeA, Type typeB, boolean mutate) {

        while (typeA instanceof SolvableType solvableType) {
            if (solvableType.isSolved()) {
                typeA = solvableType.type;
            } else {
                break;
            }
        }
        while (typeB instanceof SolvableType solvableType) {
            if (solvableType.isSolved()) {
                typeB = solvableType.type;
            } else {
                break;
            }
        }

        if (typeA instanceof SolvableType solvableType) {
            if (mutate) solvableType.solve(typeB);
            return true;
        }
        if (typeB instanceof SolvableType solvableType) {
            if (mutate) solvableType.solve(typeA);
            return true;
        }

        return switch (typeA) {
            case StructImplementation structImplementation -> {
                if (!(typeB instanceof StructImplementation b)) {
                    dbg("not compatible types");
                    yield false;
                }
                if (!structImplementation.struct().equals(b.struct())) {
                    dbg("not compatible struct types");
                    yield false;
                }
                var typesA = structImplementation.getTypes();
                var typesB = b.getTypes();
                //todo only with implemented types necessary
                for (int i = 0; i < typesA.size(); i++) {
                    if (!testAndSolve(typesA.get(i), typesB.get(i), mutate)) {
                        dbg("wrong implementation");
                        yield false;
                    }
                }
                yield true;
            }
            case Primitive primitive -> {
                if (!(typeB instanceof Primitive b)) {
                    dbg("not compatible types");
                    yield false;
                }
                if (!b.type().equals(primitive.type())) {
                    dbg("not compatible primitives");
                    yield false;
                }
                yield true;
            }
            case FunctionType functionType -> {
                if (!(typeB instanceof FunctionType b)) {
                    dbg("not compatible types");
                    yield false;
                }
                if (b.arguments().size() != functionType.arguments().size()) {
                    dbg("arg sizes");
                    yield false;
                }
                if (!testAndSolve(functionType.owner(), b.owner(), mutate)) {
                    dbg("function owners");
                    dbg(functionType.owner());
                    dbg(b.owner());
                    yield false;
                }
                for (int i = 0; i < functionType.arguments().size(); i++) {
                    if (!testAndSolve(functionType.arguments().get(i), b.arguments().get(i),
                            mutate)) {
                        dbg("function member");
                        yield false;
                    }
                }
                if (!testAndSolve(functionType.getReturnType(), b.getReturnType(), mutate)) {
                    dbg("return type");
                    yield false;
                }

                yield true;
            }
            case GenericType ignored -> {
                dbg("cant compare generics");
                yield false;
            }
            default -> throw new IllegalStateException("Unexpected value: " + typeA);
        };
    }
    private static void dbg(Object str) {
        //System.out.println(str);
    }
}
