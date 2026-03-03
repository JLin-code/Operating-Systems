import java.io.*;
import java.net.*;
import java.util.Random;

public class ServerD {

    private static final int PORT = 51155;

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server listening on 127.0.0.1:" + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: "
                    + clientSocket.getInetAddress());

            // Handle each client in a new thread
            new Thread(new ClientHandler(clientSocket)).start();
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

                // Expecting: "num1 num2"
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

                    String response = "Sum(" + num1 + "+" + num2 +
                            ") + random(" + randomNumber +
                            ") = " + result;

                    out.println(response);

                    System.out.println("Handled client "
                            + socket.getInetAddress()
                            + " → " + response);

                } catch (NumberFormatException e) {
                    out.println("Invalid integers.");
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected unexpectedly.");
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}