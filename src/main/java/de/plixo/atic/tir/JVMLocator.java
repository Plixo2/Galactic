package de.plixo.atic.tir;

import com.google.common.reflect.ClassPath;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

import java.lang.instrument.Instrumentation;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


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
            System.out.println(loadedClasses.length);

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
