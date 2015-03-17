package ru.ifmo.ctddev.bisyarina.concurrent;

import java.lang.reflect.Array;
import java.util.function.Supplier;

public class ParallelInvoker <T> {
    Thread[] threads;
    T[] results;
    boolean[] taken;
    int size;
    ParallelInvoker(Class<T> cl, int maxAmount) {
        threads = new Thread[maxAmount];
        results = (T[]) Array.newInstance(cl, maxAmount);
        size = 0;
    }

    void add(Supplier<T> supplier) {
        taken[size] = false;
        threads[size] = new Thread(new Runnable() {
            @Override
            public void run() {
                results[size] = supplier.get();
            }
        });
        threads[size].start();
        size++;
    }

    T get() {
        while (true) {
            for (int i = 0; i < size; i++) {
                if (!threads[i].isAlive() && !taken[i]) {
                    taken[i] = true;
                    return results[i];
                }
            }
        }
    }
}
