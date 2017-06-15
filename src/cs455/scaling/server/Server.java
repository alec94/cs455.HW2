package cs455.scaling.server;

import cs455.scaling.task.ReadData;
import cs455.scaling.worker.TaskQueue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * Created by alec kent on 2/22/17.
 * Server node
 */

public class Server {
    private final TaskQueue TaskQueue;
    private final int port;
    private final Selector selector;
    private int activeConnections = 0;
    private int localPort;

    //private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    private Server(int portNumber, int threadPoolSize) throws IOException {
        this.port = portNumber;
        this.selector = this.initSelector();
        this.TaskQueue = new TaskQueue(threadPoolSize, this);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("ERROR: Wrong number of arguments.");
            System.out.println("USAGE: <port-number> <thread-pool-size>");
            System.exit(-1);
        }

        int portNumber = 0;
        try {
            portNumber = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("ERROR: <port-number> must be an integer.");
            System.exit(-1);
        }

        int threadPoolSize = 0;
        try {
            threadPoolSize = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("ERROR: <thread-pool-size> must be an integer.");
            System.exit(-1);
        }

        try {
            Server server = new Server(portNumber, threadPoolSize);
            server.start();
        } catch (IOException e) {
            System.out.println("Unable to start server.");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private Selector initSelector() throws IOException {
        Selector socketSelector = SelectorProvider.provider().openSelector();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(this.port);
        serverSocketChannel.socket().bind(inetSocketAddress);

        this.localPort = serverSocketChannel.socket().getLocalPort();

        serverSocketChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel socketChannel = serverSocketChannel.accept();
        //Socket socket = socketChannel.socket();
        socketChannel.configureBlocking(false);

        socketChannel.register(this.selector, SelectionKey.OP_READ);
        this.increaseConnectionCount();
        //System.out.println("New connection.");
    }

    synchronized int connectionCount() {
        return this.activeConnections;
    }

    private synchronized void increaseConnectionCount() {
        this.activeConnections++;
    }

    public synchronized void decreaseConnectionCount() {
        this.activeConnections--;
    }

    private void start() {
        TaskQueue.start();
        ReportThread reportThread = new ReportThread(TaskQueue, this);
        Thread thread = new Thread(reportThread);
        thread.start();

        System.out.println("Server start up complete. Number of workers: " + this.TaskQueue.aliveWorkerCount() + ". Listening on port: " + this.localPort);
        //System.out.println("Server thread id: " + Thread.currentThread().getId());
        try {
            while (true) {

                try {
                    this.selector.select();

                    Iterator selectedKeys = this.selector.selectedKeys().iterator();
                    //System.out.println("key count: " + this.selector.keys().size());
                    while (selectedKeys.hasNext()) {
                        SelectionKey key = (SelectionKey) selectedKeys.next();
                        selectedKeys.remove();

                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isAcceptable()) {
                            accept(key);
                        } else if (key.isReadable()) {
                            key.interestOps(SelectionKey.OP_WRITE);
                            TaskQueue.addTask(new ReadData(key, this.TaskQueue));
                            TaskQueue.addReceiveCount();
                        }else{
                            //System.out.println("Unhandled key: " + key.interestOps());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            }
        } finally {
            reportThread.kill();
            System.out.println("Server shutting down.");
        }
    }
}
