package cs455.scaling.client;

import cs455.scaling.util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by alec kent on 2/22/17.
 * Client node, one per system
 */
public class Client {
    private final LinkedList<String> sentMessages = new LinkedList<String>();
    private final SocketChannel socketChannel;
    private int sendDelay;
    private int messageRate;
    private int sendTotal = 0;
    private int receiveTotal = 0;
    private StatThread statThread;
    //private final ByteBuffer sendBuffer = ByteBuffer.allocate(8192);

    private Client(String serverHost, int serverPort, int messageRate) throws IOException {
        this.socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
        this.sendDelay = 1000 / messageRate;
        this.messageRate = messageRate;
    }

    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("ERROR: Wrong number of arguments.");
            System.out.println("USAGE: <server-host> <server-port> <message-rate>");
            System.exit(-1);
        }

        String serverHost = args[0];

        int serverPort = 0;
        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("ERROR: <server-port> must be an integer.");
            System.exit(-1);
        }

        int messageRate = 0;
        try {
            messageRate = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.out.println("ERROR: <message-rate> must be an integer.");
            System.exit(-1);
        }

        try {
            Client client = new Client(serverHost, serverPort, messageRate);
            client.start();

        } catch (IOException e) {
            //System.out.println("Error starting client.\n" + e.getMessage());
        }

    }

    public void checkMessage(String messageHash) {
        synchronized (sentMessages) {
            if (!sentMessages.removeFirstOccurrence(messageHash)) {
                System.out.println("Unable to remove hash: " + messageHash + ", unprocessed: " + sentMessages.size());
            } else {
                receiveTotal++;
            }
        }
    }

    public synchronized int getSendTotal() {
        return this.sendTotal;
    }

    public synchronized int getReceiveTotal() {
        return this.receiveTotal;
    }

    private void sendMessages() throws IOException {

        Random random = new Random();

        while (true) {
            byte[] bytes = new byte[8000];

            random.nextBytes(bytes);
            ByteBuffer sendBuffer = ByteBuffer.wrap(bytes);

            synchronized (sentMessages) {
                sentMessages.addLast(Util.SHA1FromBytes(bytes));
            }

            while (sendBuffer.hasRemaining()) {
                socketChannel.write(sendBuffer);
            }

            sendTotal++;

            try {
                Thread.sleep(sendDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void kill() {
        System.out.println("Client shutting down.");
        this.statThread.kill();
        System.exit(-1);
    }

    private void start() throws IOException {

        while (!socketChannel.finishConnect()) {
            if (socketChannel.isConnected()) {
                break;
            }
        }

        Thread thread = new Thread(new ReceivingThread(this, socketChannel));
        thread.start();

        this.statThread = new StatThread(this);
        thread = new Thread(this.statThread);
        thread.start();

        System.out.println("New client started, send rate: " + this.messageRate + " messages/s.");

        sendMessages();
    }

}
