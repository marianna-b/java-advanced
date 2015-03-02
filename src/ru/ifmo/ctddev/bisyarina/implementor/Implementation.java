package ru.ifmo.ctddev.bisyarina.implementor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mariashka on 3/1/15.
 */
public class Implementation {
    private Class c;
    private String name;

    private List<Method> methods = new LinkedList<>();
    private List<String> imports = new ArrayList<>();

    private List<Implementation> subClassImpl;

    public Implementation(String name) {
        try {
            c = Class.forName(name);
            this.name = name + "Impl";
            initMethods(c);
            initImports();
        } catch (ClassNotFoundException e) {
            System.err.println("Class was not found");
        }
    }

    private void initImports() {
        for (Method method : methods) {
            Class[] parameters = method.getParameterTypes();
            imports.add(method.getReturnType().getCanonicalName());
            for (Class parameter : parameters) {
                imports.add(parameter.getCanonicalName());
            }
        }
    }

    private void initMethods(Class c) {
        if (c == null) {
            return;
        }
        initMethods(c.getSuperclass());
        Method[] m = c.getDeclaredMethods();
        int size = methods.size();
        for (int i = 0; i < m.length; i++) {
            int modifiers = m[i].getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                methods.add(m[i]);
            } else {
                if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
                    addMethod(m[i], size);
                }
            }
        }
    }

    private void addMethod(Method m, int size) {
        String currName = m.getName();
        Class[] currParameters = m.getParameterTypes();
        for (int j = 0; j < size; j++) {
            if (methods.get(j).getName().equals(currName) && equalParameters(currParameters, methods.get(j).getParameterTypes())) {
                methods.remove(j);
            }
        }
    }

    private boolean equalParameters(Class<?>[] l, Class<?>[] r) {
        if (l.length != r.length) {
            return false;
        }
        for (int i = 0; i < l.length; i++) {
            if (!l[i].equals(r[i])) {
                return false;
            }
        }
        return true;
    }

    private String toStringMethod(int idx) {
        return null;
    }

    private String toStringImport(int idx) {
        return null;
    }

    public String toString() {
        return null;
    }

    public String getName() {
        return name;
    }
}
