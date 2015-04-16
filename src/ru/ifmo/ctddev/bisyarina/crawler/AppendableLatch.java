package ru.ifmo.ctddev.bisyarina.crawler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class {@link ru.ifmo.ctddev.bisyarina.concurrent.Latch} provides functionality send notification about
 * something happened given amount of times, provides functionality to increase amount of times
 */
public class AppendableLatch {

    private volatile AtomicInteger counter;

    /**
     * Creates latch waiting for given number of events
     * @param counter number of events
     */
    public AppendableLatch(int counter) {
        this.counter = new AtomicInteger(counter);
    }

    /**
     *  Starts waiting for the events
     * @throws InterruptedException if any thread has interrupted the current thread while waiting
     */
    public synchronized void await() throws InterruptedException {
        while (counter.get() > 0) {
            this.wait();
        }
    }

    /**
     * Adds value to counter
     * @param val value to add
     */
    public void addCounter(int val) {
        counter.addAndGet(val);
    }

    /**
     * Notifies that one event happened
     */
    public void dec() {
        if (counter.decrementAndGet() == 0) {
            synchronized (this) {
                this.notify();
            }
        }
    }
}
