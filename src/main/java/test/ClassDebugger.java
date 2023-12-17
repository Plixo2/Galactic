package test;

import de.plixo.atic.common.JsonUtil;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class ClassDebugger {

    @SneakyThrows
    public static void main(String[] args) {
//        ClassReader cr = new ClassReader(getInputStream(TestObj.class));
//        ClassReader cr = new ClassReader(new FileInputStream("resources/test/OutClass.class"));
        ClassReader cr = new ClassReader(getInputStream(Child.class));
        var printWriter = new PrintWriter(new FileOutputStream("resources/jarlog.txt"));
        var classVisitor = new TraceClassVisitor(printWriter);
        printWriter.flush();
        cr.accept(classVisitor, 0);

        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        cr.accept(cw, 0);
        byte[] b = cw.toByteArray();
        var outFile = new File("resources/Child.class");
        JsonUtil.makeFile(outFile);
        FileUtils.writeByteArrayToFile(outFile, b);
    }


    public static void printClass(byte[] byteArray) {
        ClassReader cr = new ClassReader(byteArray);
        var printWriter = new PrintWriter(System.out);
        var classVisitor = new TraceClassVisitor(printWriter);
        printWriter.flush();
        cr.accept(classVisitor, 0);
    }

    private static InputStream getInputStream(Class<?> clazz) {
        String classFile = "/" + clazz.getName().replace('.', '/') + ".class";
        return clazz.getResourceAsStream(classFile);
    }
}
