package ru.ifmo.ctddev.bisyarina.uicopyfiles;

public class Status {
    long size;
    long currSize;
    long start;
    long currentTime;
    long currSpeed;
    long speed;


    Status(long size) {
        currSize = 0;
        if (size != 0)
            this.size = size;
        else
            this.size = 10000;
        start = System.currentTimeMillis();
        currentTime = start;
    }

    void update(long readRes) {
        currSize += readRes;
        long tmp = System.currentTimeMillis();
        long timeUpdate = tmp - currentTime;
        currentTime = tmp;
        currSpeed = (readRes * 1000)/ timeUpdate;
        speed = (currSize * 1000) / (currentTime - start);
    }

    int getPercentSize() {
        return (int) ((100 * currSize) / size);
    }

    String getTimeLeft(){
        if (currSize == size)
            return toStringTime(0);
        if (speed == 0)
            return "infinity";
        long curr = size / speed;
        return toStringTime(curr);
    }
    String getTime(){
        long curr = currentTime - start;
        return toStringTime(curr);
    }
    String getSpeed() {
        return toStringMem(speed) + "/sec.";
    }
    String getCurrentSpeed() {
        return toStringMem(currSpeed) + "/sec.";
    }

    String toStringTime(long time) {
        time /= 1000;
        long second = time % 60;
        time /= 60;
        String res = Long.toString(second) + " sec.";
        if (time == 0)
            return res;
        long minutes = time % 60;
        time /= 60;
        res = Long.toString(minutes) + " min. " + res;
        if (time == 0)
            return res;
        return Long.toString(time) + " h. " + res;
    }

    String toStringMem(long size) {
        long b = size % 1024;
        String res = Long.toString(b) + " B.";
        size /= 1024;
        if (size == 0)
            return res;
        long k = size % 1024;
        res = Long.toString(k) + " KB. " + res;
        size /= 1024;
        if (size == 0)
            return res;
        long m = size % 1024;
        res = Long.toString(m) + " MB. " + res;
        size /= 1024;
        if (size == 0)
            return res;
        return Long.toString(size) + " GB. " + res;
    }
}
