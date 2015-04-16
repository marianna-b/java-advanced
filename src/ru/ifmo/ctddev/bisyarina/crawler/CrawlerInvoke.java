package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.Downloader;
import java.util.concurrent.*;

class CrawlerInvoke {

    private final AbstractExecutorService extracting;
    private final AbstractExecutorService downloading;
    private final Downloader downloader;
    private final ConcurrentMap<String, ChangedValue> hosts;
    private final ConcurrentMap<String, String> loaded;
    private final ConcurrentMap<String, String> extracted;
    private final int perHost;

    CrawlerInvoke(Downloader downloader, int d, int e, int perHost) {
        int CAPACITY = 10000000;

        this.downloader = downloader;
        this.downloading = new ThreadPoolExecutor(1, d, 1, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(CAPACITY));
        this.extracting = new ThreadPoolExecutor(1, e, 1, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(CAPACITY));
        this.perHost = perHost;
        this.hosts = new ConcurrentHashMap<>();
        this.loaded = new ConcurrentHashMap<>();
        this.extracted = new ConcurrentHashMap<>();
    }

    AbstractExecutorService getExtracting() {
        return extracting;
    }

    AbstractExecutorService getDownloading() {
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

    ConcurrentMap<String, String> getExtracted() {
        return extracted;
    }
}
