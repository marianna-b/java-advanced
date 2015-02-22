package ru.ifmo.ctddev.bisyarina.walk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by mariashka on 2/15/15.
 */
public class RecursiveWalk {
    public static void main(String[] args) {
        if (args.length < 2 || args[0] == null || args[1] == null) {
            System.err.println("Invalid arguments");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
             OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8")) {
            String line;
            HashFileVisitor visitor = new HashFileVisitor(writer);
            while ((line = reader.readLine()) != null) {
                Path start = Paths.get(line);
                Files.walkFileTree(start, visitor);
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
