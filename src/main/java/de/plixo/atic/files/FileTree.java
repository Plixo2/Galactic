package de.plixo.atic.files;

import de.plixo.atic.Language;
import de.plixo.atic.exceptions.FileIOError;
import de.plixo.atic.path.PathDir;
import de.plixo.atic.path.PathEntity;
import de.plixo.atic.path.PathUnit;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTree {

    @Getter
    @Accessors(fluent = true)
    private final File root_file;

    @Getter
    @Accessors(fluent = true)
    public final TreeEntry root_entry;


    @Getter
    public List<Thread> threads;

    public FileTree(File root, Language.ParseConfig config) {
        this.root_file = root;
        threads = new ArrayList<>();
        root_entry = TreeEntry.getEntry("", root, config, threads);
        if (root_entry == null) {
            throw new FileIOError(
                    "cant read root file " + root.getAbsolutePath() + " as a unit for directory");
        }
    }

    public PathEntity toPath() {
        var startTime = System.currentTimeMillis();
        threads.forEach(ref -> {
            try {
                ref.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Waited " + (System.currentTimeMillis() - startTime) + "ms");
        return switch (this.root_entry) {
            case TreePath path -> new PathDir(path);
            case TreeUnit unit -> new PathUnit(unit);
        };
    }

    @Override
    public String toString() {
        return root_entry.toString();
    }
}
