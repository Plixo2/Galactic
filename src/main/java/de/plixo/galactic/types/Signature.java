package de.plixo.galactic.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class Signature {
    private final Type returnType;
    private final List<Type> arguments;
}
