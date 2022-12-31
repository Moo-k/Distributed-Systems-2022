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
descending
pop
pop
pop
pop
pop
isEmpty
END