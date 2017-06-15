package cs455.scaling.worker;

import cs455.scaling.task.Task;
import cs455.scaling.server.Server;

import java.util.LinkedList;

/**
 * Created by Alec on 2/22/2017.
 * TaskQueue class controls access to tasks
 */
public class TaskQueue {
    private final Worker[] workers;
    private final LinkedList<Task> queue = new LinkedList<Task>();
    private final Server parent;
    private final Object countLock = new Object();
    private int sendCount = 0;
    private int receiveCount = 0;

    public TaskQueue(int poolSize, Server parent) {
        //Selector selector1 = selector;
        this.parent = parent;
        workers = new Worker[poolSize];

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(this);
        }
    }

    public int getSendCount() {
        synchronized (countLock) {
            int count = this.sendCount;
            this.sendCount = 0;
            return count;
        }
    }

    public int getQueueSize() {
        int count;
        synchronized (queue) {
            count = queue.size();
        }
        return count;
    }

    public void decreaseConnectionCount() {
        parent.decreaseConnectionCount();
    }

    public void addSendCount() {
        synchronized (countLock) {
            this.sendCount++;
        }
    }

    public void addReceiveCount() {
        synchronized (countLock) {
            this.receiveCount++;
        }
    }

    public void addTask(Task task) {
        synchronized (queue) {
            queue.addLast(task);
            queue.notify();
        }
    }

    public int aliveWorkerCount() {
        int count = 0;

        for (Worker worker : workers) {
            if (worker.isAlive()) {
                count++;
            }
        }

        return count;
    }

    LinkedList<Task> getQueue() {
        return this.queue;
    }

    public void start() {
        for (Worker worker : workers) {
            if (!worker.isAlive()) {
                worker.start();
            }
        }
    }
}
