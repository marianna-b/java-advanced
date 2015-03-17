package ru.ifmo.ctddev.bisyarina.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;
import java.lang.System;

/**
 * The {@link Implementor} class provides
 * command line interface fot implementing Java classes ans interfaces.
 */

public class Implementor {
    private Implementor() {
    }

    /**
     * Generates jar-file containing implementation of a given class to given jar file.
     * Prints a message in case of an error.
     *
     * @param args String array containing "-jar" option, name of the class to implement,
     * jar file to generate
     */

    public static void main(String[] args) {
        if (args == null || args.length < 3 || args[0] == null) {
            System.err.println("Invalid args");
            return;
        }

        if (args[0].equals("-jar")) {
            if (args[1] == null || args[2] == null) {
                System.err.println("Invalid args");
                return;
            }
            try {
                Class<?> cl = Class.forName(args[1]);
                Implementation impl = new Implementation();
                impl.implementJar(cl, new File(args[2]));
            } catch (ClassNotFoundException e) {
                System.err.println("Invalid class");
            } catch (ImplerException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
