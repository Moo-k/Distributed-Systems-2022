#!/bin/bash

java Paxos 1 9 1 0 &
sleep 1
java Paxos 4 9 1 1 &
sleep 1
java Paxos 5 9 1 2 &
java Paxos 5 9 0 3 &
java Paxos 4 9 0 4 &
java Paxos 1 9 0 5 &
java Paxos 4 9 0 6 &
java Paxos 5 9 0 7 &
java Paxos 4 9 0 8 &
java Paxos 5 9 0 9 &
java Paxos 1 9 0 10 &
java Paxos 4 9 0 11 &
java Paxos 5 9 0 12 &