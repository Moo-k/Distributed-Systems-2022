#!/bin/bash
javac *.java
chmod 777 test*.sh

# Test 1: Basic functionality, 1 AS, 1 CS, 1 GETClient
echo "Test 1 -- Testing GETClient, ContentServer, AggregationServer all start up and communicate"
rm -r ./feed # remove feed directory

# Start Aggregation Server
java AggregationServer &
sleep 2
# Start Content Server with test input
java ContentServer < Test1/test1CSInput.txt &
sleep 2
# Start GETClient to get feed from Aggregation Server
java GETClient < Test1/test1ClientInput.txt > Test1/test1Output.txt
sleep 2

# Kill Aggregation Server
pkill -9 -e -f "java AggregationServer"

if (cmp Test1/test1Output.txt Test1/test1ExpectedOutput.txt)
then
    echo "====================Test 1 Passed.===================="
else
    echo "====================Test 1 Failed.===================="
fi

# Test 2: AS is persistent, can recover from crash
echo "Test 2 -- Testing Aggregation Server Persistence, Recovery From Crash"

# Using feed from previous test
java AggregationServer &
sleep 2
java GETClient < Test2/test2ClientInput.txt > Test2/test2Output.txt
sleep 2

pkill -9 -e -f "java AggregationServer"

if (cmp Test2/test2Output.txt Test2/test2ExpectedOutput.txt)
then
    echo "====================Test 2 Passed.===================="
else
    echo "====================Test 2 Failed.===================="
fi

# Test 3: Multiple Content Servers
echo "Test 3 -- Testing Multiple Content Servers"

rm -r ./feed # remove feed directory

# Start Aggregation Server
java AggregationServer &
sleep 2
# Start Content Server 1
java ContentServer < Test3/test3CSInput1.txt &
sleep 2
# Start Content Server 2
java ContentServer < Test3/test3CSInput2.txt &
sleep 2
# Start Content Server 3
java ContentServer < Test3/test3CSInput3.txt &
sleep 2
# Start GETClient to get feed from Aggregation Server
java GETClient < Test3/test3ClientInput.txt > Test3/test3Output.txt
sleep 2

# Kill Aggregation Server
pkill -9 -e -f "java AggregationServer"

if (cmp Test3/test3Output.txt Test3/test3ExpectedOutput.txt)
then
    echo "====================Test 3 Passed.===================="
else
    echo "====================Test 3 Failed.===================="
fi

# Test 4: Multiple GETClients
echo "Test 4 -- Testing Multiple GETClients"

# Using feed from previous test
java AggregationServer &
sleep 2
# Client 1
java GETClient < Test4/test4ClientInput.txt > Test4/test4Output1.txt
sleep 2
# Client 2
java GETClient < Test4/test4ClientInput.txt > Test4/test4Output2.txt
sleep 2
# Client 3
java GETClient < Test4/test4ClientInput.txt > Test4/test4Output3.txt
sleep 2

# Kill Aggregation Server
pkill -9 -e -f "java AggregationServer"

if (cmp Test4/test4Output1.txt Test4/test4ExpectedOutput1.txt && cmp Test4/test4Output2.txt Test4/test4ExpectedOutput2.txt && cmp Test4/test4Output3.txt Test4/test4ExpectedOutput3.txt)
then
    echo "====================Test 4 Passed.===================="
else
    echo "====================Test 4 Failed.===================="
fi

# Test 5: LamportClock
echo "Test 5 -- Testing Lamport Clock"

rm -r ./feed # remove feed directory

java AggregationServer &
sleep 2
# Content Server 1
java ContentServer < Test5/test5CSInput1.txt &
sleep 2
# Client 1
java GETClient < Test5/test5ClientInput.txt > Test5/test5Output1.txt
sleep 2
# Start Content Server 2
java ContentServer < Test5/test5CSInput2.txt &
sleep 2
# Start Content Server 3
java ContentServer < Test5/test5CSInput3.txt &
sleep 2
# Client 2
java GETClient < Test5/test5ClientInput.txt > Test5/test5Output2.txt
sleep 2

# Kill Aggregation Server
pkill -9 -e -f "java AggregationServer"

if (cmp Test5/test5Output1.txt Test5/test5ExpectedOutput1.txt && cmp Test5/test5Output2.txt Test5/test5ExpectedOutput2.txt)
then
    echo "====================Test 5 Passed.===================="
else
    echo "====================Test 5 Failed.===================="
fi