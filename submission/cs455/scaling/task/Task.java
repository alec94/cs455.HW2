package cs455.scaling.task;

import cs455.scaling.worker.TaskQueue;

import java.nio.channels.SelectionKey;

/**
 * Created by Alec on 2/22/2017.
 * Base interface used by all tasks
 */
public abstract class Task implements Runnable {
    SelectionKey key;
    TaskQueue queue;

    void printMessage(String message) {
        Long id = Thread.currentThread().getId();

        System.out.println("[Thread " + id + "]: " + message);
    }
}
