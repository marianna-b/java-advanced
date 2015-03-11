package ru.ifmo.ctddev.bisyarina.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;

public class Implementor {
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
