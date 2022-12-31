a1805637 Yuanxi Wang

To compile all files, run "javac *.java". If using the testing script (testall.sh), the script will compile the program, as well as removing any preexisting feeds.

To run the programs individually, use "java GETClient"/"java ContentServer"/"java AggregationServer [port]". If no port is provided for the AggregationServer, it will use the default port 4567. Each program will then prompt the user for inputs.

To use the testing script, give permissions to the testing script testall.sh using 'chmod 777 testall.sh'

Run the testing script using "./testall.sh".
The testing script will then compile all the java files, as well as removing any previous feeds.

Test 1 tests the basic functionality of the ContentServer, AggregationServer and the Client, such that the Content Server is able to PUT text to the AS, which the Client can then GET from the AS.

Test 2 tests the persistence of the Aggregation Server, such that if it crashes, the existing feed can still be used when a Client tries to GET the feed.

Test 3 tests the ability of multiple Content Servers to send PUT requests, which will then be ordered by the Aggregation Server in the order in which they arrived. The Client then receives the ordered version whenever it sends a GET request to the AS.

Test 4 tests the ability of multiple Clients to simultaneously send GET requests to the Aggregation Server, which should return the same result to each of them.

Test 5 tests the implementation of the Lamport Clock, making sure that the lamport clock is correctly updated with each communication between the processes.
After starting the AS, CS1 PUTs its message to the AS (LC=1), then Client1 GETs the feed at LC=2. Two more messages are then sent from CS2 and CS3 (LC=3&4), then Client2 GETs the updated feed at LC=5.

Note: While testing the testall.sh script on cs50 sandbox, race conditions occasionally occurred for tests 3-5. Even after increasing the delay between commands, they still occurred. This may be due to how sandbox handles background processes.

Basic functionality:

+ Text sending works - please send text strings instead of fully formatted XML (see below for bonus)
+ client, Atom server and content server processes start up and communicate
+ PUT operation works for one content server
+ GET operation works for many read clients
- Atom server expired feeds works (12s)
+ Retry on errors (server not available etc) works

Full functionality:

+ Lamport clocks are implemented
? All error codes are implemented
? Content servers are replicated and fault tolerant

Bonus functionality:
- XML parsing works

The heartbeat system was not implemented, thus feeds will not expire, even after the Content Server dies. In the Aggregation Server, I had planned to start a thread which asynchronously checked for heartbeats from the Content Servers, removing the file associated with Content Servers that expired.
However, since the heartbeat system I had implemented used the same IO streams as the PUT commands, the heartbeat could occur during a PUT operation, ending up in the feed. Furthermore, since the heartbeat mechanism will also die if and when the Aggregation Server crashes, the heartbeat will lose track of which Content Servers were active, and thus delete all files, resulting in the feed losing its persistence.
One possible solution to this problem is to have the Aggregation Server send the file name to the Content Server when the feed is updated, so that the Content Server can remind the AS of which file it is responsible for. However, this may lead to security issues, if the CS is able to hijack the feeds of other Content Servers.

The error codes are implemented in the Aggregation Server, but are not sent to the Clients and Content Servers.

The contents from content servers are replicated in the feed directory such that it can be recovered if the AS crashes. However, they are stored according to the Lamport Clock values when they were last updated. Therefore, if the Aggregation Server were to crash, any new inputs at the same LC values will overwrite the old file.
While this solution to the feed provides persistence to the feed such that the AS will not lose the data after a restart, old files may be overwritten after the restart.