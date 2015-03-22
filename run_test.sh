#!/usr/bin/env bash

javac -cp ./IterativeParallelismTest.jar: src/ru/ifmo/ctddev/bisyarina/concurrent/*.java

java -cp ./IterativeParallelismTest.jar::./src:./junit-4.11.jar:./quickcheck-0.6.jar:./hamcrest-core-1.3.jar info.kgeorgiy.java.advanced.concurrent.Tester list ru.ifmo.ctddev.bisyarina.concurrent.ParallelList $1
