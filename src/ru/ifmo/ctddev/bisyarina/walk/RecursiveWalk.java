package ru.ifmo.ctddev.bisyarina.walk;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

/**
 * Created by mariashka on 2/15/15.
 */
public class RecursiveWalk {
    public static void main(String[] args) {
        HashFileVisitor visitor = new HashFileVisitor();

        try {
            Path input = Paths.get(args[0]);
            List<String> list = Files.readAllLines(input, Charset.forName("UTF-8"));
            for (String line : list) {
                Path start = Paths.get(line);
                Files.walkFileTree(start, visitor);
            }
        } catch (IOException e) {
            System.err.println("Input file error: " + args[0] + " " + e.toString());
        }

        List<HashedFile> files = visitor.getFiles();
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8")) {
            for (int i = 0; i < files.size(); i++) {
                Integer currHash = files.get(i).getHash();
                String path = files.get(i).getPath().toString();
                String curr = files.get(i).getHashString();
                curr += " " + path + "\r\n";
                writer.write(curr);
            }
        } catch (IOException e) {
            System.err.println("Output file error: " + args[0] + " " + e.toString());
        }
    }
}
