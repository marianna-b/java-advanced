#!/usr/bin/env bash

javadoc -classpath IterativeParallelismTest.jar:quickcheck-0.6.jar:junit-4.11.jar -d doc -link http://docs.oracle.com/javase/8/docs/api/ src/ru/ifmo/ctddev/bisyarina/concurrent/Monoid.java src/ru/ifmo/ctddev/bisyarina/concurrent/package-info.java src/ru/ifmo/ctddev/bisyarina/concurrent/ParallelInvoker.java src/ru/ifmo/ctddev/bisyarina/concurrent/IterativeParallelism.java src/ru/ifmo/ctddev/bisyarina/concurrent/ParallelMapperImpl.java src/ru/ifmo/ctddev/bisyarina/concurrent/Latch.java
