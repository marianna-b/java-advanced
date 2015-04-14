package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.URLUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

public class Task {

    public static AbstractExecutorService extracting;
    public static AbstractExecutorService downloading;
    public static Downloader downloader;
    public static ConcurrentMap<String, ChangedValue> hosts;
    public static int perHost = 0;

    private BiFunction<String, ChangedValue, ChangedValue> remap = (s, changedValue) -> {
        changedValue.incIfLess(perHost);
        return changedValue;
    };

    private int depth = 0;
    private final int neededDepth;
    private String url;
    private AppendableLatch latch;
    private CopyOnWriteArrayList<String> list;


    Task(String url, int depth, int neededDepth, CopyOnWriteArrayList<String> list, AppendableLatch latch) {
        this.neededDepth = neededDepth;
        this.depth = depth;
        this.url = url;
        this.latch = latch;
        this.list = list;
    }

    public boolean checkDepth() {
        return depth < neededDepth;
    }

    private Task getChild(String s) {
        return new Task(s, depth + 1, neededDepth, list, latch);
    }

    public Runnable getDownloader() {
        return () -> {
            try {
                String host = URLUtils.getHost(url);
                hosts.putIfAbsent(host, new ChangedValue(0));

                if (!hosts.computeIfPresent(host, remap).changed) {
                    downloading.submit(Task.this.getDownloader());
                }

                Document document = downloader.download(url);
                extracting.submit(() -> {
                    try {
                        List<String> links = document.extractLinks();
                        list.addAll(links);
                        if (!Task.this.checkDepth()) {
                            latch.dec();
                            return;
                        }

                        for (String link : links) {
                            Task task = getChild(link);
                            extracting.submit(task.getDownloader());
                        }

                        latch.addCounter(links.size());
                        latch.dec();

                    } catch (IOException ignored) {}

                });
            } catch (IOException ignored) {}
        };

    }
}
