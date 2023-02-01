package practice.echo.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class EchoHandler implements Handler{
    static final int RECEIVING = 0, SENDING = 1;

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    final ByteBuffer buffer = ByteBuffer.allocate(256);
    int state = RECEIVING;

    public EchoHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        // Attach a handler to handle when an event occurs in SocketChannel.
        selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
        // 굳이 wake up 필요 없을 듯 select를 할 다른 쓰레드가 존재하지 않음
        // https://hbase.tistory.com/39
        // selector.wakeup();
    }

    @Override
    public void handle() {
        try {
            if (state == RECEIVING) {
                receive();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void receive() throws IOException {
        int readCount = socketChannel.read(buffer);
        System.out.println("receive: " + new String(buffer.array()).trim());
        if (readCount > 0) {
            buffer.flip();
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        state = SENDING;
    }

    void send() throws IOException {
        socketChannel.write(buffer);
        System.out.println("send: " + new String(buffer.array()).trim());
        buffer.clear();
        selectionKey.interestOps(SelectionKey.OP_READ);
        state = RECEIVING;
    }
}
