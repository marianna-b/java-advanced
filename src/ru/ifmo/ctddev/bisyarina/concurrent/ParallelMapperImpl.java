package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.*;
import java.util.function.Function;
/**
 * {@link ru.ifmo.ctddev.bisyarina.concurrent.ParallelMapperImpl} provides functionality to process task
 * using multiple threads
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
public class ParallelMapperImpl implements ParallelMapper {
    private final Queue<Runnable> queue = new LinkedList<>();
    private Boolean isInterrupted = false;

    /**
     * Constructs mapper using given amount of threads to process tasks
     * @param threadAmount amount of threads
     */
    ParallelMapperImpl(int threadAmount) {
        Thread[] threads = new Thread[threadAmount];
        for (int i = 0; i < threadAmount; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            synchronized (queue) {
                                while (queue.isEmpty()) {
                                    synchronized (isInterrupted) {
                                        if (isInterrupted)
                                            return;
                                    }
                                    queue.wait();
                                }
                                queue.poll().run();
                            }
                        } catch (InterruptedException e) {
                            isInterrupted = true;
                        }
                    }
                }
            });
            threads[i].start();
        }
    }

    @Override
    public <T, R> List<R> run(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        int[] counter = new int[1];
        @SuppressWarnings("unchecked")
        R[] results = (R[]) new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            final int finalI = i;
            set(() -> {
                results[finalI] = f.apply(args.get(finalI));
                synchronized (counter) {
                    counter[0]++;
                    if (counter[0] == args.size()) {
                        counter.notifyAll();
                    }
                }
            });
        }

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (counter) {
            while (counter[0] < args.size()) {
                counter.wait();
            }
        }
        return Arrays.asList(results);
    }

    private void set(Runnable r) {
        synchronized (queue) {
            queue.add(r);
            queue.notify();
        }
    }

    @Override
    public void close() throws InterruptedException {
        synchronized (isInterrupted) {
            isInterrupted = true;
        }
        synchronized (queue) {
            queue.notifyAll();
        }
    }
}
