package de.plixo.galactic.boundary;

import java.lang.instrument.Instrumentation;
import java.util.UUID;

public class InstrumentHook {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.getProperties().put(INSTRUMENTATION_KEY, inst);
    }

    public static Instrumentation getInstrumentation() {
        return (Instrumentation) System.getProperties().get(INSTRUMENTATION_KEY);
    }

    private static final Object INSTRUMENTATION_KEY =
            UUID.fromString("25ae95fc-c51f-444c-b348-65de7d1ee55e");

}