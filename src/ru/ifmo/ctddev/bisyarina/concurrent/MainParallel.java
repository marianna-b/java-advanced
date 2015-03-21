package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by mariashka on 3/21/15.
 */
public class MainParallel {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(4);
        list.add(3);
        list.add(5);
        list.add(1);
        list.add(-1);
        list.add(4);
        list.add(10);
        list.add(-1);
        list.add(11);
        ParallelList parallelList = new ParallelList();
        try {
            System.err.println(parallelList.minimum(3, list, Comparator.<Integer>naturalOrder()));
            System.err.println(parallelList.maximum(3, list, Comparator.<Integer>naturalOrder()));
            System.err.println(parallelList.any(3, list, new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer > 0;
                }
            }));
            System.err.println(parallelList.all(3, list, new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer > 0;
                }
            }));
            System.err.println(parallelList.any(3, list, new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer == 0;
                }
            }));
            System.err.println(parallelList.filter(3, list, new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer > 0;
                }
            }));
            System.err.println(parallelList.concat(3, list));
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}
