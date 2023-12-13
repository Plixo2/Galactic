package de.plixo.atic.tir;

import de.plixo.atic.types.Type;
import de.plixo.atic.types.Method;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A collection of methods from a given name, with different signatures
 */
@Getter
@ToString
public class MethodCollection {
    private final String name;
    private final List<Method> methods;

    public MethodCollection(String name, List<Method> methods) {
        this.name = name;
        this.methods = methods;
    }


    public MethodCollection(String name, Method method) {
        this.name = name;
        this.methods = List.of(method);
    }

    public MethodCollection join(@Nullable MethodCollection method) {
        if (method != null) {
            var objects = new ArrayList<Method>();
            objects.addAll(this.methods);
            objects.addAll(method.methods);
            return new MethodCollection(name, objects);
        }
        return this;
    }

    public boolean isEmpty() {
        return methods.isEmpty();
    }

    public MethodCollection filter(Predicate<Method> predicate) {
        return new MethodCollection(this.name, this.methods.stream().filter(predicate).toList());
    }

    public @Nullable Method findBestMatch(List<Type> types, Context context) {
        for (Method method : methods) {
            if (method.isCallable(types, context)) {
                return method;
            }
        }
        return null;

    }
}
