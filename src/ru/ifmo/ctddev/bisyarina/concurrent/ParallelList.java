package ru.ifmo.ctddev.bisyarina.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ParallelList implements ListIP {
    @Override
    public <T> T maximum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        BiFunction<T, T, T> f = (a, b) -> comparator.compare(a, b) < 0 ? b : a;
        BiFunction<Integer, Integer, Supplier<T>> g = (l, r) -> () -> {
            Monoid<T> monoid = new Monoid<>(f);
            for (int j = l; j < r; j++) {
                monoid.process(list.get(j));
            }
            return monoid.get();
        };
        ParallelInvoker<T> parallelInvoker = new ParallelInvoker<>(i, list.size(), g);
        return parallelInvoker.getAll(new Monoid<>(f));
    }

    @Override
    public <T> T minimum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        BiFunction<T, T, T> f = (a, b) -> comparator.compare(a, b) > 0 ? b : a;
        BiFunction<Integer, Integer, Supplier<T>> g = (l, r) -> () -> {
            Monoid<T> monoid = new Monoid<>(f);
            for (int j = l; j < r; j++) {
                monoid.process(list.get(j));
            }
            return monoid.get();
        };
        ParallelInvoker<T> parallelInvoker = new ParallelInvoker<>(i, list.size(), g);
        return parallelInvoker.getAll(new Monoid<>(f));
    }

    @Override
    public <T> boolean all(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        BiFunction<Boolean, Boolean, Boolean> f = (a, b) -> a&b;
        BiFunction<Integer, Integer, Supplier<Boolean>> g = (l, r) -> () -> {
            Monoid<Boolean> monoid = new Monoid<>(f, true);
            for (int j = l; j < r; j++) {
                monoid.process(predicate.test(list.get(j)));
            }
            return monoid.get();
        };

        ParallelInvoker<Boolean> parallelInvoker = new ParallelInvoker<>(i, list.size(), g);
        return parallelInvoker.getAll(new Monoid<>(f, true));
    }

    @Override
    public <T> boolean any(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        BiFunction<Boolean, Boolean, Boolean> f = (a, b) -> a|b;
        BiFunction<Integer, Integer, Supplier<Boolean>> g = (l, r) -> () -> {
            Monoid<Boolean> monoid = new Monoid<>(f, false);
            for (int j = l; j < r; j++) {
                monoid.process(predicate.test(list.get(j)));
            }
            return monoid.get();
        };
        ParallelInvoker<Boolean> parallelInvoker = new ParallelInvoker<>(i, list.size(), g);
        return parallelInvoker.getAll(new Monoid<>(f, false));
    }

    @Override
    public String concat(int i, List<?> list) throws InterruptedException {
        BiFunction<StringBuilder, StringBuilder, StringBuilder> f = StringBuilder::append;
        BiFunction<Integer, Integer, Supplier<StringBuilder>> g = (l, r) -> () -> {
            StringBuilder builder = new StringBuilder();
            for (int j = l; j < r; j++) {
                builder.append(list.get(j).toString());
            }
            return builder;
        };
        ParallelInvoker<StringBuilder> parallelInvoker = new ParallelInvoker<>(i, list.size(), g);
        return parallelInvoker.getAll(new Monoid<>(f)).toString();
    }

    @Override
    public <T> List<T> filter(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        BiFunction<List<T>, List<T>, List<T>> f = (a, b) -> {
            a.addAll(b);
            return a;
        };
        BiFunction<Integer, Integer, Supplier<List<T>>> g = (l, r) -> () -> {
            List<T> res = new ArrayList<>();
            for (int j = l; j < r; j++) {
                if (predicate.test(list.get(j)))
                    res.add(list.get(j));
            }
            return res;
        };
        ParallelInvoker<List<T>> parallelInvoker = new ParallelInvoker<>(i, list.size(), g);
        return parallelInvoker.getAll(new Monoid<>(f));
    }

    @Override
    public <T, U> List<U> map(int i, List<? extends T> list, Function<? super T, ? extends U> function) throws InterruptedException {
        BiFunction<List<U>, List<U>, List<U>> f = (a, b) -> {
            a.addAll(b);
            return a;
        };
        BiFunction<Integer, Integer, Supplier<List<U>>> g = (l, r) -> () -> {
            List<U> res = new ArrayList<>();
            for (int j = l; j < r; j++) {
                res.add(function.apply(list.get(j)));
            }
            return res;
        };
        ParallelInvoker<List<U>> parallelInvoker = new ParallelInvoker<>(i, list.size(), g);
        return parallelInvoker.getAll(new Monoid<>(f));
    }
}
