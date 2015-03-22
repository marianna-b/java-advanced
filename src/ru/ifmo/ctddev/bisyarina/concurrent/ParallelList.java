package ru.ifmo.ctddev.bisyarina.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * {@link ru.ifmo.ctddev.bisyarina.concurrent.ParallelList} provides functionality for
 * list parallel computations
 */

public class ParallelList implements ListIP {
    /**
     * Returns maximum element of list. If it is not unique - the first
     * @param i amount of threads to use
     * @param list list to process
     * @param comparator comparator to determine maximum
     * @param <T> supertype of elements of the list
     * @return maximum element
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    public <T> T maximum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        BiFunction<T, T, T> f = (a, b) -> comparator.compare(a, b) < 0 ? b : a;
        Function<List<? extends T>, Supplier<T>> g = (subList) -> () -> {
            Monoid<T> monoid = new Monoid<>(f);
            subList.forEach(monoid::process);
            return monoid.get();
        };
        ParallelInvoker<T> parallelInvoker = new ParallelInvoker<>(i, list, g);
        return parallelInvoker.get(new Monoid<>(f));
    }

    /**
     * Returns minimum element of list. If it is not unique - the first
     * @param i amount of threads to use
     * @param list list to process
     * @param comparator comparator to determine minimum
     * @param <T> supertype of elements of the list
     * @return minimum element
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    public <T> T minimum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        BiFunction<T, T, T> f = (a, b) -> comparator.compare(a, b) > 0 ? b : a;
        Function<List<? extends T>, Supplier<T>> g = (subList) -> () -> {
            Monoid<T> monoid = new Monoid<>(f);
            subList.forEach(monoid::process);
            return monoid.get();
        };
        ParallelInvoker<T> parallelInvoker = new ParallelInvoker<>(i, list, g);
        return parallelInvoker.get(new Monoid<>(f));
    }

    /**
     * Returns if all elements suit predicate
     * @param i amount of threads to use
     * @param list list to process
     * @param predicate predicate to suit
     * @param <T> supertype of elements of the list
     * @return true, if all elements suit predicate, false otherwise
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    public <T> boolean all(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        BiFunction<Boolean, Boolean, Boolean> f = (a, b) -> a&b;
        Function<List<? extends T>, Supplier<Boolean>> g = (subList) -> () -> {
            Monoid<Boolean> monoid = new Monoid<>(f, true);
            for (T element : subList) {
                monoid.process(predicate.test(element));
            }
            return monoid.get();
        };

        ParallelInvoker<Boolean> parallelInvoker = new ParallelInvoker<>(i, list, g);
        return parallelInvoker.get(new Monoid<>(f, true));
    }

    /**
     * Returns if any element suits predicate
     * @param i amount of threads to use
     * @param list list to process
     * @param predicate predicate to suit
     * @param <T> supertype of elements of the list
     * @return true, if exists element suiting predicate, false otherwise
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    public <T> boolean any(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        BiFunction<Boolean, Boolean, Boolean> f = (a, b) -> a|b;
        Function<List<? extends T>, Supplier<Boolean>> g = (subList) -> () -> {
            Monoid<Boolean> monoid = new Monoid<>(f, false);
            for (T element : subList) {
                monoid.process(predicate.test(element));
            }
            return monoid.get();
        };
        ParallelInvoker<Boolean> parallelInvoker = new ParallelInvoker<>(i, list, g);
        return parallelInvoker.get(new Monoid<>(f, false));
    }

    /**
     * Returns string representation of values in list
     * @param i amount of threads to use
     * @param list list to process
     * @return string representation of list elements
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    public String concat(int i, List<?> list) throws InterruptedException {
        BiFunction<StringBuilder, StringBuilder, StringBuilder> f = StringBuilder::append;
        Function<List<?>, Supplier<StringBuilder>> g = (subList) -> () -> {
            StringBuilder builder = new StringBuilder();
            for (Object aSubList : subList) {
                builder.append(aSubList.toString());
            }
            return builder;
        };
        ParallelInvoker<StringBuilder> parallelInvoker = new ParallelInvoker<>(i, list, g);
        return parallelInvoker.get(new Monoid<>(f)).toString();
    }

    /**
     * Returns list of elements that suit predicate
     * @param i amount of threads to use
     * @param list list to process
     * @param predicate predicate to suit
     * @param <T> supertype of elements of the list
     * @return list of elements that suit predicate
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    public <T> List<T> filter(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        BiFunction<List<T>, List<T>, List<T>> f = (a, b) -> {
            a.addAll(b);
            return a;
        };
        Function<List<? extends T>, Supplier<List<T>>> g = (subList) -> () -> {
            List<T> res = new ArrayList<>();
            for (T element : subList) {
                if (predicate.test(element))
                    res.add(element);
            }
            return res;
        };
        ParallelInvoker<List<T>> parallelInvoker = new ParallelInvoker<>(i, list, g);
        return parallelInvoker.get(new Monoid<>(f));
    }

    /**
     * Returns list of elements generated by applying given function to list elements
     * @param i amount of threads to use
     * @param list list to process
     * @param function function to apply
     * @param <T> supertype of elements of the list
     * @return list of elements generated by applying given function to list elements
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    public <T, U> List<U> map(int i, List<? extends T> list, Function<? super T, ? extends U> function) throws InterruptedException {
        BiFunction<List<U>, List<U>, List<U>> f = (a, b) -> {
            a.addAll(b);
            return a;
        };
        Function<List<? extends T>, Supplier<List<U>>> g = (subList) -> () -> {
            List<U> res = new ArrayList<>();
            for (T element : subList) {
                res.add(function.apply(element));
            }
            return res;
        };
        ParallelInvoker<List<U>> parallelInvoker = new ParallelInvoker<>(i, list, g);
        return parallelInvoker.get(new Monoid<>(f));
    }
}
