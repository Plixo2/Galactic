package test;

import com.google.common.reflect.ClassPath;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;


public class JVMLocator {

    public static void main(String[] args) {
        new JVMLocator().add();
    }

    public void add() {
        ClassPath classPath = null;
        var hashSet = new HashSet<String>();
        try {
//            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
//            System.out.println(classLoadingMXBean.getLoadedClassCount());


            Instrumentation inst = ByteBuddyAgent.install();
            Class[] loadedClasses = inst.getAllLoadedClasses();
            for (Class loadedClass : loadedClasses) {
                hashSet.add(loadedClass.getName());
            }

            classPath = ClassPath.from(ClassLoader.getSystemClassLoader());
            Set<ClassPath.ClassInfo> classes = classPath.getAllClasses();
            for (ClassPath.ClassInfo aClass : classes) {
                hashSet.add(aClass.getName());
            }
            hashSet.forEach(System.out::println);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
