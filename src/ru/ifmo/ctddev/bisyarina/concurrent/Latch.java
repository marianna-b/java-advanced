package ru.ifmo.ctddev.bisyarina.concurrent;

/**
 * Created by mariashka on 3/25/15.
 */
public class Latch {
    private volatile int counter;

    public Latch(int counter) {
        this.counter = counter;
    }

    public synchronized void set() throws InterruptedException {
        while (counter > 0) {
            this.wait();
        }
    }

    public synchronized void inc() {
        counter--;
        if (counter == 0)
            this.notify();
    }
}
