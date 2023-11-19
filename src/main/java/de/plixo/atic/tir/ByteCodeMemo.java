package de.plixo.atic.tir;

import lombok.SneakyThrows;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ByteCodeMemo {
    public static Map<String, ClassNode> map = new HashMap<>();
    public static Map<ObjectPath, ClassNode> pathMap = new HashMap<>();


    @SneakyThrows
    public static ClassNode getName(String name) {
        var classNode = map.get(name);
        if (classNode != null) {
            return classNode;
        }
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(name);
        cr.accept(cn, 0);
        map.put(name,cn);
        return cn;
    }

    @SneakyThrows
    public static ClassNode getPath(ObjectPath objectPath) {
        var classNode = pathMap.get(objectPath);
        if (classNode != null) {
            return classNode;
        }
        var stream = Context.class.getResourceAsStream(objectPath.asJVMPath());
        if (stream == null) {
            pathMap.put(objectPath,null);
            return null;
        }
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(stream);
        cr.accept(cn, 0);
        pathMap.put(objectPath,cn);
        return cn;
    }

}
