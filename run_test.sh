#!/usr/bin/env bash

javac -cp ./ParallelMapperTest.jar: src/ru/ifmo/ctddev/bisyarina/concurrent/*.java

java -cp ./ParallelMapperTest.jar::./src:./junit-4.11.jar:./quickcheck-0.6.jar:./hamcrest-core-1.3.jar info.kgeorgiy.java.advanced.mapper.Tester list ru.ifmo.ctddev.bisyarina.concurrent.ParallelMapperImpl,ru.ifmo.ctddev.bisyarina.concurrent.IterativeParallelism $1
