package ru.ifmo.ctddev.bisyarina.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ParallelList implements ListIP {

    @Override
    public <T> T maximum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        if (i == 0) {
            return null;
        }
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);
        ParallelInvoker<T> parallelInvoker = new ParallelInvoker<>(i);
        BiFunction<T, T, T> f = (a, b) -> comparator.compare(a, b) < 0 ? b : a;

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;

            parallelInvoker.add(() -> {
                Monoid<T> monoid = new Monoid<>(f);
                for (int j = finalL; j < r; j++) {
                    monoid.process(list.get(j));
                }
                return monoid.get();
            });
        }

        return parallelInvoker.getAll(new Monoid<>(f));
    }

    @Override
    public <T> T minimum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        if (i == 0) {
            return null;
        }
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<T> parallelInvoker = new ParallelInvoker<>(i);
        BiFunction<T, T, T> f = (a, b) -> comparator.compare(a, b) > 0 ? b : a;

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(() -> {
                Monoid<T> monoid = new Monoid<>(f);
                for (int j = finalL; j < r; j++) {
                    monoid.process(list.get(j));
                }
                return monoid.get();
            });
        }

        return parallelInvoker.getAll(new Monoid<>(f));
    }

    @Override
    public <T> boolean all(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        if (i == 0) {
            return false;
        }
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<Boolean> parallelInvoker = new ParallelInvoker<>(i);
        BiFunction<Boolean, Boolean, Boolean> f = (a, b) -> a&b;

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(() -> {
                Monoid<Boolean> monoid = new Monoid<>(f, true);
                for (int j = finalL; j < r; j++) {
                    monoid.process(predicate.test(list.get(j)));
                }
                return monoid.get();
            });
        }

        return parallelInvoker.getAll(new Monoid<>(f, true));
    }

    @Override
    public <T> boolean any(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        if (i == 0) {
            return false;
        }
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<Boolean> parallelInvoker = new ParallelInvoker<>(i);
        BiFunction<Boolean, Boolean, Boolean> f = (a, b) -> a|b;

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(() -> {
                Monoid<Boolean> monoid = new Monoid<>(f, false);
                for (int j = finalL; j < r; j++) {
                    monoid.process(predicate.test(list.get(j)));
                }
                return monoid.get();
            });
        }

        return parallelInvoker.getAll(new Monoid<>(f, false));
    }

    @Override
    public String concat(int i, List<?> list) throws InterruptedException {
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<StringBuilder> parallelInvoker = new ParallelInvoker<>(i);
        BiFunction<StringBuilder, StringBuilder, StringBuilder> f = StringBuilder::append;

        for (int l = 0; l < list.size(); l += interval) {

            int r = Math.min(list.size(), l + interval);
            final int finalL = l;

            parallelInvoker.add(() -> {
                StringBuilder builder = new StringBuilder();
                for (int j = finalL; j < r; j++) {
                    builder.append(list.get(j).toString());
                }
                return builder;
            });
        }

        return parallelInvoker.getAll(new Monoid<>(f)).toString();
    }

    @Override
    public <T> List<T> filter(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        if (i == 0) {
            return null;
        }

        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);
        ParallelInvoker<List<T>> parallelInvoker = new ParallelInvoker<>(i);
        BiFunction<List<T>, List<T>, List<T>> f = (a, b) -> {
            a.addAll(b);
            return a;
        };

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(() -> {
                List<T> res = new ArrayList<>();
                for (int j = finalL; j < r; j++) {
                    if (predicate.test(list.get(j)))
                        res.add(list.get(j));
                }
                return res;
            });
        }

        return parallelInvoker.getAll(new Monoid<>(f));
    }

    @Override
    public <T, U> List<U> map(int i, List<? extends T> list, Function<? super T, ? extends U> function) throws InterruptedException {
        if (i == 0) {
            return null;
        }

        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);
        ParallelInvoker<List<U>> parallelInvoker = new ParallelInvoker<>(i);
        BiFunction<List<U>, List<U>, List<U>> f = (a, b) -> {
            a.addAll(b);
            return a;
        };

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(() -> {

                List<U> res = new ArrayList<>();
                for (int j = finalL; j < r; j++) {
                    res.add(function.apply(list.get(j)));
                }
                return res;
            });
        }

        return parallelInvoker.getAll(new Monoid<>(f));
    }
}
