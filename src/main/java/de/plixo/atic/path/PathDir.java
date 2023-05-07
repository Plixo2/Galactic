package de.plixo.atic.path;

import de.plixo.atic.files.TreeEntry;
import de.plixo.atic.files.TreePath;
import de.plixo.atic.files.TreeUnit;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PathDir extends PathEntity {

    @Getter
    private List<PathUnit> units;

    @Getter
    private List<PathDir> subDirs;


    public PathDir(TreePath path) {
        super(path.name(), path.localName());
        this.units = new ArrayList<>();
        this.subDirs = new ArrayList<>();
        for (TreeEntry entry : path.entries()) {
            if (entry instanceof TreePath sub) {
                subDirs.add(new PathDir(sub));
            } else if (entry instanceof TreeUnit unit) {
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
