#!/bin/bash
chmod 777 test?.sh
chmod 777 thread?.sh

echo "Test 1 -- Testing: pushValue + pop + isEmpty"
./test1.sh > TestingFiles/test1Output.txt

if (cmp TestingFiles/test1Output.txt TestingFiles/test1ExpectedOutput.txt)
then
    echo "Test 1 Passed."
else
    echo "Test 1 Failed."
fi

echo "Test 2 -- Testing: pushOperator (ascending)"
./test2.sh > TestingFiles/test2Output.txt

if (cmp TestingFiles/test2Output.txt TestingFiles/test2ExpectedOutput.txt)
then
    echo "Test 2 Passed."
else
    echo "Test 2 Failed."
fi

echo "Test 3 -- Testing: pushOperator (descending) + EOF"
./test3.sh > TestingFiles/test3Output.txt

if (cmp TestingFiles/test3Output.txt TestingFiles/test3ExpectedOutput.txt)
then
    echo "Test 3 Passed."
else
    echo "Test 3 Failed."
fi

echo "Test 4 -- Testing: pushOperator (max)"
./test4.sh > TestingFiles/test4Output.txt

if (cmp TestingFiles/test4Output.txt TestingFiles/test4ExpectedOutput.txt)
then
    echo "Test 4 Passed."
else
    echo "Test 4 Failed."
fi

echo "Test 5 -- Testing: pushOperator (min)"
./test5.sh > TestingFiles/test5Output.txt

if (cmp TestingFiles/test5Output.txt TestingFiles/test5ExpectedOutput.txt)
then
    echo "Test 5 Passed."
else
    echo "Test 5 Failed."
fi

echo "Test 6 -- Testing: delayPop"
./test6.sh > TestingFiles/test6Output.txt

if (cmp TestingFiles/test6Output.txt TestingFiles/test6ExpectedOutput.txt)
then
    echo "Test 6 Passed."
else
    echo "Test 6 Failed."
fi

echo "Test 7 -- Testing: delay + delayPop with multiple clients"
./test7.sh > TestingFiles/test7Output.txt
