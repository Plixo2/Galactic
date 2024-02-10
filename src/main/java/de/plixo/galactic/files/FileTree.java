package de.plixo.galactic.files;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

/**
 * Generates a FileTreeEntry from a given file
 */
public class FileTree {

    /**
     * Generates a FileTreeEntry from a given file recursively
     *
     * @param file        root file of the tree
     * @param filePattern regex pattern for files to include
     * @return root of the FileTree
     */
    public static @Nullable FileTreeEntry generateFileTree(File file, String filePattern) {
        return get(null, file, filePattern);
    }

    /**
     * Recursive helper function for generateFileTree
     * @param path absolute path, seperated by dots, null means it's the root
     * @param file current file to convert
     * @param filePattern regex pattern for files to include
     * @return FileTreeEntry for the file, null if the file is not matching the pattern, or the 'file' is not a file or directory
     */

    private static @Nullable FileTreeEntry get(@Nullable String path, File file,
                                               String filePattern) {
        var absolutePath = file.getAbsolutePath();
        var localName = FilenameUtils.getBaseName(absolutePath);
        String name;
        if (path == null) {
            name = localName;
        } else {
            name = STR."\{path}.\{localName}";
        }
        if (file.isDirectory()) {
            var children = new ArrayList<FileTreeEntry>();
            var childFiles = file.listFiles();
            if (childFiles != null) {
                for (var child : childFiles) {
                    var childTree = get(name, child, filePattern);
                    if (childTree != null) {
                        children.add(childTree);
                    }
                }
            }
            return new FileTreeEntry.FileTreePackage(localName, name, children);
        } else if (file.isFile()) {
            var stellaFile = FilenameUtils.getExtension(absolutePath).matches(filePattern);
            if (stellaFile) {
                return new FileTreeEntry.FileTreeUnit(localName, name, file);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
