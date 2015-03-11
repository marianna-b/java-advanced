package ru.ifmo.ctddev.bisyarina.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;

/**
 * The {@link ru.ifmo.ctddev.bisyarina.implementor.Implementor} class provides
 * command line interface fot implementing Java classes ans interfaces.
 */

public class Implementor {
    /**
     * Generates jar-file containing implementation of a given class.
     * Prints a message in case of an error.
     *
     * @param args String array containing name of the class to implement
     */

    public static void main(String[] args) {
        if (args == null || args.length == 0 || args[0] == null) {
            System.err.println("Invalid args");
            return;
        }
        try {
            Class<?> cl = Class.forName(args[0]);
            Implementation impl = new Implementation();
            impl.implementJar(cl, new File("."));
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid class");
        } catch (ImplerException e) {
            System.err.println(e.getMessage());
        }
    }
}
