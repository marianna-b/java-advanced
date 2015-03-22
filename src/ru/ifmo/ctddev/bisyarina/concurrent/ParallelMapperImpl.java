package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;

/**
 * Created by mariashka on 3/22/15.
 */
public class ParallelMapperImpl implements ParallelMapper {
    private Thread[] threads;
    private final Queue<Runnable> queue = new PriorityQueue<>();

    ParallelMapperImpl(int threadAmount) {
        this.threads = new Thread[threadAmount];
        for (int i = 0; i < threadAmount; i++) {
            this.threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    Runnable f;
                    synchronized (queue) {
                        f = queue.poll();
                    }
                    f.run();
                }
            });
        }
    }

    @Override
    public <T, R> List<R> run(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        int[] counter = new int[1];
        @SuppressWarnings("unchecked")
        R[] results = (R[]) new Object[args.size()];
        synchronized (queue) {
            for (int i = 0; i < args.size(); i++) {
                final int finalI = i;
                queue.add(() -> {
                    results[finalI] = f.apply(args.get(finalI));
                    counter[0]++;
                    if (counter[0] == args.size())
                        notifyAll();
                });
            }
        }
        while (counter[0] < args.size()) {
            wait();
        }
        return Arrays.asList(results);
    }

    @Override
    public void close() throws InterruptedException {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }
}
