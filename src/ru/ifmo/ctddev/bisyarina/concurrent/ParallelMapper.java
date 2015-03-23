package ru.ifmo.ctddev.bisyarina.concurrent;


import java.util.List;
import java.util.function.Function;

/**
 * {@link ru.ifmo.ctddev.bisyarina.concurrent.ParallelMapper} provides interface for parallel processing tasks
 */
public interface ParallelMapper extends AutoCloseable {
    /**
     * Returns a list function applying results
     * @param f function to apply
     * @param args list of arguments for function to apply
     * @param <T> supertype of input values
     * @param <R> supertype of return values
     * @return list of results of application
     * @throws InterruptedException if any thread computing result has interrupted the current thread.
     */
    <T, R> List<R> run(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException;

    /**
     * Interrupts all working threads
     */
    @Override
    void close() throws InterruptedException;
}