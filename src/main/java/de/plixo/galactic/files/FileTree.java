package de.plixo.galactic.files;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

/**
 * Generates a FileTreeEntry from a given File
 */
public class FileTree {

    /**
     * Generates a FileTreeEntry from a given File recursively
     * @param file root file of the tree
     * @param filePattern regex pattern for files to include
     * @return root of the FileTree
     */
    public static @Nullable FileTreeEntry generateFileTree(File file, String filePattern) {
        return get(null, file, filePattern);
    }

    private static @Nullable FileTreeEntry get(@Nullable String path, File file, String filePattern) {
        var absolutePath = file.getAbsolutePath();
        var localName = FilenameUtils.getBaseName(absolutePath);
        String name;
        if (path == null) {
            name = localName;
        } else {
            name = path + "." + localName;
        }
        if (file.isDirectory()) {
            var children = new ArrayList<FileTreeEntry>();
            var treePackage = new FileTreeEntry.FileTreePackage(localName, name, children);
            var childFiles = file.listFiles();
            if (childFiles != null) {
                for (var child : childFiles) {
                    var childTree = get(name, child, filePattern);
                    if (childTree != null) {
                        children.add(childTree);
                    }
                }
            }
            return treePackage;
        } else if (file.isFile()) {
            var aticFile = FilenameUtils.getExtension(absolutePath).matches(filePattern);
            if (aticFile) {
                return new FileTreeEntry.FileTreeUnit(localName, name, file);
            }
        }
        return null;
    }

}
