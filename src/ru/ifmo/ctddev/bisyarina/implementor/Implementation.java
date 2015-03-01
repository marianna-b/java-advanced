package ru.ifmo.ctddev.bisyarina.implementor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mariashka on 3/1/15.
 */
public class Implementation {
    private Class c;
    private String name;

    private List<Field> fields;
    private List<Method> methods;
    private List<Class> subClasses;
    private List<Constructor> constructors;
    private List<String> imports;

    private List<Implementation> subClassImpl;

    public Implementation(String name) {
        try {
            c = Class.forName(name);
            this.name = name + "Impl";
            fields = Arrays.asList(c.getDeclaredFields());
            constructors = Arrays.asList(c.getDeclaredConstructors());
            subClasses = Arrays.asList(c.getDeclaredClasses());

            initMethods();
            initImports();
        } catch (ClassNotFoundException e) {
            System.err.println("Class was not found");
        }
    }

    private void initImports() {
        for (Field field : fields) {
            imports.add(field.getType().getCanonicalName());
        }
        for (Constructor constructor : constructors) {
            Class[] parameters = constructor.getParameterTypes();
            for (Class parameter : parameters) {
                imports.add(parameter.getCanonicalName());
            }
        }
        for (Method method : methods) {
            Class[] parameters = method.getParameterTypes();
            for (Class parameter : parameters) {
                imports.add(parameter.getCanonicalName());
            }
        }
    }

    private void initMethods() {
        Class curr = c;
        while (curr != null) {
            initMethodsByClass(curr);
            curr = curr.getSuperclass();
        }
    }

    private void initMethodsByClass(Class curr) {
        int size = methods.size();
        Method[] currMethods = curr.getDeclaredMethods();
        for (Method currMethod : currMethods) {
            addMethod(currMethod, size);
        }
        Class[] interfaces = curr.getInterfaces();
        for (Class anInterface : interfaces) {
            initMethodsByClass(anInterface);
        }
    }

    private void addMethod(Method m, int size) {
        int modifiers = m.getModifiers();

        if (modifiers == Modifier.PUBLIC || modifiers == Modifier.PROTECTED
                || (m.getDeclaringClass() == c && modifiers == Modifier.PRIVATE)) {
            String currName = m.getName();
            Class<?>[] currParameters = m.getParameterTypes();
            for (int j = 0; j < size; j++) {
                if (!methods.get(j).getName().equals(currName) || !equalParameters(currParameters, methods.get(j).getParameterTypes())) {
                    methods.add(m);
                }
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

    private String toStringConstructor(int idx) {
        return null;
    }

    private String toStringSubClass(int idx) {
        return null;
    }

    private String toStringField(int idx) {
        return null;
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
