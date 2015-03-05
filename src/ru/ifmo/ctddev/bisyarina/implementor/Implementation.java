package ru.ifmo.ctddev.bisyarina.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;


/**
 * Created by mariashka on 3/1/15.
 */
public class Implementation implements Impler {
    private Class<?> c;
    private String name;

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
    private final TreeSet<String> imports = new TreeSet<>();

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
        initInterfaceMethods();
        initSuperClassMethods(c);
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(getImplPath(root) + name + ".java"), "UTF-8")) {
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
        String pack = c.getPackage().getName().replace(".", "/");
        String path = root.getPath().concat("/" + pack + "/");

        Files.createDirectories(Paths.get(path));
        return path;
    }

    private void initInterfaceMethods() {
        Class<?>[] interfaces = c.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            addInterfaceMethods(anInterface);
        }
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

    private Class<?> getType(Class<?> cl) {
        while (cl.isArray()) {
            cl = cl.getComponentType();
        }
        return cl;
    }

    public String toString() {
        String file = "";

        file += ImplementationGenerator.toStringPackage(c.getPackage());

        file += ImplementationGenerator.toStringClass(c, name) + " {\n\n";

        for (Constructor constructor : constructors) {
            file += ImplementationGenerator.toStringConstructor(constructor, name) + "\n\n";
        }
        for (Method method : methods) {
            file += ImplementationGenerator.toStringMethod(method) + "\n\n";
        }

        file += "}";
        return file;
    }
}


