#!/bin/bash

java SorterClient << END
pushValue
1
pushValue
4
pushValue
12
delayPop
5000
pop
isEmpty
delayPop
8000
isEmpty
close
END