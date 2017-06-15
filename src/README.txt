Name: Alec Kent
Class: CS 455
Assignment: HW2-PC
Date: 3/6/17

Assignment 2: Scalable Server Design: Using Thread Pools to Manage Active Network Connections

As part of this assignment you will be developing a server to handle network traffic by designing and building your own thread pool. This thread pool will have a configurable number of threads that will be used to perform tasks relating to network communications. Specifically, you will use this thread pool to manage all tasks relating to network communications. This includes: managing incoming network connections, receiving data over these network connections, and sending data over any of these links.

Files:
	cs455/scaling/client/Client.java - Client that sends data to the server
	cs455/scaling/client/ReceivingThread.java - Receives data from the server for the client
	cs455/scaling/clientStatThread.java - Reports client stats every 10 seconds
	cs455/scaling/server/ReportThread.java - Reports server stats every 5 seconds
   	cs455/scaling/server/Server.java - Receives data from clients and sends hashes in reply
   	cs455/scaling/task/ComputeHash.java - task which computes the data hash
   	cs455/scaling/task/DataReply.java - task which sends the hash back to the client
   	cs455/scaling/task/ReadData.java - task which reads data from the socket channel
    cs455/scaling/task/Task.java - abstract class extended by the tasks
   	cs455/scaling/util/Util.java - contains utility methods
   	cs455/scaling/worker/Worker.java - executes tasks from the queue
    cs455/scaling/worker/TaskQueue.java - task queue that workers pull from

Main methods are in Client.java and Server.java
