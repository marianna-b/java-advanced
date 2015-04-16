#!/usr/bin/env bash

javac -cp ./WebCrawlerTest.jar:./jsoup-1.8.1.jar:./src: src/ru/ifmo/ctddev/bisyarina/crawler/*.java

java -cp ./WebCrawlerTest.jar:./jsoup-1.8.1.jar:./src:./junit-4.11.jar:./quickcheck-0.6.jar:./hamcrest-core-1.3.jar info.kgeorgiy.java.advanced.crawler.Tester $1 ru.ifmo.ctddev.bisyarina.crawler.WebCrawler $2

