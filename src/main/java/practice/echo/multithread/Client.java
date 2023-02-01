package practice.echo.multithread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket server = new Socket();
        server.connect(new InetSocketAddress("localhost", 8080));
        System.out.println("Connected...!");

        PrintWriter printWriter = new PrintWriter(server.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("send: ");
            String message = scanner.nextLine();
            printWriter.println(message);
            printWriter.flush();
            if (message.toLowerCase(Locale.ROOT).equals("quit") || message.toLowerCase(Locale.ROOT).equals("q")) {
                break;
            }
            String receive = bufferedReader.readLine();
            System.out.println("receive: " + receive);
        }
        printWriter.close();
        bufferedReader.close();
        server.close();

    }
}
