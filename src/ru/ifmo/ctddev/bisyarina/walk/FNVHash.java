package ru.ifmo.ctddev.bisyarina.walk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mariashka on 2/15/15.
 */
public class FNVHash implements HashAlgorithm {
    private static final Integer p = Integer.parseUnsignedInt("16777619");
    private int offset;
    private int len;
    private Integer hash;

    FNVHash() {
        hash = Integer.parseUnsignedInt("2166136261");
    }

    @Override
    public Integer getHash(InputStream in) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        int c;
        while ((c = bufferedInputStream.read()) >= 0) {
            hash = (hash * 0x01000193) ^ (c & 0xff);
        }
        return hash;
    }
}
