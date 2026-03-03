import java.io.*;
import java.net.*;
import java.util.Random;

public class ServerA {
    public static void main(String[] args) throws IOException {

        // 0 = automatically assign random available port
        ServerSocket serverSocket = new ServerSocket(0);

        int port = serverSocket.getLocalPort();
        System.out.println("Server started.");
        System.out.println("Listening on port: " + port);

        Socket socket = serverSocket.accept();
        System.out.println("Client connected.");

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Random random = new Random();
        int randomNumber = random.nextInt(100); // 0–99

        out.println(randomNumber);
        System.out.println("Sent random number: " + randomNumber);

        socket.close();
        serverSocket.close();
    }
}