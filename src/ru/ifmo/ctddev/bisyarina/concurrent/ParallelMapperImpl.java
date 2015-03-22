package ru.ifmo.ctddev.bisyarina.concurrent;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private final Thread[] threads;
    private final Queue<Runnable> queue = new LinkedList<>();

    ParallelMapperImpl(int threadAmount) {
        this.threads = new Thread[threadAmount];
        for (int i = 0; i < threadAmount; i++) {
            this.threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isInterrupted = false;
                    while (!isInterrupted) {
                        try {
                            get().run();
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

    private Runnable get() throws InterruptedException {
        synchronized (queue) {
            while (queue.isEmpty()) {
                queue.wait();
            }
        return queue.poll();
        }
    }

    private void set(Runnable r) {
        synchronized (queue) {
            queue.add(r);
            queue.notify();
        }
    }

    @Override
    public void close() throws InterruptedException {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }
}
