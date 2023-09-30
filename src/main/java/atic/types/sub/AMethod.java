package atic.types.sub;

import atic.types.AType;
import lombok.AllArgsConstructor;

import java.lang.reflect.Modifier;
import java.util.List;

@AllArgsConstructor
public class AMethod {
    public int modifiers;
    public String name;
    public AType returnType;
    public List<AType> arguments;
    @Override
    public String toString() {
        return "AMethod{" + "name='" + name + '\'' + ", returnType=" + returnType + ", arguments=" +
                arguments + '}';
    }
}
