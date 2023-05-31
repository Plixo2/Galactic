package de.plixo.atic.tir.tree;

import com.google.common.collect.Streams;
import de.plixo.atic.common.Constant;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class CompileAnnotations {

    public static boolean isBuildIn(Unit.Annotation annotation) {
        return Arrays.stream(CompileAnnotation.values())
                .anyMatch(ref -> isAnnotation(ref, annotation));
    }

    public static boolean isAnnotation(CompileAnnotation type, Unit.Annotation annotation) {
        if (!type.annotationName().equals(annotation.name())) return false;
        if (type.options().size() != annotation.values().size()) return false;

        return Streams.zip(type.options().stream(), annotation.values().stream(),
                        (option, constant) -> constant.constant().getClass().equals(option))
                .allMatch(ref -> ref);
    }

    public enum CompileAnnotation {
        NATIVE("Native", Constant.StringConstant.class),
        NATIVE_METHOD("NativeMethod", Constant.StringConstant.class);

        @Getter
        private final String annotationName;

        @Getter
        private final List<Class<? extends Constant>> options;

        @SafeVarargs
        CompileAnnotation(String name, Class<? extends Constant>... clazz) {
            this.annotationName = name;
            this.options = Arrays.stream(clazz).toList();
        }
    }
}
