package ru.ifmo.ctddev.bisyarina.implementor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by mariashka on 3/3/15.
 */
public class ImplementationGenerator {

    public static String toStringMethod(Method m) {
        String current = "";
        current += getAccessModString(m.getModifiers()) + m.getReturnType().getSimpleName() + " " + m.getName();
        current += "(" + toStringParameters(m.getParameterTypes()) + ")";
        current += "{" + "\n";
        current += "return " + getDefaultValueString(m.getReturnType()) + ";";
        current += "\n" + "}";
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

    private static String getDefaultValueString(Class cl) {
        if (cl.isPrimitive()) {
            if (cl.equals(boolean.class)) {
                return "false";
            } else {
                if (cl.equals(char.class)) {
                    return "'" + "\u0000" + "'";
                } else {
                    if (cl.equals(void.class)) {
                        return "";
                    } else {
                        return "0";
                    }
                }
            }
        } else {
            return "null";
        }
    }

    private static String toStringParameters(Class<?>[] parameterTypes) {
        String curr = "";
        for (int i = 0; i < parameterTypes.length; i++) {
            curr += parameterTypes[i].getSimpleName() + " a" + Integer.toString(i);
            if (i < parameterTypes.length - 1) {
                curr += ", ";
            }
        }
        return curr;
    }

    public static String toStringImport(String importStr) {
        return "import " + importStr + ";";
    }

    public static String toStringPackage(Package pack) {
        return "package " + pack.getName() + ";";
    }


    public static String toStringClass(Class c, String name) {
        String res = getAccessModString(c.getModifiers()) + " class " + name;
        if (c.isInterface()) {
            res += " implements ";
        } else {
            res += " extends ";
        }
        res += c.getSimpleName();
        return res;
    }
}
