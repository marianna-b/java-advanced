package ru.ifmo.ctddev.bisyarina.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides functionality to process list and get result of specified type
 */
public class ParallelInvoker {
    private final ParallelMapper mapper;

    /**
     * Constructs instance invoking parallel computations with given mapper
     * @param mapper mapper to invoke with
     */
    public ParallelInvoker(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Return a list of results of applying given function to given list of arguments
     * splitting it to parts
     * @param maxAmount amount of parts to produce
     * @param list list to process
     * @param f function to apply
     * @param <T> supertype of return values
     * @param <K> supertype of input values
     * @return a list of results of applying given function partly
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    public <T, K> List<T> splitProcess
        (int maxAmount, List<? extends K> list, Function<List<? extends K>, Supplier<T>> f)
            throws InterruptedException {
        List<List<? extends K>> arg = new ArrayList<>();
        int interval = list.size() / maxAmount + (list.size() % maxAmount == 0 ? 0 : 1);
        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            arg.add(list.subList(l, r));
        }
        Function<List<? extends K>, T> g = (subList) -> f.apply(subList).get();
        return mapper.map(g, arg);
    }
}
