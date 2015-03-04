package ru.ifmo.ctddev.bisyarina.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

/**
 * Created by mariashka on 3/3/15.
 */
public class ImplementException extends ImplerException {
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
