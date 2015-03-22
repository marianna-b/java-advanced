package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.function.Supplier;

import static java.lang.reflect.Array.newInstance;

public class ParallelInvoker <T> {
    Thread[] threads;
    T[] results;
    int size;
    @SafeVarargs
    ParallelInvoker(int maxAmount, T ... args) {
        threads = new Thread[maxAmount];
        results = (T[]) newInstance(args.getClass().getComponentType(), maxAmount);
        size = 0;
    }

    void add(Supplier<? extends T> supplier) {
        int curr_size = size;
        threads[size] = new Thread(() -> results[curr_size] = supplier.get());
        threads[size].start();
        size++;
    }

    T getAll(Monoid<T> monoid) throws InterruptedException {
        for (int i = 0; i < size; i++) {
            threads[i].join();
            monoid.process(results[i]);
        }
        return monoid.get();
    }
}
