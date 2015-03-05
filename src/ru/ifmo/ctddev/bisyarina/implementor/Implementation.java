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
    private Class c;
    private String name;

    private NavigableSet<Method> methods = new TreeSet<>(new Comparator<Method>() {
        @Override
        public int compare(Method m1, Method m2) {
            return toComparingString(m1).compareTo(toComparingString(m2));
        }

        private String toComparingString(Method m1) {
            return m1.getName() + ImplementationGenerator.toStringParameterList(m1.getParameterTypes());
        }
    });

    private Constructor[] constructors;
    private TreeSet<String> imports = new TreeSet<>();

    public void implement(Class<?> token, File root) throws ImplerException {
        if (token.isPrimitive() || token.isArray()) {
            throw new ImplerException("Primitive types or arrays can't be implemented");
        }
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
        initImports();
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(getImplPath(root) + getName() + ".java"), "UTF-8")) {
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
        Class[] interfaces = c.getInterfaces();
        for (Class anInterface : interfaces) {
            addInterfaceMethods(anInterface);
        }
    }

    private void addInterfaceMethods(Class cl) {
        Class[] interfaces = cl.getInterfaces();
        for (Class anInterface : interfaces) {
            addInterfaceMethods(anInterface);
        }
        Method[] m = cl.getDeclaredMethods();
        for (Method aM : m) {
            int modifiers = aM.getModifiers();
            if (!Modifier.isPrivate(modifiers)) {
                if (!aM.isDefault() && !Modifier.isStatic(aM.getModifiers())) {
                    addMethod(aM);
                }
            }
        }
    }

    private void initSuperClassMethods(Class c) {
        if (c == null) {
            return;
        }
        initSuperClassMethods(c.getSuperclass());
        Method[] m = c.getDeclaredMethods();
        for (Method aM : m) {
            int modifiers = aM.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                addMethod(aM);
            } else {
                if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
                    removeMethod(aM);
                }
            }
        }
    }

    private void removeMethod(Method m) {
        methods.remove(m);
    }

    private void initImports() {
        imports.add(c.getPackage().getName() + ".*");
        for (Method method : methods) {
            Class[] parameters = method.getParameterTypes();
            if (!method.getReturnType().isPrimitive()) {
                if (!method.getReturnType().isArray()) {
                    imports.add(method.getReturnType().getCanonicalName());
                } else {
                    imports.add(method.getReturnType().getComponentType().getCanonicalName());
                }
            }

            for (Class parameter : parameters) {
                if (!parameter.isPrimitive()) {
                    if (!parameter.isArray()) {
                        imports.add(parameter.getCanonicalName());
                    } else {
                        imports.add(parameter.getComponentType().getCanonicalName());
                    }
                }
            }
        }

        for (Constructor constructor : constructors) {
            Class[] parameters = constructor.getParameterTypes();
            for (Class parameter : parameters) {
                if (!parameter.isPrimitive()) {
                    if (!parameter.isArray()) {
                        imports.add(parameter.getCanonicalName());
                    } else {
                        imports.add(parameter.getComponentType().getCanonicalName());
                    }
                }
            }

            Class[] exceptions = constructor.getExceptionTypes();
            for (Class exception : exceptions) {
                if (!exception.isPrimitive()) {
                    if (!exception.isArray()) {
                        imports.add(exception.getCanonicalName());
                    } else {
                        imports.add(exception.getComponentType().getCanonicalName());
                    }
                }
            }
        }
    }

    private void addMethod(Method m) {
        methods.add(m);
    }

    public String toString() {
        String file = "";

        file += ImplementationGenerator.toStringPackage(c.getPackage());
        for (String anImport : imports) {
            file += ImplementationGenerator.toStringImport(anImport) + "\n\n";
        }

        file += ImplementationGenerator.toStringClass(c, getName()) + " {\n\n";

        for (Constructor constructor : constructors) {
            file += ImplementationGenerator.toStringConstructor(constructor, getName()) + "\n\n";
        }
        for (Method method : methods) {
            file += ImplementationGenerator.toStringMethod(method) + "\n\n";
        }

        file += "}";
        return file;
    }

    private String getName() {
        return name;
    }
}


