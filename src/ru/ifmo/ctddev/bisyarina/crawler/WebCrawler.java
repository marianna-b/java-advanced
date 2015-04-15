package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Downloader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {

    private CrawlerInvoke invoke;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        invoke = new CrawlerInvoke(downloader, downloaders, extractors, perHost);
    }

    @Override
    public List<String> download(String url, int depth) throws IOException {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        AppendableLatch latch = new AppendableLatch(1);
        Task t = new Task(url, 1, depth, list, latch, invoke);
        invoke.getDownloading().submit(t.getDownloader());
        try {
            latch.await();
        } catch (InterruptedException e) {
            return null;
        }
        return list;
    }

    @Override
    public void close() {
        invoke.close();
    }

    public static void main(String[] args) {
        if (args == null) {
            System.err.println("Invalid args");
            return;
        }
        if (args.length < 5) {
            System.err.println("Not enough parameters");
            return;
        }
        int depth = Integer.parseInt(args[1]);
        int downloaders = Integer.parseInt(args[2]);
        int extractors = Integer.parseInt(args[3]);
        int perHost = Integer.parseInt(args[4]);

        try {
            Downloader downloader = new CachingDownloader(new File("."));
            WebCrawler crawler = new WebCrawler(downloader, downloaders, extractors, perHost);
            List<String> list = crawler.download(args[0], depth);
            crawler.close();

            list.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
