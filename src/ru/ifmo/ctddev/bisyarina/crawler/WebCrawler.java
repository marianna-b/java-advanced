package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Downloader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {
    private final int CAPACITY = 100000;

    private final AbstractExecutorService downloading;
    private final AbstractExecutorService extracting;


    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        downloading = new ThreadPoolExecutor(1, downloaders, 1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(CAPACITY));
        extracting = new ThreadPoolExecutor(1, extractors, 1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(CAPACITY));

        Task.downloader = downloader;
        Task.extracting = extracting;
        Task.downloading = downloading;
        Task.perHost = perHost;
    }

    @Override
    public List<String> download(String url, int depth) throws IOException {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        AppendableLatch latch = new AppendableLatch(1);
        Task t = new Task(url, 1, depth, list, latch);
        downloading.submit(t.getDownloader());
        try {
            latch.await();
        } catch (InterruptedException e) {
            return null;
        }
        return list;
    }

    @Override
    public void close() {
        downloading.shutdown();
        extracting.shutdown();
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
        int depth = Integer.getInteger(args[1]);
        int downloaders = Integer.getInteger(args[2]);
        int extractors = Integer.getInteger(args[3]);
        int perHost = Integer.getInteger(args[4]);

        try {
            Downloader downloader = new CachingDownloader(new File("."));
            WebCrawler crawler = new WebCrawler(downloader, downloaders, extractors, perHost);
            List<String> list = crawler.download(args[0], depth);
            for (String item : list) {
                System.out.println(item);
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
