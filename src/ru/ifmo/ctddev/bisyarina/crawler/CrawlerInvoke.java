package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.Downloader;
import java.util.concurrent.*;

class CrawlerInvoke {
    private final ExecutorService extracting;
    private final ExecutorService downloading;
    private final Downloader downloader;
    private final ConcurrentMap<String, ChangedValue> hosts;
    private final ConcurrentMap<String, String> loaded;
    private final int perHost;

    CrawlerInvoke(Downloader downloader, int d, int e, int perHost) {
        this.downloader = downloader;
        this.downloading = Executors.newFixedThreadPool(d);
        this.extracting = Executors.newFixedThreadPool(e);

        this.perHost = perHost;
        this.hosts = new ConcurrentHashMap<>();
        this.loaded = new ConcurrentHashMap<>();
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

    ConcurrentMap<String, String> getLoaded() {
        return loaded;
    }
}
