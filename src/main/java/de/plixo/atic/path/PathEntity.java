package de.plixo.atic.path;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Iterator;

@AllArgsConstructor
public sealed abstract class PathEntity permits PathDir, PathUnit {

    @Getter
    private final String name;

    @Getter
    private final String localName;

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        this.prettyPrint(buffer, "", "");
        return buffer.toString();
    }

    abstract void prettyPrint(StringBuilder buffer, String prefix, String childrenPrefix);

}
