package de.plixo.atic.typing.types;

public sealed abstract class Type
        permits FunctionType, GenericType, Primitive, SolvableType, StructImplementation {

    public abstract String string();
    public abstract String shortString();

    @Override
    public String toString() {
        return formatted();
    }


    public String formatted() {
        var bob = new StringBuilder();
        var string = string();
        var level = 0;
        var iterator = string.lines().iterator();
        while (iterator.hasNext()) {
            var line = iterator.next();

            if (line.contains("}")) level --;
            bob.append("    ".repeat(level)).append(line);
            if (iterator.hasNext()) {
                bob.append("\n");
            }
            if (line.contains("{")) level ++;
        }
        return bob.toString();
    }
}
