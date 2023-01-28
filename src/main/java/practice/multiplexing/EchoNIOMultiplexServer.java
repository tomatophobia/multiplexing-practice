package practice.multiplexing;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoNIOMultiplexServer {
    private static final String EXIT = "EXIT";

    public static void main(String[] args) throws IOException {
        // Create Selector
        Selector selector = Selector.open();
        // Create ServerSocketChannel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", 8080));
        serverSocket.configureBlocking(false);
        // Register ServerSocketChannel to the Selector
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(256);
        System.out.println("server started!");
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectionKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                // Case ServerSocketChannel
                if (key.isAcceptable()) {
                    register(selector, serverSocket);
                }
                // Case SocketChannel
                if (key.isReadable()) {
                    answerWithEcho(buffer, key);
                }
                iter.remove();
            }
        }
    }

    private static void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer); // write
        buffer.flip();  // w -> r

        ByteBuffer copy = buffer.duplicate();
        byte[] arr = new byte[copy.limit() - copy.position()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = copy.get();
        }
        String msg = new String(arr).trim();
        System.out.println("client send: " + msg);

        client.write(buffer);  // read
        buffer.clear(); // r -> w

        if (msg.equals(EXIT)) {
            client.close();
            System.out.println("closed client.");
            return;
        }
    }

    private static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        if (client == null) {
            System.out.println("Socket channel is null");
            return;
        }
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("connected new client.");
    }
}
