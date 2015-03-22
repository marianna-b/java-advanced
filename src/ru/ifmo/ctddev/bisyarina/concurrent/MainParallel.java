package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

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
        ParallelMapper mapper = new ParallelMapperImpl(3);
        IterativeParallelism iterativeParallelism = new IterativeParallelism(mapper);
        try {
            System.err.println(iterativeParallelism.minimum(3, list, Comparator.<Integer>naturalOrder()));

            System.err.println(iterativeParallelism.maximum(3, list, Comparator.<Integer>naturalOrder()));
            System.err.println(iterativeParallelism.any(3, list, new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer > 0;
                }
            }));
            System.err.println(iterativeParallelism.all(3, list, new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer > 0;
                }
            }));
            System.err.println(iterativeParallelism.any(3, list, new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer == 0;
                }
            }));
            System.err.println(iterativeParallelism.filter(3, list, new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer > 0;
                }
            }));
            System.err.println(iterativeParallelism.concat(3, list));
            mapper.close();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

    }
}
