package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.lang.reflect.Array.newInstance;

public class ParallelInvoker <T> {
    Thread[] threads;
    T[] results;
    int size;
    @SafeVarargs
    ParallelInvoker(int maxAmount, int listSize, BiFunction<Integer, Integer, Supplier<T>> f, T ... args ) {
        threads = new Thread[maxAmount];
        //noinspection unchecked
        results = (T[]) newInstance(args.getClass().getComponentType(), maxAmount);
        size = 0;

        int interval = listSize / maxAmount + (listSize % maxAmount == 0 ? 0 : 1);
        for (int l = 0; l < listSize; l += interval) {
            int r = Math.min(listSize, l + interval);
            int curr_size = size;
            Supplier<T> supplier = f.apply(l, r);
            threads[size] = new Thread(() -> results[curr_size] = supplier.get());
            threads[size].start();
            size++;
        }
    }

    T getAll(Monoid<T> monoid) throws InterruptedException {
        for (int i = 0; i < size; i++) {
            threads[i].join();
            monoid.process(results[i]);
        }
        return monoid.get();
    }
}
