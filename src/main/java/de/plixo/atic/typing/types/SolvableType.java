package de.plixo.atic.typing.types;

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

    @Override
    public String shortString() {
        if (type == null) {
            return "?";
        }
        return type.shortString();
    }

    public void solve(Type type) {
        if (this.type instanceof SolvableType solvableType) {
            solvableType.solve(type);
        }
        this.type = type;
    }

    public boolean isSolved() {
        return type != null;
    }
}
