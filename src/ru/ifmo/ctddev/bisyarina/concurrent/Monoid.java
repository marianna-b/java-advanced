package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.function.BiFunction;

/**
 * {@link ru.ifmo.ctddev.bisyarina.concurrent.Monoid} provides functionality
 * for algebraic structure with a single associative binary operation and an identity element
 * @param <T> type of element
 */

class Monoid<T> {
    private final BiFunction<T, T, T> f;
    private boolean identity;
    private T result;

    /**
     * Creates monoid for cases when identity element is unknown
     * @param f binary function to process elements
     */
    Monoid(BiFunction<T, T, T> f) {
        this.f = f;
        identity = true;
    }

    /**
     * Creates monoid for cases when identity element is known
     * @param f binary function to process elements
     * @param identityElem identity element
     */
    Monoid(BiFunction<T, T, T> f, T identityElem) {
        this.f = f;
        this.identity = false;
        this.result = identityElem;
    }

    /**
     * Updates value stored in monoid with given
     * @param elem element to update with
     */
    public void process(T elem) {
        if (identity) {
            result = elem;
            identity = false;
            return;
        }
        result = f.apply(result, elem);
    }

    /**
     * Getter for value stored in monoid.
     * If stored unknown identity element return null
     * @return value stored in monoid
     */
    public T get() {
        return result;
    }
}
