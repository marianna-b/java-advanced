package ru.ifmo.ctddev.bisyarina.concurrent;


import java.util.List;
import java.util.function.Function;

public interface ParallelMapper extends AutoCloseable {
    <T, R> List<R> run(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException;

    @Override
    void close() throws InterruptedException;
}