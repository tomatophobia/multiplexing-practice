package practice.echo.reactor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final Thread reactorThread = new Thread(new Reactor(8080));
        reactorThread.start();
        reactorThread.join();
    }
}
