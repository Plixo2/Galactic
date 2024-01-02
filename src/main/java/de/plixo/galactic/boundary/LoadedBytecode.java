package de.plixo.galactic.boundary;

import de.plixo.galactic.files.ObjectPath;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Remembers all loaded classes from the classpath
 */
public class LoadedBytecode {

    private final Map<ObjectPath, JVMLoadedClass> classes = new HashMap<>();


    public @Nullable JVMLoadedClass getClass(ObjectPath path) {
        return classes.get(path);
    }

    public void putClass(ObjectPath path, JVMLoadedClass loadedClass) {
        classes.put(path, loadedClass);
    }

    public boolean containsClass(ObjectPath path) {
        return classes.containsKey(path);
    }

    public Collection<JVMLoadedClass> getClasses() {
        return classes.values();
    }

}
