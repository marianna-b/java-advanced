package ru.ifmo.ctddev.bisyarina.concurrent;

/**
 * Class {@link ru.ifmo.ctddev.bisyarina.concurrent.Latch} provides functionality send notification about
 * something happened given amount of times
 */
public class Latch {
    private volatile int counter;

    /**
     * Creates latch waiting for given number of events
     * @param counter number of events
     */
    public Latch(int counter) {
        this.counter = counter;
    }

    /**
     *  Starts waiting for the events
     * @throws InterruptedException if any thread has interrupted the current thread while waiting
     */
    public synchronized void await() throws InterruptedException {
        while (counter > 0) {
            this.wait();
        }
    }

    /**
     * Notifies that one event happened
     */
    public synchronized void inc() {
        counter--;
        if (counter == 0)
            this.notify();
    }
}
