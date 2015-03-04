package ru.ifmo.ctddev.bisyarina.implementor;

/**
 * Created by mariashka on 3/1/15.
 */
public class Implementor {
    public static void main(String[] args) {
        if (args == null) {
            System.err.println("Argument array is null");
            return;
        }
        if (args.length == 0 || args[0] == null) {
            System.err.println("Class name not provided");
            return;
        }

    }
}
