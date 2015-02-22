package ru.ifmo.ctddev.bisyarina.walk;

/**
 * Created by mariashka on 2/15/15.
 */
public class HashedFile {
    private static final int LENGTH = 8;

    public static String getHashString(Integer hash) {
        String hashString = Integer.toUnsignedString(hash, 16) ;
        String zeroString = "";
        for (int i = 0; i < LENGTH - hashString.length(); i++) {
            zeroString += "0";
        }
        return zeroString + hashString;
    }
}
