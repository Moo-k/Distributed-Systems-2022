a1805637 Yuanxi Wang

To compile, use "javac *.java" in /sorterrmi directory
Start rmi registry using "rmiregistry &"
Start server in sorterrmi directory using "java SorterServer"

If not using automated testing scripts, start client in another terminal in the /sorterrmi directory using "java SorterClient"

Otherwise, run "./testAll.sh" to run all automated testing scripts.
Before running the test scripts, they must all have permissions to execute, which can be given using "chmod 777 testAll.sh"
testAll.sh will then set the permissions for all other testing scripts.
If any tests fail, the script will print where the differences are in the generated output and expected output.

The first 6 tests are for single clients.
In test 1, the functionality of "pushValue", "pop", "isEmpty", and "close" are tested.
In test 2, the functionality of "pushOperator" using "ascending" is tested.
In test 3, the functionality of "pushOperator" using "descending" is tested, as well as the 'EOF' message when there is no "close" command in the test input.
In test 4, the functionality of "max" is tested, both in the case of a single maximum element, as well as when there are multiple maximum elements.
In test 5, the functionality of "min" is tested, both in the case of a single minimum element, as well as when there are multiple minimum elements.
In test 6, the functionality of "delayPop" is tested, but it is difficult to ensure that the proper amount of time is being delayed in the function, especially since it will likely not be exactly the amount of time given.
In an improved implementation of test 6, another thread can be started at the same time as the delayPop function, which checks that it has been at least x milliseconds before a response is given.

To better show the behavior when there are multiple clients, and extra function "delay" was implemented, which is similar to "delayPop", but does not pop an element off the stack.
In test 7, multiple clients are run simultaneously, by using multiple bash scripts. Using delays, it is shown that each client can access the same stack, and other clients can perform actions while a client is waiting for a delay to finish. However, while this results in slightly more deterministic outputs, it does not fully solve potential race conditions, as threads are not locked while waiting for others to finish.

The following is the expected behavior at certain times, based on the time (ms) since execution.
At time 0, thread1.sh pushes '1' onto the stack.
At time 200, thread2.sh pushes '2' onto the stack.
At time 300, thread3.sh issues 'delayPop', to be executed in 1500ms, at time 1800.
At time 400, thread4.sh issues 'pop', which pops '2' from the stack.
At time 500, thread1.sh pushes '3' onto the stack, and closes its client.
At time 600, thread2.sh pushes '4' onto the stack, and closes its client.
At time 700, thread4.sh issues 'pop', which pops '4' from the stack. thread4.sh closes its client.
At time 1800, thread3.sh completes its 'delayPop', which was issued at time 300, popping '3' from the stack. It then issues another 'pop', which pops '1' from the stack. thread3.sh checks that the stack is empty, and closes its client.

From test7ExpectedOutput.txt, it can be seen that the order in which the elements are pushed and popped to and from the stack is correct, but there is a race condition in the first few lines of the output, where all 4 scripts are executed simultaneously. Thus, this test is not deterministic, but demonstrates that the order in which tests are executed is correct.

While it is possible to automate the testing for test 7, as it is, the output must be manually checked to make sure that all actions executed in the correct order.
