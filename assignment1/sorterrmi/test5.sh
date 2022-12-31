#!/bin/bash

java SorterClient << END
pushValue
1
pushValue
4
pushValue
12
pushOperator
min
pop
isEmpty
pushValue
7
pushValue
23
pushValue
-12
pushValue
-12
pushOperator
min
pop
pop
isEmpty
close
END