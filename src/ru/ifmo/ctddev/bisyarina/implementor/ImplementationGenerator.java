package ru.ifmo.ctddev.bisyarina.implementor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * The {@link ru.ifmo.ctddev.bisyarina.implementor.ImplementationGenerator ImplementationGenerator} class
 * provides interface to generate {@link java.lang.String} representation of
 * default implementation of class members
 */
public class ImplementationGenerator {
    /**
     * {@link java.lang.String} value representing the line separator
     */
    public static String lineSeparator = System.getProperty("line.separator");

    /**
     * Returns {@link java.lang.String} representation of {@link java.lang.reflect.Method} m
     * default implementation
     *
     * @param m - method to represent
     * @return a {@link java.lang.String} representation of given method
     */
    public static String toStringMethod(Method m) {
        String current = "";
        current += getAccessModString(m.getModifiers()) + m.getReturnType().getCanonicalName() + " " + m.getName();
        current += "(" + toStringParameters(m.getParameterTypes()) + ")";

        current += "{" + lineSeparator;
        current += "return " + getDefaultValueString(m.getReturnType()) + ";";
        current += lineSeparator + "}";
        return current;
    }

    /**
     * Returns {@link java.lang.String} representation of access modifiers
     * from given int value.
     * If impossible to determine access modifiers return empty string.
     *
     * @param modifiers int value
     * @return String representation of access modifier
     */
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

    /**
     * Returns {@link java.lang.String} representation of default value for given {@link java.lang.Class}
     * @param cl class to get default value for
     * @return a String representing default value
     */
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

    /**
     * Returns {@link java.lang.String} representation of parameter list with default value names
     * separated with commas.
     * @param parameterTypes array of {@link java.lang.Class} representing types of parameters
     * @return {@link java.lang.String} of given parameter list and default value names
     */
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

    /**
     * Returns {@link java.lang.String} representing package definition
     * @param pack {@link java.lang.Package} to be defined
     * @return {@link java.lang.String} representing package definition
     */
    public static String toStringPackage(Package pack) {
        return "package " + pack.getName() + ";";
    }

    /**
     * Returns first line of class definition
     *
     * @param name {@link java.lang.String} name of {@link java.lang.Class} to define
     * @param c    interface/class given class is extended/implemented from
     * @return first line of class definition
     */
    public static String toStringClass(String name, Class<?> c) {
        String res = getAccessModString(c.getModifiers()) + " class " + name;
        if (c.isInterface()) {
            res += " implements ";
        } else {
            res += " extends ";
        }
        res += c.getCanonicalName();
        return res;
    }

    /**
     * Returns {@link java.lang.String} representing of constructor only invoking given
     * constructor of superclass
     *
     * @param name {@link java.lang.String} name of class to generate {@link java.lang.reflect.Constructor}
     * @param constructor {@link java.lang.reflect.Constructor} of superclass to use
     * @return {@link java.lang.String} representation of constructor
     */
    public static String toStringConstructor(String name, Constructor constructor) {
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

    /**
     * Returns {@link java.lang.String} representation of default parameter names
     * @param l amount of names needed
     * @return {@link java.lang.String} of default parameter names
     */
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

    /**
     * Returns {@link java.lang.String} representation of parameter list separated with commas.
     * @param parameterTypes array of {@link java.lang.Class} representing types of parameters
     * @return {@link java.lang.String} of given parameter list
     */
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
