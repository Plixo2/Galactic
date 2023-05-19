package de.plixo.atic.files;

import de.plixo.atic.Language;
import de.plixo.atic.exceptions.FileIOError;
import de.plixo.atic.exceptions.LanguageError;
import de.plixo.atic.lexer.Node;
import de.plixo.atic.lexer.Position;
import de.plixo.atic.lexer.Region;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileTree {

    @Getter
    @Accessors(fluent = true)
    private final File root_file;

    @Getter
    @Accessors(fluent = true)
    public final TreeEntry root_entry;

    private final List<Thread> threads;

    public FileTree(File root, Language.ParseConfig config) {
        this.root_file = root;
        threads = new ArrayList<>();
        root_entry = getEntry("", root, config);
        if (root_entry == null) {
            throw new FileIOError(
                    "cant read root file " + root.getAbsolutePath() + " as a unit for directory");
        }
    }

    public @Nullable TreeEntry getEntry(String path, File file, Language.ParseConfig config) {
        var absolutePath = file.getAbsolutePath();
        var localName = FilenameUtils.getBaseName(absolutePath);
        var name = path + "." + localName;
        if (path.isEmpty()) {
            name = localName;
        }
        if (file.isDirectory()) {
            var treePath = new TreeEntry.TreePath(localName, name);
            var files = file.listFiles();
            if (files != null) {
                for (File children : files) {
                    LanguageError.errorFile = children;
                    var entry = getEntry(name, children, config);
                    if (entry != null) {
                        treePath.addEntry(entry);
                    }
                }
            }
            return treePath;
        } else if (file.isFile()) {
            if (FilenameUtils.getExtension(absolutePath).matches(config.filePattern())) {
                var dummyRegion = Region.fromPosition(new Position(-1, -1, -1));
                var dummy = new Node(null, "", false, List.of(), dummyRegion);

                var treeUnit = new TreeEntry.TreeUnit(localName, name, file, dummy);
                Language.TASK_CREATED += 1;
                Thread thread = Thread.startVirtualThread(() -> {
                    String src;
                    try {
                        src = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new FileIOError("could not read unit " + localName, e);
                    }
                    treeUnit.setRoot(config.lexer().buildTree(src));
                });
                thread.setName("Lexer Thread #" + threads.size());
                threads.add(thread);

                return treeUnit;
            }
            return null;
        } else {
            return null;
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
            case TreeEntry.TreePath path -> new PathEntity.PathDir(path);
            case TreeEntry.TreeUnit unit -> new PathEntity.PathUnit(unit);
        };
    }

    @Override
    public String toString() {
        return root_entry.toString();
    }
}
