package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.Downloader;

import java.util.concurrent.*;
import java.util.function.BiFunction;

class CrawlerInvoke {
    private final ExecutorService extracting;
    private final ExecutorService downloading;
    private final Downloader downloader;
    private final ConcurrentMap<String, ChangedValue> hosts;
    private final ConcurrentMap<String, BlockingQueue<Runnable>> delayedHosts;
    private final ConcurrentMap<String, Boolean> loaded;
    private final int perHost;

    CrawlerInvoke(Downloader downloader, int d, int e, int perHost) {
        this.downloader = downloader;
        this.downloading = Executors.newFixedThreadPool(d);
        this.extracting = Executors.newFixedThreadPool(e);

        this.perHost = perHost;
        this.hosts = new ConcurrentHashMap<>();
        this.loaded = new ConcurrentHashMap<>();
        this.delayedHosts = new ConcurrentHashMap<>();
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

    public ConcurrentMap<String, BlockingQueue<Runnable>> getDelayedHosts() {
        return delayedHosts;
    }

    public void pollToDownloading(String host, BiFunction<String, ChangedValue, ChangedValue> remapDel) {
        synchronized (getHosts().get(host)) {
            hosts.computeIfPresent(host, remapDel);
            getDelayedHosts().computeIfPresent(host, (s, runnables) -> {
                while (!runnables.isEmpty()) {
                    Runnable r = runnables.poll();
                    downloading.execute(r);
                }
                return runnables;
            });
        }
    }
}
