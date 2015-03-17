#!/usr/bin/env bash

jar cvfm Implementor.jar Manifest -C src ru/ifmo/ctddev/bisyarina/implementor/Implementor.class
jar uvf Implementor.jar -C src ru/ifmo/ctddev/bisyarina/implementor/Implementation.class
jar uvf Implementor.jar -C src ru/ifmo/ctddev/bisyarina/implementor/Implementation\$1.class
jar uvf Implementor.jar -C src ru/ifmo/ctddev/bisyarina/implementor/ImplementationGenerator.class
