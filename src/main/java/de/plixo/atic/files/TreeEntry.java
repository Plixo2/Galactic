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
import java.util.List;

public sealed abstract class TreeEntry permits TreePath, TreeUnit {
    @Getter
    @Accessors(fluent = true)
    private final String localName;

    @Getter
    @Accessors(fluent = true)
    private final String name;

    public TreeEntry(String localName, String name) {
        this.localName = localName;
        this.name = name;
    }


    public static @Nullable TreeEntry getEntry(String path, File file, Language.ParseConfig config,
                                               List<Thread> threads) {
        var absolutePath = file.getAbsolutePath();
        var localName = FilenameUtils.getBaseName(absolutePath);
        var name = path + "." + localName;
        if (path.isEmpty()) {
            name = localName;
        }
        if (file.isDirectory()) {
            var treePath = new TreePath(localName, name);
            var files = file.listFiles();
            if (files != null) {
                for (File children : files) {
                    LanguageError.errorFile = children;
                    var entry = getEntry(name, children, config, threads);
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

                var treeUnit = new TreeUnit(localName, name, file, dummy);

                Thread thread = Thread.startVirtualThread(() -> {
                    String src;
                    try {
                        src = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new FileIOError("could not read unit " + localName, e);
                    }
                    treeUnit.setRoot(config.lexer().buildTree(src));
                });
                threads.add(thread);

                return treeUnit;
            }
            return null;
        } else {
            return null;
        }
    }
}
