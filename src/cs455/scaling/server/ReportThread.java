package cs455.scaling.server;

import cs455.scaling.worker.TaskQueue;

import java.util.Date;

/**
 * Created by Alec on 3/4/2017.
 * prints the status message every 5 seconds
 */
class ReportThread implements Runnable {
    private final TaskQueue taskQueue;
    private final Server server;
    private volatile boolean done = false;

    public ReportThread(TaskQueue taskQueue, Server server) {
        this.taskQueue = taskQueue;
        this.server = server;
    }

    public void kill() {
        this.done = true;
        System.out.println("ReportThread shutting down.");
    }

    public void run() {
        while (!done) {
            int throughput = taskQueue.getSendCount() / 5;

            System.out.println("[" + new Date().toString() + "] Current Server Throughput: " + throughput + " messages/s, Active Client Connections: " + server.connectionCount());
            //System.out.println("Queue size: " + taskQueue.getQueueSize());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }
}
