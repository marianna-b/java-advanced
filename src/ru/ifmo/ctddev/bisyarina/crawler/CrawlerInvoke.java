package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.Downloader;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

class CrawlerInvoke {
    private final ExecutorService extracting;
    private final ExecutorService downloading;
    private final Downloader downloader;
    private final ConcurrentMap<String, ChangedValue> hosts;
    private final Map<String, Queue<Runnable>> delayedHosts;
    private final ConcurrentMap<String, Boolean> loaded;
    private final int perHost;

    CrawlerInvoke(Downloader downloader, int d, int e, int perHost) {
        this.downloader = downloader;
        this.downloading = Executors.newFixedThreadPool(d);
        this.extracting = Executors.newFixedThreadPool(e);

        this.perHost = perHost;
        this.hosts = new ConcurrentHashMap<>();
        this.loaded = new ConcurrentHashMap<>();
        this.delayedHosts = new HashMap<>();
    }

    ExecutorService getExtracting() {
        return extracting;
    }

    ExecutorService getDownloading() {
        return downloading;
    }

    Downloader getDownloader() {
        return downloader;
    }

    ConcurrentMap<String, ChangedValue> getHosts() {
        return hosts;
    }

    int getPerHost() {
        return perHost;
    }

    void close() {
        downloading.shutdown();
        extracting.shutdown();
    }

    ConcurrentMap<String, Boolean> getLoaded() {
        return loaded;
    }

    public Map<String, Queue<Runnable>> getDelayedHosts() {
        return delayedHosts;
    }

    public void pollToDownloading(String host, BiFunction<String, ChangedValue, ChangedValue> remapDel) {
        hosts.computeIfPresent(host, remapDel);
        synchronized (delayedHosts) {
            Queue<Runnable> q = delayedHosts.get(host);
            if (q != null && !q.isEmpty())
                downloading.execute(q.poll());
        }
    }
}
