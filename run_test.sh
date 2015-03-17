#!/usr/bin/env bash

javac -cp ./ImplementorTest.jar: src/ru/ifmo/ctddev/bisyarina/implementor/*.java

java -cp ./ImplementorTest.jar::./src:./junit-4.11.jar:./quickcheck-0.6.jar:./hamcrest-core-1.3.jar info.kgeorgiy.java.advanced.implementor.Tester class ru.ifmo.ctddev.bisyarina.implementor.Implementation $1
