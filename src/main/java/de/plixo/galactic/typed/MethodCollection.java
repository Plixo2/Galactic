package de.plixo.galactic.typed;

import de.plixo.galactic.types.Method;
import de.plixo.galactic.types.Signature;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
    public MethodCollection map(Function<Method, Method> function) {
        return new MethodCollection(this.name, this.methods.stream().map(function).toList());
    }

    /**
     * Find the best matching method for the given signature.
     * Returns null, if to signature matches.
     *
     * @param signature the signature to match
     * @return the best matching method
     */
    public @Nullable Method findBestMatch(Signature signature, Context context) {
        var matches = methods.stream().filter(method -> method.matchSignature(signature, context));
        //TODO grade and find best match
        return matches.findFirst().orElse(null);
    }
}
