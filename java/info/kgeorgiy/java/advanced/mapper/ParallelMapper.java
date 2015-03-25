package info.kgeorgiy.java.advanced.mapper;

import java.util.List;
import java.util.function.Function;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
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
    <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException;

    @Override
    /**
     * Interrupts all working threads
     */
    void close() throws InterruptedException;
}
