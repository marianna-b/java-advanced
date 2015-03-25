package ru.ifmo.ctddev.bisyarina.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
/**
 * {@link ru.ifmo.ctddev.bisyarina.concurrent.ParallelMapperImpl} provides functionality to process task
 * using multiple threads
 */
public class ParallelMapperImpl implements ParallelMapper{
    private Queue<Runnable> queue = new ArrayDeque<>();
    private volatile boolean isInterrupted = false;

    /**
     * Constructs mapper using given amount of threads to process tasks
     * @param threadAmount amount of threads
     */
    public ParallelMapperImpl(int threadAmount) {
        Thread[] threads = new Thread[threadAmount];
        for (int i = 0; i < threadAmount; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Runnable f;
                            synchronized (ParallelMapperImpl.this) {
                                while (queue.isEmpty()) {
                                    if (isInterrupted)
                                        return;
                                    ParallelMapperImpl.this.wait();
                                }
                                f = queue.poll();
                            }
                            f.run();
                        } catch (InterruptedException e) {
                            synchronized (ParallelMapperImpl.this) {
                                isInterrupted = true;
                                ParallelMapperImpl.this.notifyAll();
                            }
                        }
                    }
                }
            });
            threads[i].start();
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        Latch latch = new Latch(args.size());

        @SuppressWarnings("unchecked")
        R[] results = (R[]) new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            final int finalI = i;
            set(() -> {
                results[finalI] = f.apply(args.get(finalI));
                latch.inc();
            });
        }
        latch.set();
        return Arrays.asList(results);
    }

    private void set(Runnable r) {
        synchronized (this) {
            queue.add(r);
            this.notify();
        }
    }

    @Override
    public void close() throws InterruptedException {
        synchronized (this) {
            isInterrupted = true;
            this.notifyAll();
        }
    }
}
