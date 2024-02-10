package de.plixo.galactic.codegen;

import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The CompileContext is used to store information about the current compilation.
 * It is used to store the instructions and variables.
 */
@AllArgsConstructor
@Getter
public class CompileContext {
    @Setter
    private int lastLineNumber = 0;
    private final InsnList instructions;
    private final MethodNode node;
    private final Context normalContext;
    private final Map<Scope.Variable, Integer> variables = new HashMap<>();
    private int variablesCount = 0;


    public void add(AbstractInsnNode instruction) {
        this.instructions.add(instruction);
    }

    public void putVariable(@NotNull Scope.Variable variable) {
        if (!variables.containsKey(variable)) {
            variables.put(variable, variablesCount);
            var type = variable.getType();
            variablesCount += Objects.requireNonNull(type).JVMSize();
        }
    }

    public int getVariablesIndex(Scope.Variable variable) {
        if (!variables.containsKey(variable)) {
            throw new NullPointerException(STR."Variable not found \{variable}");
        }
        return variables.get(variable);
    }

    public Set<Map.Entry<Scope.Variable, Integer>> getVariables() {
        return Set.copyOf(variables.entrySet());
    }


}
