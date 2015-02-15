package ru.ifmo.ctddev.bisyarina.walk;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mariashka on 2/15/15.
 */
public interface HashAlgorithm {
    public Integer getHash(InputStream in) throws IOException;
}
