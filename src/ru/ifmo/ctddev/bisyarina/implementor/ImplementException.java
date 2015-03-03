package ru.ifmo.ctddev.bisyarina.implementor;

/**
 * Created by mariashka on 3/3/15.
 */
public class ImplementException extends Exception {
    public ImplementException() {
    }

    public ImplementException(final String message) {
        super(message);
    }

    public ImplementException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ImplementException(final Throwable cause) {
        super(cause);
    }
}
