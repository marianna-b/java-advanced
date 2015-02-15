package ru.ifmo.ctddev.bisyarina.walk;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariashka on 2/15/15.
 */
public class HashFileVisitor extends SimpleFileVisitor<Path> {
    private final List<HashedFile> files = new ArrayList<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            HashAlgorithm hash = new FNVHash();
            Integer h = hash.getHash(in);
            files.add(new HashedFile(file, h));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException e) {
        if (e != null) {
            System.err.println(file.toString() + " " + e.toString());
            files.add(new HashedFile(file, 0));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException e) {
        return FileVisitResult.CONTINUE;
    }

    public List<HashedFile> getFiles() {
        return files;
    }
}
