package de.plixo.galactic.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class Signature {
    private final @Nullable Type returnType;
    private final List<Type> arguments;


}
