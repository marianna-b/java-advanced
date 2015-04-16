package ru.ifmo.ctddev.bisyarina.crawler;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Downloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class {@link ru.ifmo.ctddev.bisyarina.crawler.WebCrawler} provides functionality for recursive downloading
 * web pages
 */
public class WebCrawler implements Crawler {

    private final CrawlerInvoke invoke;

    /**
     * Constructs WebCrawler downloading pages using given downloader, given max values of downloadings,
     * extractions and downloading per host
     * @param downloader downloader for downloading
     * @param downloaders possible amount of simultaneous downloads
     * @param extractors possible amount of extractions at the same time
     * @param perHost possible downloadings per host
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        invoke = new CrawlerInvoke(downloader, downloaders, extractors, perHost);
    }

    /**
     * Downloads recursively pages to given depth
     * @param url url to download
     * @param depth depth of recursion
     * @return list of links extracted from loaded pages
     * @throws IOException if downloading failed
     */
    public List<String> download(String url, int depth) throws IOException {
        List<String> list = new ArrayList<>();
        AppendableLatch latch = new AppendableLatch(1);
        Task t = new Task(url, 1, depth - 1, list, latch, invoke);
        list.add(url);
        invoke.getDownloading().execute(t::getDownloader);
        try {
            latch.await();
        } catch (InterruptedException e) {
            return null;
        }
        return list;
    }

    /**
     * Closes crawler so that it will no longer permit new tasks
     */
    public void close() {
        invoke.close();
    }

    /**
     * Provides interface for using crawler
     * @param args [url, depth, downloaders, extractors, perHost]
     */
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
