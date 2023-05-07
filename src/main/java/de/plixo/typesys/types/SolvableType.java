package de.plixo.typesys.types;

public final class SolvableType extends Type {
    public Type type;

    @Override
    public String string() {
        var s = "not solved #" + Integer.toHexString(hashCode());
        if (type == null) {
            return s;
        }
        return "solved " + type + " #" + Integer.toHexString(hashCode());
    }

    public void solve(Type type) {
        if (this.type instanceof SolvableType solvableType) {
            solvableType.solve(type);
        }
        this.type = type;
    }

    public boolean isSolved() {
        return type != null;
//        if (type == null) {
//            return false;
//        }
//        if (type instanceof SolvableType solvableType) {
//            return solvableType.isSolved();
//        }
//        return true;
    }
}
