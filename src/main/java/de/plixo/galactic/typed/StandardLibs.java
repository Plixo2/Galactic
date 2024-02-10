package de.plixo.galactic.typed;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StandardLibs {
    private final String packageName;
    private final List<Lib> imports;

    public record Lib(String file, String name) {

    }
}
