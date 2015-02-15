package ru.ifmo.ctddev.bisyarina.walk;

import java.nio.file.Path;

/**
 * Created by mariashka on 2/15/15.
 */
public class HashedFile {
    private final Integer hash;
    private final Path path;
    private final int LENGTH = 8;

    HashedFile(Path path, Integer hash) {
        this.path = path;
        this.hash = hash;
    }

    public Integer getHash() {
        return hash;
    }

    public String getHashString() {
        String hashString = Integer.toUnsignedString(hash, 16) ;
        String zeroString = "";
        for (int i = 0; i < LENGTH - hashString.length(); i++) {
            zeroString += "0";
        }
        return zeroString + hashString;
    }

    public Path getPath() {
        return path;
    }
}
