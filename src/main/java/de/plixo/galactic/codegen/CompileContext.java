package de.plixo.galactic.codegen;

import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.Scope;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The CompileContext is used to store information about the current compilation.
 * It is used to store the instructions and variables.
 */
@RequiredArgsConstructor
@Getter
public class CompileContext {
    private final InsnList instructions;
    private final MethodNode node;
    private final Context normalContext;
    private final Map<Scope.Variable, Integer> variables = new HashMap<>();
    private int variablesCount = 0;


    public void add(AbstractInsnNode instruction) {
        this.instructions.add(instruction);
    }

    public void putVariable(Scope.Variable variable) {
        if (!variables.containsKey(variable)) {
            variables.put(variable, variablesCount);
            variablesCount++;
        }
    }

    public int getVariablesIndex(Scope.Variable variable) {
        if (!variables.containsKey(variable)) {
            throw new NullPointerException("Variable not found");
        }
        return variables.get(variable);
    }

    public Set<Map.Entry<Scope.Variable, Integer>> getVariables() {
        return Set.copyOf(variables.entrySet());
    }


}
