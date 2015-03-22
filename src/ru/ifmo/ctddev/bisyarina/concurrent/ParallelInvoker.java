package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.reflect.Array.newInstance;

/**
 * Provides functionality to process list and get result of specified type
 * @param <T> type of result
 */
public class ParallelInvoker <T> {
    private Thread[] threads;
    private T[] results;
    private int size;

    /**
     * Constructs parallel invoker for given amount of threads, given list, and given function generating supplier
     * for sublist
     * @param maxAmount maximal amount of threads to use
     * @param list list to process
     * @param f function invoked to get answers fornsublists
     * @param <K> type of list elements
     */
    @SafeVarargs
    public <K> ParallelInvoker(int maxAmount, List<? extends K> list, Function<List<? extends K>, Supplier<T>> f, T ... args ) {
        threads = new Thread[maxAmount];
        //noinspection unchecked
        results = (T[]) newInstance(args.getClass().getComponentType(), maxAmount);
        size = 0;

        int interval = list.size() / maxAmount + (list.size() % maxAmount == 0 ? 0 : 1);
        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            int curr_size = size;
            Supplier<T> supplier = f.apply(list.subList(l, r));
            threads[size] = new Thread(() -> results[curr_size] = supplier.get());
            threads[size].start();
            size++;
        }
    }

    /**
     * Returns value computed by given monoid from results of parallel computing
     * @param monoid monoid to compute result
     * @return value computed by given monoid
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    public T get(Monoid<T> monoid) throws InterruptedException {
        for (int i = 0; i < size; i++) {
            threads[i].join();
            monoid.process(results[i]);
        }
        return monoid.get();
    }
}
