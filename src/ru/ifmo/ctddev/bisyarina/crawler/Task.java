package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.URLUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

class Task {
    private final CrawlerInvoke invoke;
    private final int depth;
    private final int neededDepth;
    private final String url;
    private final AppendableLatch latch;
    private final List<String> list;

    Task(String url, int depth, int neededDepth, List<String> list, AppendableLatch latch,
         CrawlerInvoke invoke) {
        this.neededDepth = neededDepth;
        this.depth = depth;
        this.url = url;
        this.latch = latch;
        this.list = list;

        this.invoke = invoke;
    }

    boolean checkDepth() {
        return depth < neededDepth;
    }

    ChangedValue remapAdd(String s, ChangedValue changedValue) {
        changedValue.incIfLess(invoke.getPerHost());
        return changedValue;
    }

    ChangedValue remapDel(String s, ChangedValue changedValue) {
        changedValue.dec();
        return changedValue;
    }

    Task getChild(String s) {
        return new Task(s, depth + 1, neededDepth, list, latch, invoke);
    }

    void getDownloader() {
        if (invoke.getLoaded().putIfAbsent(url, Boolean.TRUE) != null) {
            latch.dec();
            return;
        }
        String host;
        try {
            host = URLUtils.getHost(url);
            invoke.getHosts().putIfAbsent(host, new ChangedValue(0));

            if (!invoke.getHosts().computeIfPresent(host, this::remapAdd).changed) {
                synchronized (invoke.getDelayedHosts()) {
                    invoke.getDelayedHosts().putIfAbsent(host, new LinkedList<>());
                    invoke.getDelayedHosts().get(host).add(this::getDownloader);
                }
                return;
            }
        } catch (MalformedURLException e) {
            latch.dec();
            return;
        }

        try {
            Document document = invoke.getDownloader().download(url);

            invoke.pollToDownloading(host, this::remapDel);
            invoke.getExtracting().execute(getExtractor(document));
        } catch (IOException ignored) {
            invoke.pollToDownloading(host, this::remapDel);
            latch.dec();
        }
    }


    Runnable getExtractor(Document document) {
        return () -> {
            try {
                List<String> links = document.extractLinks();
                synchronized (list) {
                    list.addAll(links);
                }
                if (checkDepth()) {
                    latch.addCounter(links.size());
                    for (String link : links) {
                        invoke.getDownloading().execute(getChild(link)::getDownloader);
                    }
                }
            } catch (IOException ignored) {
            } finally {
                latch.dec();
            }
        };
    }
}
