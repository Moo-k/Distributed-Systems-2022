#!/bin/bash

java SorterClient << END
pushValue
1
pushValue
3
pushValue
2
pushValue
15
pushValue
12
pushOperator
ascending
pop
pop
pop
pop
pop
isEmpty
close
END