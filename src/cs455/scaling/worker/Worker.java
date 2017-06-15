package cs455.scaling.worker;

import cs455.scaling.task.Task;

/**
 * Created by Alec on 2/22/2017.
 * Thread which does work
 */
class Worker extends Thread {
    private final TaskQueue TaskQueue;

    Worker(TaskQueue TaskQueue) {
        this.TaskQueue = TaskQueue;
    }

    private void processTask(Task task) {
        task.run();
    }

    private void printMessage(String message) {
        Long id = Thread.currentThread().getId();
        System.out.println("[Thread " + id + "]: " + message);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Task task;
                synchronized (TaskQueue.getQueue()) {
                    while (TaskQueue.getQueue().isEmpty()) {
                        try {
                            TaskQueue.getQueue().wait();
                        } catch (InterruptedException e) {
                            //ignored
                        }
                    }

                    task = TaskQueue.getQueue().removeFirst();
                }

                try {
                    processTask(task);
                } catch (Exception e) {
                    printMessage("Error in worker thread. " + e.getMessage());
                    break;
                }
            }
        } finally {
            printMessage("worker shutting down.");
        }
    }
}
