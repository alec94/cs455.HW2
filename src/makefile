JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	cs455/scaling/client/Client.java \
	cs455/scaling/client/ReceivingThread.java \
	cs455/scaling/client/StatThread.java \
	cs455/scaling/server/ReportThread.java \
	cs455/scaling/server/Server.java \
	cs455/scaling/task/ComputeHash.java \
	cs455/scaling/task/DataReply.java \
	cs455/scaling/task/ReadData.java \
	cs455/scaling/task/Task.java \
	cs455/scaling/util/Util.java \
	cs455/scaling/worker/TaskQueue.java \
	cs455/scaling/worker/Worker.java
	
default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) -r *.class