#!/usr/bin/env bash

javac -cp ./HelloUDPTest.jar:./jsoup-1.8.1.jar:./src: src/ru/ifmo/ctddev/bisyarina/helloudp/*.java

java -cp ./HelloUDPTest.jar:./jsoup-1.8.1.jar:./src:./junit-4.11.jar:./quickcheck-0.6.jar:./hamcrest-core-1.3.jar info.kgeorgiy.java.advanced.hello.Tester server ru.ifmo.ctddev.bisyarina.helloudp.HelloUDPServer $1
