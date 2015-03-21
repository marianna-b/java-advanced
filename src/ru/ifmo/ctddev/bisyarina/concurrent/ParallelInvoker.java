package ru.ifmo.ctddev.bisyarina.concurrent;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParallelInvoker <T> {
    Thread[] threads;
    T[] results;
    int size;
    @SafeVarargs
    ParallelInvoker(int maxAmount, T ... args) {
        threads = new Thread[maxAmount];
        results = (T[]) Array.newInstance(args.getClass().getComponentType(), maxAmount);
        size = 0;
    }

    void add(Supplier<? extends T> supplier) {
        int curr_size = size;
        threads[size] = new Thread(new Runnable() {
            @Override
            public void run() {
                results[curr_size] = supplier.get();
            }
        });
        threads[size].start();
        size++;
    }

    T getAll(Function<T[], T> f) throws InterruptedException {
        for (int i = 0; i < size; i++) {
            threads[i].join();
        }
        return f.apply(results);
    }
}
