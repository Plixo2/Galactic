package de.plixo.galactic.codegen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public class JarOutput {
    // Path to the class file, with .class at the end
    private final String path;
    private final byte[] data;
}
