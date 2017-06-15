package cs455.scaling.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Alec on 3/4/2017.
 * Receives data from server and sends it to the client
 */
class ReceivingThread implements Runnable {
    private final Client parent;
    private final SocketChannel socketChannel;

    private final ByteBuffer buffer = ByteBuffer.allocate(8192);

    public ReceivingThread(Client parent, SocketChannel socketChannel) {
        this.parent = parent;
        this.socketChannel = socketChannel;
    }

    public void run() {
        while (true) {

            buffer.clear();

            int numRead;

            try {
                numRead = socketChannel.read(this.buffer);
            } catch (IOException e) {
                System.out.println("Unable to read from socket channel");
                try {
                    socketChannel.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    parent.kill();
                }
                break;
            }

            if (numRead == -1) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Socket closed by client.");
                parent.kill();
                break;
            }

            //System.out.println("Data received.");

            byte[] dataCopy = new byte[numRead];
            System.arraycopy(this.buffer.array(), 0, dataCopy, 0, numRead);

            //System.out.println("data: " + Arrays.toString(dataCopy));

            String hash = null;
            try {
                hash = new String(dataCopy, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            parent.checkMessage(hash);
        }
    }
}
