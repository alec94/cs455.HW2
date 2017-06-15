package cs455.scaling.task;

import cs455.scaling.worker.TaskQueue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by Alec on 2/27/2017.
 * Read data from receiving thread
 */
public class ReadData extends Task {

    public ReadData(SelectionKey key, TaskQueue queue) {
        this.key = key;
        this.queue = queue;
    }

    private void read() throws IOException {
        SocketChannel socketChannel = (SocketChannel) this.key.channel();

        ByteBuffer readBuffer = ByteBuffer.allocate(8192);

        int numRead;

        //System.out.print("Reading data... ");

        try {
            numRead = socketChannel.read(readBuffer);
        } catch (IOException e) {
            printMessage("Bad SelectionKey. Closing socketChannel.");
            key.cancel();
            socketChannel.close();
            queue.decreaseConnectionCount();
            return;
        }

        if (numRead == -1) {
            key.channel().close();
            key.cancel();
            queue.decreaseConnectionCount();
            printMessage("Socket closed by client.");
        }

        //System.out.println("success");

        byte[] dataCopy = new byte[numRead];
        System.arraycopy(readBuffer.array(), 0, dataCopy, 0, numRead);
        //System.out.println("num read: " + numRead + ", data: " + Arrays.toString(dataCopy));
        key.interestOps(SelectionKey.OP_READ);

        queue.addTask(new ComputeHash(this.key, dataCopy, this.queue));
        //queue.addReceiveCount();
    }

    @Override
    public void run() {
        try {
            read();
        } catch (IOException e) {
            printMessage("Error reading data. " + e.getMessage());
        }
    }


}
