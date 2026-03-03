import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerE {

    private static final int PORT = 51155;

    private static final int THREAD_POOL_SIZE = 3;
    private static ExecutorService pool =
            Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server listening on 127.0.0.1:" + PORT);
        System.out.println("Thread pool size: " + THREAD_POOL_SIZE);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: "
                    + clientSocket.getInetAddress());

            pool.execute(new ClientHandler(clientSocket));
        }
    }
}

class ClientHandler implements Runnable {

    private Socket socket;
    private Random random = new Random();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true)
        ) {
            String line;

            while ((line = in.readLine()) != null) {

                if (line.equalsIgnoreCase("exit"))
                    break;

                String[] parts = line.split(" ");

                if (parts.length != 2) {
                    out.println("Send two integers separated by space.");
                    continue;
                }

                try {
                    int num1 = Integer.parseInt(parts[0]);
                    int num2 = Integer.parseInt(parts[1]);

                    int randomNumber = random.nextInt(100);
                    int result = num1 + num2 + randomNumber;

                    String response = "Thread "
                            + Thread.currentThread().getName()
                            + " → Sum(" + num1 + "+" + num2
                            + ") + random(" + randomNumber
                            + ") = " + result;

                    out.println(response);

                    System.out.println(response);

                } catch (NumberFormatException e) {
                    out.println("Invalid integers.");
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected.");
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}