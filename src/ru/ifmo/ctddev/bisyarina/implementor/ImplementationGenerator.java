package ru.ifmo.ctddev.bisyarina.implementor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ImplementationGenerator {
    public static String lineSeparator = System.getProperty("line.separator");

    public static String toStringMethod(Method m) {
        String current = "";
        current += getAccessModString(m.getModifiers()) + m.getReturnType().getCanonicalName() + " " + m.getName();
        current += "(" + toStringParameters(m.getParameterTypes()) + ")";

        current += "{" + lineSeparator;
        current += "return " + getDefaultValueString(m.getReturnType()) + ";";
        current += lineSeparator + "}";
        return current;
    }

    private static String getAccessModString(int modifiers) {
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

    private static String getDefaultValueString(Class<?> cl) {
        if (cl.isPrimitive()) {
            if (cl.equals(boolean.class)) {
                return "false";
            }
            if (cl.equals(void.class)) {
                return "";
            }
            return "0";
        }
        return "null";
    }

    private static String toStringParameters(Class<?>[] parameterTypes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parameterTypes.length; i++) {
            builder.append(parameterTypes[i].getCanonicalName());
            builder.append(" a");
            builder.append(i);
            if (i < parameterTypes.length - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static String toStringPackage(Package pack) {
        return "package " + pack.getName() + ";";
    }

    public static String toStringClass(Class<?> c, String name) {
        String res = getAccessModString(c.getModifiers()) + " class " + name;
        if (c.isInterface()) {
            res += " implements ";
        } else {
            res += " extends ";
        }
        res += c.getCanonicalName();
        return res;
    }

    public static String toStringConstructor(Constructor constructor, String name) {
        StringBuilder builder = new StringBuilder();

        builder.append(getAccessModString(constructor.getModifiers()));
        builder.append(name);
        builder.append("(");
        builder.append(toStringParameters(constructor.getParameterTypes()));
        builder.append(")");

        Class<?>[] exceptions = constructor.getExceptionTypes();
        if (exceptions.length > 0) {
            builder.append(" throws ");
            builder.append(toStringParameterList(exceptions));
        }

        builder.append("{");
        builder.append(lineSeparator);
        builder.append("super(");
        builder.append(getDefaultVarNameList(constructor.getParameterCount()));
        builder.append(");");
        builder.append(lineSeparator);
        builder.append("}");
        return builder.toString();
    }

    private static String getDefaultVarNameList(int l) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < l; i++) {
            builder.append("a");
            builder.append(i);
            if (i < l - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static String toStringParameterList(Class<?>[] parameterTypes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parameterTypes.length; i++) {
            builder.append(parameterTypes[i].getCanonicalName());
            if (i < parameterTypes.length - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
}
