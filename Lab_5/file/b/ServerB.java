import java.io.*;
import java.net.*;
import java.util.Random;

public class ServerB {

    public static void main(String[] args) throws IOException {

        int port = 51155; 
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Server started.");
        System.out.println("Listening on port: " + port);

        Socket socket = serverSocket.accept();
        System.out.println("Client connected.");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);

        Random random = new Random();
        String input;

        while ((input = in.readLine()) != null) {

            if (input.equalsIgnoreCase("exit")) {
                out.println("Goodbye!");
                break;
            }

            try {
                int clientNumber = Integer.parseInt(input);
                int randomNumber = random.nextInt(100);
                int result = clientNumber + randomNumber;

                out.println("Server added " + randomNumber +
                        ", result = " + result);

                System.out.println("Client sent: " + clientNumber);
                System.out.println("Responded with result: " + result);

            } catch (NumberFormatException e) {
                out.println("Please send a valid integer or 'exit'");
            }
        }

        socket.close();
        serverSocket.close();
        System.out.println("Server closed.");
    }
}