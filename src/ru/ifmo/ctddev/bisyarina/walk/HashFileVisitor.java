package ru.ifmo.ctddev.bisyarina.walk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by mariashka on 2/15/15.
 */
public class HashFileVisitor extends SimpleFileVisitor<Path> {
    private OutputStreamWriter writer;

    HashFileVisitor(OutputStreamWriter writer) {
        super();
        this.writer = writer;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            HashAlgorithm hash = new FNVHash();
            Integer h = hash.getHash(in);
            writer.write(HashedFile.getHashString(h) + " " + file.toString() + "\r\n");
        } catch (IOException e) {
            System.err.println(e.toString());
            writer.write(HashedFile.getHashString(0) + " " + file.toString() + "\r\n");
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException {
        if (e != null) {
            System.err.println(e.toString());
            writer.write(HashedFile.getHashString(0) + " " + file.toString() + "\r\n");
        }
        return FileVisitResult.CONTINUE;
    }

}
