package de.plixo.typesys.types;

public sealed abstract class Type
        permits FunctionType, GenericType, Primitive, SolvableType, StructImplementation {

    public abstract String string();

    @Override
    public String toString() {
        return formated();
    }


    public String formated() {
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
