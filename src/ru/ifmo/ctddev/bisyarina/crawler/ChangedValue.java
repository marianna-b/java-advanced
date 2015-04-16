package ru.ifmo.ctddev.bisyarina.crawler;

class ChangedValue {

    volatile Integer value;
    volatile Boolean changed;

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
