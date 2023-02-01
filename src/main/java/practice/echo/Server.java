package practice.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        Socket client = serverSocket.accept();
        System.out.println("Conneted...!");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter printWriter = new PrintWriter(client.getOutputStream());

        while (true) {
            String message = bufferedReader.readLine();
            System.out.println("receive: " + message);
            if (message.toLowerCase(Locale.ROOT).equals("quit") || message.toLowerCase().equals("q")) {
                break;
            }
            printWriter.println(message);
            printWriter.flush();
            System.out.println("send: " + message);
        }
        bufferedReader.close();
        printWriter.close();
        client.close();
    }
}
