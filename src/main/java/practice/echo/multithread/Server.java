package practice.echo.multithread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(8080);
        List<Thread> pool = new ArrayList<>();
        final int THREAD_LIMIT = 10;

        while (true) {
            Socket socket = serverSocket.accept();
            Handler handler = new Handler(socket);
            Thread worker = new Thread(handler);
            // Thread.activeCount 써도 되지만 그냥 연습이니까
            while (pool.size() >= THREAD_LIMIT) {
                Thread.sleep(1000);
                pool.removeIf(t -> !t.isAlive());
            }
            worker.start();
            pool.add(worker);
        }
    }
}

class Handler implements Runnable {
    Socket socket;

    Handler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Started...!");
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
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
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closed...!");
        }
    }
}
