package cs455.scaling.task;

import cs455.scaling.util.Util;
import cs455.scaling.worker.TaskQueue;

import java.nio.channels.SelectionKey;

/**
 * Created by Alec on 2/27/2017.
 * Turn data byte[] into a message hash and creates a new DataReply task
 */
class ComputeHash extends Task {
    private final byte[] data;

    ComputeHash(SelectionKey key, byte[] data, TaskQueue queue) {
        this.key = key;
        this.data = data;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            String hash = Util.SHA1FromBytes(data);
            //System.out.println("message hash: " + hash);
            queue.addTask(new DataReply(this.key, hash, this.queue));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
