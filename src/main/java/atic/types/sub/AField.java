package atic.types.sub;

import atic.types.AType;
import lombok.AllArgsConstructor;

import java.lang.reflect.Modifier;

@AllArgsConstructor
public class AField {
    public int modifier;
    public String name;
    public AType type;

    @Override
    public String toString() {
        return "AField{" + "name='" + name + '\'' + ", type=" + type + '}';
    }
}
