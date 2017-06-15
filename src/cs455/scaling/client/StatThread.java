package cs455.scaling.client;

import java.util.Date;

/**
 * Created by Alec on 3/4/2017.
 * Prints the send and receive totals every 10 seconds
 */
class StatThread implements Runnable {
    private final Client parent;
    private volatile boolean done = false;

    public StatThread(Client parent) {
        this.parent = parent;
    }

    public void kill() {
        this.done = true;
    }

    public void run() {
        while (!done) {
            System.out.println("[" + new Date().toString() + "] Total Sent Count: " + parent.getSendTotal() + ", Total Received Count: " + parent.getReceiveTotal());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("StatThread shutting down.");
    }
}
