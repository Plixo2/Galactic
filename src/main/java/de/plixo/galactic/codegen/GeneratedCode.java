package de.plixo.galactic.codegen;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public record GeneratedCode(List<JarOutput> output) {


    public void write(OutputStream out, Manifest manifest) throws IOException {
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
