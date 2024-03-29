package ru.ifmo.ctddev.bisyarina.crawler;

class ChangedValue {
    volatile int value;
    volatile boolean changed;

    ChangedValue(int value){
        this.changed = true;
        this.value = value;
    }

    void incIfLess(int x) {
        if (x == value) {
            changed = false;
        } else {
            changed = true;
            value++;
        }
    }

    void dec() {
        this.value--;
        this.changed = false;
    }
}
