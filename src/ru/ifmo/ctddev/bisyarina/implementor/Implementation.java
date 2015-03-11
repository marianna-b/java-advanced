package ru.ifmo.ctddev.bisyarina.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Implementation implements Impler, JarImpler {
    private Class<?> c;
    private String name;
    private String filePath;

    private final NavigableSet<Method> methods = new TreeSet<>(new Comparator<Method>() {
        @Override
        public int compare(Method m1, Method m2) {
            return toComparingString(m1).compareTo(toComparingString(m2));
        }

        private String toComparingString(Method m1) {
            return m1.getName() + ImplementationGenerator.toStringParameterList(m1.getParameterTypes());
        }
    });

    private Constructor[] constructors;

    @Override
    public void implementJar(Class<?> aClass, File file) throws ImplerException {
        implement(aClass, file);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler.run(null, null, null, filePath + ".java") < 0) {
            throw new ImplerException("Implementation failed");
        }

        try (FileOutputStream stream = new FileOutputStream(filePath + ".jar");
             JarOutputStream out = new JarOutputStream(stream, new Manifest());
             FileInputStream in = new FileInputStream(filePath + ".class")) {

            JarEntry jarAdd = new JarEntry(filePath + ".class");
            out.putNextEntry(jarAdd);

            byte[] buffer = new byte[1024];
            int r;
            while ((r = in.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, r);
            }
        } catch (IOException e) {
            throw new ImplerException(e.getLocalizedMessage());
        }
    }

    public void implement(Class<?> token, File root) throws ImplerException {
        if (Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Final classes can't be implemented");
        }

        c = token;
        this.name = c.getSimpleName() + "Impl";

        initConstructor();
        if (!hasNonPrivateConstructor()) {
            throw new ImplerException("Can't be implemented - doesn't have non private constructor");
        }
        addInterfaceMethods(c);
        initSuperClassMethods(c);

        try {
            filePath = getImplPath(root) + name;
        } catch (IOException e) {
            throw new ImplerException(e.getLocalizedMessage());
        }
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(filePath + ".java"), "UTF-8")) {
            writer.write(toString());
        } catch (IOException e) {
            throw new ImplerException(e.getLocalizedMessage());
        }

    }

    private boolean hasNonPrivateConstructor() {
        if (constructors.length == 0) {
            return true;
        }
        for (Constructor constructor : constructors) {
            if (!Modifier.isPrivate(constructor.getModifiers())) {
                return true;
            }
        }
        return false;
    }

    private void initConstructor() {
        constructors = c.getDeclaredConstructors();
    }

    private String getImplPath(File root) throws IOException {
        String fileSeparator = File.separator;
        String pack = c.getPackage().getName().replace(".", fileSeparator);
        String path = root.getPath().concat(fileSeparator + pack + fileSeparator);

        Files.createDirectories(Paths.get(path));
        return path;
    }

    private void addInterfaceMethods(Class<?> cl) {
        Class<?>[] interfaces = cl.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            addInterfaceMethods(anInterface);
        }
        Method[] m = cl.getDeclaredMethods();
        for (Method aM : m) {
            int modifiers = aM.getModifiers();
            if (!Modifier.isPrivate(modifiers)) {
                if (!aM.isDefault() && !Modifier.isStatic(aM.getModifiers())) {
                    methods.add(aM);
                }
            }
        }
    }

    private void initSuperClassMethods(Class<?> c) {
        if (c == null) {
            return;
        }
        initSuperClassMethods(c.getSuperclass());
        Method[] m = c.getDeclaredMethods();
        for (Method aM : m) {
            int modifiers = aM.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                methods.add(aM);
            } else {
                if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
                    methods.remove(aM);
                }
            }
        }
    }

    public String toString() {
        String file = "";

        file += ImplementationGenerator.toStringPackage(c.getPackage());

        file += ImplementationGenerator.toStringClass(c, name) + " {" + ImplementationGenerator.lineSeparator;

        for (Constructor constructor : constructors) {
            file += ImplementationGenerator.toStringConstructor(constructor, name) + ImplementationGenerator.lineSeparator;
        }
        for (Method method : methods) {
            file += ImplementationGenerator.toStringMethod(method) + ImplementationGenerator.lineSeparator;
        }

        file += "}";
        return file;
    }

}


