package practice.multiplexing;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class EchoNIOMultiplexClient {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost", 8080));
        // https://jamssoft.tistory.com/221 => 자바 버퍼에 대한 자세한 설명

        ByteBuffer buffer = ByteBuffer.allocate(256); // starts with write mode

        while (true) {
            System.out.print("send: ");
            String msg = sc.nextLine();
            buffer.put(msg.getBytes()); // write
            buffer.flip(); // w -> r
            client.write(buffer); // read
            buffer.clear();  // clear and r -> w
            client.read(buffer); // write
            buffer.flip(); // w -> r
            byte[] arr = new byte[buffer.limit() - buffer.position()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = buffer.get();
            }
            String ans = new String(arr).trim();
            System.out.println("server: " + ans);
            buffer.clear(); // clear and r -> w

            if (ans.equals("EXIT")) {
                System.out.println("Bye!");
                break;
            }
        }
    }
}
