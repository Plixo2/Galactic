package de.plixo.atic.path;

import de.plixo.atic.files.TreeUnit;
import de.plixo.atic.lexer.Node;

import java.io.File;

public final class PathUnit extends PathEntity {

    public Node node;
    public File file;

    public PathUnit(TreeUnit unit) {
        super(unit.name(), unit.localName());
        this.node = unit.getRoot();
        this.file = unit.file();
    }


    @Override
    void prettyPrint(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append("Unit: ");
        buffer.append(this.name());
        buffer.append('\n');
    }
}
