package ru.ifmo.ctddev.bisyarina.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ParallelList implements ListIP {

    @Override
    public <T> T maximum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        if (i == 0) {
            return null;
        }
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);
        ParallelInvoker<Pair<T, Integer>> parallelInvoker =
                new ParallelInvoker<>(list.getClass().getComponentType(), i);
        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(new Supplier<Pair<T, Integer>>() {
                int idx = finalL;
                @Override
                public Pair<T, Integer> get() {
                    for (int j = finalL; j < r; j++) {
                        if (comparator.compare(list.get(idx), list.get(j)) > 0) {
                            idx = j;
                        }
                    }
                    return new Pair<>(list.get(idx), finalL);
                }
            });
        }

        Pair<T, Integer> min = null;
        for (int j = 0; j < i; j++) {
            Pair<T, Integer> curr = parallelInvoker.get();
            if (min == null) {
                min = curr;
                continue;
            }
            if (comparator.compare(min.getKey(), curr.getKey()) < 0) {
                min = curr;
                continue;
            }
            if (comparator.compare(min.getKey(), curr.getKey()) == 0 && min.getValue() > curr.getValue()) {
                min = curr;
            }
        }
        return min != null ? min.getKey() : null;
    }

    @Override
    public <T> T minimum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        return null;
    }

    @Override
    public <T> boolean all(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        return false;
    }

    @Override
    public <T> boolean any(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        return false;
    }
    @Override
    public String concat(int i, List<?> list) throws InterruptedException {
        return null;
    }

    @Override
    public <T> List<T> filter(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        return null;
    }

    @Override
    public <T, U> List<U> map(int i, List<? extends T> list, Function<? super T, ? extends U> function) throws InterruptedException {
        return null;
    }
}
