package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.function.BiFunction;

public class Monoid<T> {
    private BiFunction<T, T, T> f;
    private boolean neutral;
    private T result;

    Monoid(BiFunction<T, T, T> f) {
        this.f = f;
        neutral = true;
    }

    Monoid(BiFunction<T, T, T> f, T neutralElem) {
        this.f = f;
        this.neutral = false;
        this.result = neutralElem;
    }

    public void process(T elem) {
        if (neutral) {
            result = elem;
            neutral = false;
            return;
        }
        result = f.apply(result, elem);
    }

    public T get() {
        return result;
    }
}
