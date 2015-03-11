#!/usr/bin/env bash

javadoc -private -classpath ImplementorTest.jar:quickcheck-0.6.jar:junit-4.11.jar -d doc -link http://docs.oracle.com/javase/8/docs/api/ src/ru/ifmo/ctddev/bisyarina/implementor/*.java
