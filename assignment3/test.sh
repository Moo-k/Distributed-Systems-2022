#!/bin/bash
javac *.java
chmod 777 test*.sh

# kill all existing process, redirecting the output so it's not included in output file (for easier comparison)
pkill -9 -e -f "java Paxos" > /dev/null

# remove pre-existing id file and create new id file from 0
rm id.txt
echo "0" >> id.txt

# Test 1: all instant, one proposer(1)
# 9 total : 5 for consensus

echo "Test 1 -- Testing: All Immediate Responses, One Proposer"
./test1.sh > testing/test1output.txt

sleep 5

tail1=$(tail -n 1 "testing/test1expected_output.txt")
sleep 0.1
tail2=$(tail -n 1 "testing/test1output.txt")

echo "Test 1 Expected Output: "
echo $tail1
echo "Test 1 Output: "
echo $tail2

if (diff <(echo "$tail1") <(echo "$tail2"))
then
    echo "Test 1 Passed."
else
    echo "Test 1 Failed."
fi

# kill all existing process, redirecting the output so it's not included in output file (for easier comparison)
pkill -9 -e -f "java Paxos" > /dev/null

# remove pre-existing id file and create new id file from 0
rm id.txt
echo "0" >> id.txt

# Test 2: all instant, two proposers(1,2)
# 9 total : 5 for consensus

echo "Test 2 -- Testing: All Immediate Responses, Two Proposers"
./test2.sh > testing/test2output.txt

sleep 5

tail1=$(tail -n 1 "testing/test2expected_output.txt")
sleep 0.1
tail2=$(tail -n 1 "testing/test2output.txt")

echo "Test 2 Expected Output: "
echo $tail1
echo "Test 2 Output: "
echo $tail2

if (diff <(echo "$tail1") <(echo "$tail2"))
then
    echo "Test 2 Passed."
else
    echo "Test 2 Failed."
fi

# kill all existing process, redirecting the output so it's not included in output file (for easier comparison)
pkill -9 -e -f "java Paxos" > /dev/null

# remove pre-existing id file and create new id file from 0
rm id.txt
echo "0" >> id.txt

# Test 3: all instant excepted for node 2, two proposers(1,2)
# Node 2 has behaviour profile of M2: sometimes immediate, sometimes long delay
# 9 total : 5 for consensus

echo "Test 3 -- Testing: All Immediate Responses Except M2 (sometimes late), Two Proposers"
./test3.sh > testing/test3output.txt

sleep 60


tail1=$(tail -n 1 "testing/test3expected_output.txt")
sleep 0.1
tail2=$(tail -n 1 "testing/test3output.txt")

echo "Test 3 Expected Output: "
echo $tail1
echo "Test 3 Output: "
echo $tail2

if (diff <(echo "$tail1") <(echo "$tail2"))
then
    echo "Test 3 Passed."
else
    echo "Test 3 Failed."
fi

# kill all existing process, redirecting the output so it's not included in output file (for easier comparison)
pkill -9 -e -f "java Paxos" > /dev/null

# remove pre-existing id file and create new id file from 0
rm id.txt
echo "0" >> id.txt

# Test 4: mix of immediate, medium, and late responses, three proposers(0,1,2)
# 17 total : 9 for consensus

echo "Test 4 -- Testing: n=13 nodes, 3 Proposers, Varying response times (immediate,medium,late)"
./test4.sh > testing/test4output.txt

sleep 60


tail1=$(tail -n 1 "testing/test4expected_output.txt")
sleep 0.1
tail2=$(tail -n 1 "testing/test4output.txt")

echo "Test 4 Expected Output: "
echo $tail1
echo "Test 4 Output: "
echo $tail2

if (diff <(echo "$tail1") <(echo "$tail2"))
then
    echo "Test 4 Passed."
else
    echo "Test 4 Failed."
fi

# kill all existing process, redirecting the output so it's not included in output file (for easier comparison)
pkill -9 -e -f "java Paxos" > /dev/null

# remove pre-existing id file and create new id file from 0
rm id.txt
echo "0" >> id.txt