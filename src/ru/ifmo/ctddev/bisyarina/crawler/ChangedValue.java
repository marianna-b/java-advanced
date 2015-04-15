package ru.ifmo.ctddev.bisyarina.crawler;

public class ChangedValue {
    Integer value;
    Boolean changed;

    ChangedValue(int value){
        this.changed = false;
        this.value = value;
    }

    public void incIfLess(int x) {
        if (x == value) {
            changed = false;
        } else {
            changed = true;
            value++;
        }
    }

    public void dec() {
        this.value--;
        this.changed = false;
    }
}
