package de.plixo.galactic.codegen;

import de.plixo.galactic.boundary.JVMLoadedClass;
import de.plixo.galactic.boundary.LoadedBytecode;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * Output of the code generator
 *
 * @param output
 */
public record GeneratedCode(List<JarOutput> output) {

    /**
     * Dumps the generated code to the given directory
     *
     * @param file the directory to write to
     */
    @SneakyThrows
    public void dump(File file) {
        var _ = file.mkdirs();
        if (!file.isDirectory()) {
            throw new IOException("file is not a directory");
        }
        deleteClassFiles(file);
        var absolutePath = file.getAbsolutePath();
        for (var jarOutput : output) {
            var path = STR."\{absolutePath}/\{jarOutput.path()}";
            FileUtils.writeByteArrayToFile(new File(path), jarOutput.data());
        }
    }

    private static void deleteClassFiles(File fileToDelete) {
        File[] allContents = fileToDelete.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteClassFiles(file);
            }
        }
        if (fileToDelete.getName().endsWith(".class") || fileToDelete.isDirectory()) {
            var _ = fileToDelete.delete();
        }
    }

    public void write(OutputStream out, Manifest manifest, @Nullable LoadedBytecode loadedClasses)
            throws IOException {
        var jvmManifest = manifest.getJVMManifest();
        JarOutputStream target = new JarOutputStream(out, jvmManifest);
        var now = System.currentTimeMillis();
        for (var jarOutput : output) {
            var entry = new JarEntry(jarOutput.path());
            target.putNextEntry(entry);
            var data = jarOutput.data();
            target.write(data, 0, data.length);
            entry.setTime(now);
            target.closeEntry();
        }

        if (loadedClasses != null) {
            var classes = loadedClasses.getClasses().stream().toList();
            for (JVMLoadedClass loadedClass : classes) {
                var jvmDestination = loadedClass.getJVMDestination();
                var entry = new JarEntry(STR."\{jvmDestination}.class");
                target.putNextEntry(entry);
                var data = loadedClass.getData();
                target.write(data, 0, data.length);
                entry.setTime(now);
                target.closeEntry();
            }
        }

        target.close();
    }

    public record Manifest(@Nullable String mainClass, String version) {

        private java.util.jar.Manifest getJVMManifest() {
            java.util.jar.Manifest jvmManifest = new java.util.jar.Manifest();
            jvmManifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, version);
            if (mainClass != null) {
                jvmManifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);
            }
            return jvmManifest;
        }
    }
}
