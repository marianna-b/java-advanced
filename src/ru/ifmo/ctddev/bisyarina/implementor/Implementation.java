package ru.ifmo.ctddev.bisyarina.implementor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    private List<Implementation> subClassImpl;

    public Implementation(String name) {
        try {
            c = Class.forName(name);
            this.name = name + "Impl";
            fields = Arrays.asList(c.getDeclaredFields());
            constructors = Arrays.asList(c.getDeclaredConstructors());
            subClasses = Arrays.asList(c.getDeclaredClasses());
            methods = Arrays.asList(c.getDeclaredMethods());
            initBySuperClassMethods();
        } catch (ClassNotFoundException e) {
            // ToDo handle exception
        }
    }

    private void initBySuperClassMethods() {

    }

    public String toString() {
        return null;
    }
}
