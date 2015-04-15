package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.URLUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Task {
    private final CrawlerInvoke invoke;


    private int depth = 0;
    private final int neededDepth;
    private String url;
    private AppendableLatch latch;
    private CopyOnWriteArrayList<String> list;


    Task(String url, int depth, int neededDepth, CopyOnWriteArrayList<String> list, AppendableLatch latch,
         CrawlerInvoke invoke) {
        this.neededDepth = neededDepth;
        this.depth = depth;
        this.url = url;
        this.latch = latch;
        this.list = list;

        this.invoke = invoke;
    }

    public boolean checkDepth() {
        return depth < neededDepth;
    }

    private ChangedValue remapAdd(String s, ChangedValue changedValue) {
        changedValue.incIfLess(invoke.getPerHost());
        return changedValue;
    }

    private ChangedValue remapDel(String s, ChangedValue changedValue) {
        changedValue.dec();
        return changedValue;
    }

    private Task getChild(String s) {
        return new Task(s, depth + 1, neededDepth, list, latch, invoke);
    }

    public void getDownloader() {
        String host;
        try {
            host = URLUtils.getHost(url);
            invoke.getHosts().putIfAbsent(host, new ChangedValue(0));

            if (!invoke.getHosts().computeIfPresent(host, this::remapAdd).changed) {
                invoke.getDownloading().submit(this::getDownloader);
                return;
            }
        } catch (MalformedURLException e) {
            latch.dec();
            return;
        }

        try {
            Document document = invoke.getDownloader().download(url);
            invoke.getHosts().computeIfPresent(host, this::remapDel);

            invoke.getExtracting().submit(() -> {
                try {
                    List<String> links = document.extractLinks();
                    list.addAll(links);
                    if (checkDepth()) {
                        latch.addCounter(links.size());

                        for (String link : links) {
                            invoke.getDownloading().submit(getChild(link)::getDownloader);
                        }
                    }
                } catch (IOException ignored) {}
                finally {
                    latch.dec();
                }
            });
        } catch (IOException ignored) {
            invoke.getHosts().computeIfPresent(host, this::remapDel);
            latch.dec();
        }
    }
}
