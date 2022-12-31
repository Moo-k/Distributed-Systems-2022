===== Compiling and Running the program =====
First, to compile the program, use 'javac Paxos.java'.
To use the program, use the command 'java Paxos [Behavior] [Number of Nodes] [Proposer/Acceptor] [Own Position]'.

The command line arguments are used as such:

- Behavior:
1: Immediate response
2: Sometimes immediate, sometimes delayed by 5 seconds
3: Sometimes immediate, sometimes does not respond
4: Medium response time: 2 seconds delayed
5: Late response time: 5 seconds delayed

- Number of Nodes:
The total number of nodes in the system.

- Proposer/Acceptor:
0: The node does not want to be the president
1: The node wants to be the president

- Own Position:
The node's own position within the system, to identify themselves to other nodes.

In this implementation, behavior M3 does not work, as the system does not come to a consensus if a proposer goes offline.
If there are multiple proposers, there must be a delay between the proposers, so that there is no race condition in the nodes receiving their IDs, which can cause inconsistent behavior.
After performing a test, the processes must be terminated using 'pkill -9 -e -f "java Paxos"'. The global ID should also be reset, using 'rm id.txt' and 'echo "0" >> id.txt'

===== Testing =====
To use the testing script, give permissions to the testing script test.sh using 'chmod 777 test.sh'.

The testing script will automatically compile the program, as well as removing any remaining processes from previous tests. The global ID will also be reset to 0. The testing script will also provide the necessary permissions for any sub-testing scripts, such as test1.sh.

Test 1:
For the first test, there are 9 nodes in total, from node 0 to node 8. Each node will have an immediate response to any queries, and node 1 will be the proposer. Since there is only one proposer, we expected the accepted ID to be 0.
By comparing the last line of the output with the last line of the expected output, we can avoid any inconsistencies from race conditions, to find the final accepted value.
We expect the proposer (1) to be expected with an ID of (0).

Test 2:
For the second test, there are 9 nodes in total. Each node will have immediate responses, but there are now 2 proposers, node 1 and node 2. To ensure that there is no race condition between the two, there is a sleep command for 1 second between the proposals.
For this test, we expect the second proposer (2) to be accepted, since it will have the higher ID (1).

Test 3:
For the third test, we will test the implementation when M2 occasionally responds very late. There are 9 nodes in total, with 2 proposers, node 1 and node 2. Node 2 will have the behavior profile of M2, who immediately responds sometimes, and responds after a long delay other times.
Again, we expect the second proposer (2) to be accepted, since it will still have the higher ID (1), albeit with delayed responses sometimes. We also allow 60 seconds for the system to reach consensus in the worst case.

Test 4:
For test 4, we will test the implementation for n councilors, with 3 different response times: immediate, medium, and late. We will also use 3 proposers, out of 13 total councilors (node 0 - 12). Nodes 0, 1, and 2 will be the proposers.
We expect the last proposer (2) to be accepted, with an ID of (2). We will also allow 60 seconds for the system to reach a consensus.


Functionality:
+ Paxos implementation works when two councillors send voting proposals at the same time
+ Paxos implementation works in the case where all M1-M9 have immediate responses to voting queries
= Paxos implementation works when M1 – M9 have responses to voting queries suggested by the profiles above, including when M2 or M3 propose and then go offline
    + Partial: M2 works but M3 does not.
= Paxos implementation works with a number ‘n’ of councilors with four profiles of response times: immediate;  medium; late; never
    + Partial: Immediate, Medium, and Late work, but Never does not.