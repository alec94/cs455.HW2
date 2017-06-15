package cs455.scaling.task;

import cs455.scaling.worker.TaskQueue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by Alec on 2/27/2017.
 * sends computed hash back to client
 */
class DataReply extends Task {
    private final String hash;

    public DataReply(SelectionKey key, String hash, TaskQueue queue) {
        this.key = key;
        this.queue = queue;
        this.hash = hash;
    }

    private void write() throws IOException {
        //System.out.print("Trying to send reply... ");
        key.interestOps(SelectionKey.OP_WRITE);
        SocketChannel socketChannel = (SocketChannel) key.channel();
        byte[] hashBytes = this.hash.getBytes("UTF-8");

        ByteBuffer writeBuffer = ByteBuffer.wrap(hashBytes);

        while (writeBuffer.hasRemaining()) {
            socketChannel.write(writeBuffer);
        }

        //System.out.println("success");

        key.interestOps(SelectionKey.OP_READ);
    }

    public void run() {
        try {
            write();
            queue.addSendCount();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
