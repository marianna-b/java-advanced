package ru.ifmo.ctddev.bisyarina.implementor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by mariashka on 3/3/15.
 */
public class ImplementationGenerator {
    private static String sep = System.getProperty("line.separator");

    public static String toStringMethod(Method m) {
        String current = "";
        current += getAccessModString(m.getModifiers()) + m.getReturnType().getCanonicalName() + " " + m.getName();
        current += "(" + toStringParameters(m.getParameterTypes()) + ")";

        current += "{" + sep;
        current += "return " + getDefaultValueString(m.getReturnType()) + ";";
        current += sep + "}";
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
        String curr = "";
        for (int i = 0; i < parameterTypes.length; i++) {
            curr += parameterTypes[i].getCanonicalName() + " a" + Integer.toString(i);
            if (i < parameterTypes.length - 1) {
                curr += ", ";
            }
        }
        return curr;
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
        String current = "";
        current += getAccessModString(constructor.getModifiers()) + name;
        current += "(" + toStringParameters(constructor.getParameterTypes()) + ")";

        Class<?>[] exceptions = constructor.getExceptionTypes();
        if (exceptions.length > 0) {
            current += " throws " + toStringParameterList(exceptions);
        }

        current += "{" + sep;
        current += "super(" + getDefaultVarNameList(constructor.getParameterCount()) + ");\n";
        current += sep + "}";
        return current;
    }

    private static String getDefaultVarNameList(int l) {
        String current = "";
        for (int i = 0; i < l; i++) {
            current += "a" + Integer.toString(i);
            if (i < l - 1) {
                current += ", ";
            }
        }
        return current;
    }

    public static String toStringParameterList(Class<?>[] parameterTypes) {
        String curr = "";
        for (int i = 0; i < parameterTypes.length; i++) {
            curr += parameterTypes[i].getCanonicalName();
            if (i < parameterTypes.length - 1) {
                curr += ", ";
            }
        }
        return curr;
    }
}
