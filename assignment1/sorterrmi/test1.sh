#!/bin/bash

java SorterClient << END
pushValue
1
pushValue
2
pushValue
5
pushValue
6
pop
pop
isEmpty
pop
pushValue
12
pop
isEmpty
pop
isEmpty
close
END