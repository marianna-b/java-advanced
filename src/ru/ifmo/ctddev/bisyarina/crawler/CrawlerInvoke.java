package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.Downloader;

import java.util.concurrent.*;

public class CrawlerInvoke {
    private AbstractExecutorService extracting;
    private AbstractExecutorService downloading;
    private Downloader downloader;
    private ConcurrentMap<String, ChangedValue> hosts;
    private int perHost = 0;

    CrawlerInvoke(Downloader downloader, int d, int e, int perHost) {
        int CAPACITY = 100000;

        this.downloader = downloader;
        this.downloading = new ThreadPoolExecutor(1, d, 1, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(CAPACITY));
        this.extracting = new ThreadPoolExecutor(1, e, 1, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(CAPACITY));
        this.perHost = perHost;
        this.hosts = new ConcurrentHashMap<>();
    }

    public AbstractExecutorService getExtracting() {
        return extracting;
    }

    public AbstractExecutorService getDownloading() {
        return downloading;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public ConcurrentMap<String, ChangedValue> getHosts() {
        return hosts;
    }

    public int getPerHost() {
        return perHost;
    }

    public void close() {
        downloading.shutdown();
        extracting.shutdown();
    }
}
