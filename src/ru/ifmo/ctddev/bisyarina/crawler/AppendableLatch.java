package ru.ifmo.ctddev.bisyarina.crawler;

/**
 * Class {@link ru.ifmo.ctddev.bisyarina.concurrent.Latch} provides functionality send notification about
 * something happened given amount of times
 */
public class AppendableLatch {

    private volatile int counter;

    /**
     * Creates latch waiting for given number of events
     * @param counter number of events
     */
    public AppendableLatch(int counter) {
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

    public synchronized void addCounter(int l) {
        this.counter += l;
    }

    /**
     * Notifies that one event happened
     */
    public synchronized void dec() {
        counter--;
        if (counter == 0)
            this.notify();
    }
}
