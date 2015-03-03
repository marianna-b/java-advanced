package ru.ifmo.ctddev.bisyarina.implementor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
        Implementation implementation = new Implementation(args[0]);

        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(implementation.getName() + ".java"), "UTF-8")) {
            writer.write(implementation.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
