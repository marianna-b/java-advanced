package ru.ifmo.ctddev.bisyarina.implementor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mariashka on 3/1/15.
 */
public class Implementation {
    private Class c;
    private String name;

    private Package pack;
    private List<Method> methods = new LinkedList<>();
    private List<String> imports = new ArrayList<>();

    public Implementation(String name) {
        try {
            c = Class.forName(name);
            this.name = name + "Impl";

            initPackage();
            initInterfaceMethods();
            initSuperClassMethods(c);
            initImports();

        } catch (ClassNotFoundException e) {
            System.err.println("Class was not found");
        }
    }

    private void initPackage() {
        pack = c.getPackage();
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
            if (Modifier.isProtected(modifiers) || Modifier.isPublic(modifiers)) {
                methods.add(aM);
            }
        }
    }

    private void initImports() {
        List<String> currImports = new ArrayList<>();
        currImports.add(c.getCanonicalName());
        for (Method method : methods) {
            Class[] parameters = method.getParameterTypes();
            currImports.add(method.getReturnType().getCanonicalName());

            for (Class parameter : parameters) {
                currImports.add(parameter.getCanonicalName());
            }
        }

        Collections.sort(currImports);
        for (String currImport : currImports) {
            if (imports.size() > 0) {
                if (!imports.get(imports.size() - 1).equals(currImport)) {
                    imports.add(currImport);
                }
            } else {
                imports.add(currImport);
            }
        }
    }

    private void initSuperClassMethods(Class c) {
        if (c == null) {
            return;
        }
        initSuperClassMethods(c.getSuperclass());
        Method[] m = c.getDeclaredMethods();
        int size = methods.size();
        for (Method aM : m) {
            int modifiers = aM.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                methods.add(aM);
            } else {
                if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
                    removeMethod(aM, size);
                }
            }
        }
    }

    private void removeMethod(Method m, int size) {
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
        Method m = methods.get(idx);
        String current = "";
        current += getAccessModString(m) + m.getReturnType().getName() + " " + m.getName();
        current += "(" + toStringParameters(m.getParameterTypes()) + ")";
        current += "{" + "\n";
        current += "return " + getDefaultValueString(m.getReturnType()) + ";";
        current += "\n" + "}";
        return current;
    }

    private String getAccessModString(Method m) {
        int modifiers = m.getModifiers();
        if (Modifier.isProtected(modifiers)) {
            return "protected ";
        }
        if (Modifier.isPublic(modifiers)) {
            return "public ";
        }
        if (Modifier.isPrivate(modifiers)) {
            return "private ";
        }
        return "";
    }

    private String getDefaultValueString(Class cl) {
        if (c.isPrimitive()) {
            if (c.equals(boolean.class)) {
                return "false";
            } else {
                if (c.equals(char.class)) {
                    return "'" + "\u0000" + "'";
                } else {
                    return "0";
                }
            }
        } else {
            return "null";
        }
    }

    private String toStringParameters(Class<?>[] parameterTypes) {
        String curr = "";
        for (int i = 0; i < parameterTypes.length; i++) {
            curr += parameterTypes[i].getName();
            if (i < parameterTypes.length - 1) {
                curr += ", ";
            }
        }
        return curr;
    }

    private String toStringImport(int idx) {
        return "import " + imports.get(idx) + ";";
    }

    public String toString() {
        return null;
    }

    public String getName() {
        return name;
    }
}
