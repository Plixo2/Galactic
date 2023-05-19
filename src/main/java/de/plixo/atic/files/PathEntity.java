package de.plixo.atic.files;

import de.plixo.atic.lexer.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@AllArgsConstructor
public sealed abstract class PathEntity permits PathEntity.PathDir, PathEntity.PathUnit {

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

    public static final class PathDir extends PathEntity {

        @Getter
        private final List<PathUnit> units;

        @Getter
        private final List<PathDir> subDirs;


        public PathDir(TreeEntry.TreePath path) {
            super(path.name(), path.localName());
            this.units = new ArrayList<>();
            this.subDirs = new ArrayList<>();
            for (TreeEntry entry : path.entries()) {
                if (entry instanceof TreeEntry.TreePath sub) {
                    subDirs.add(new PathDir(sub));
                } else if (entry instanceof TreeEntry.TreeUnit unit) {
                    units.add(new PathUnit(unit));
                }
            }
        }

        @Override
        void prettyPrint(StringBuilder buffer, String prefix, String childrenPrefix) {
            buffer.append(prefix);
            buffer.append(this.name());
            buffer.append('\n');
            for (var it = this.units().iterator(); it.hasNext(); ) {
                var next = it.next();
                if (it.hasNext()) {
                    next.prettyPrint(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
                } else {
                    next.prettyPrint(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
                }
            }
            for (Iterator<PathDir> it = this.subDirs().iterator(); it.hasNext(); ) {
                var next = it.next();
                if (it.hasNext()) {
                    next.prettyPrint(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
                } else {
                    next.prettyPrint(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
                }
            }

        }
    }

    public static final class PathUnit extends PathEntity {

        @Getter
        private final Node node;
        @Getter
        private final File file;

        public PathUnit(TreeEntry.TreeUnit unit) {
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
}
