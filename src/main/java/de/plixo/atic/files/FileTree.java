package de.plixo.atic.files;

import de.plixo.atic.Language;
import de.plixo.atic.ParseConfig;
import de.plixo.atic.exceptions.reasons.FileIOFailure;
import de.plixo.atic.exceptions.reasons.ThreadFailure;
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

    public FileTree(File root, ParseConfig config) {
        this.root_file = root;
        threads = new ArrayList<>();
        root_entry = getEntry("", root, config);
        if (root_entry == null) {
            throw new FileIOFailure(root, FileIOFailure.FileType.ROOT).create();
        }
    }

    public @Nullable TreeEntry getEntry(String path, File file, ParseConfig config) {
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
                    var entry = getEntry(name, children, config);
                    if (entry != null) {
                        treePath.addEntry(entry);
                    }
                }
            }
            return treePath;
        } else if (file.isFile()) {
            if (FilenameUtils.getExtension(absolutePath).matches(config.filePattern())) {
                var dummyRegion = Region.fromPosition(file, new Position(-1, -1, -1));
                var dummy = new Node(null, "", false, List.of(), dummyRegion);
                var treeUnit = new TreeEntry.TreeUnit(localName, name, file, dummy);
                Runnable runnable = () -> {
                    String src;
                    try {
                        src = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new FileIOFailure(file, FileIOFailure.FileType.UNIT).create();
                    }
                    treeUnit.setRoot(config.lexer().buildTree(file, src, "unit2"));
                };
                if (config.threaded()) {
                    Thread thread = Thread.startVirtualThread(runnable);
                    Language.TASK_CREATED += 1;
                    thread.setName("Lexer Thread #" + threads.size());
                    threads.add(thread);
                } else {
                    runnable.run();
                }

                return treeUnit;
            }
            return null;
        } else {
            return null;
        }
    }

    public PathEntity toPath() {
        threads.forEach(ref -> {
            try {
                ref.join();
            } catch (InterruptedException e) {
                throw new ThreadFailure(e).create();
            }
        });
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
