import java.io.*;
import java.net.*;
import java.util.Random;

public class ServerC {

    public static void main(String[] args) throws IOException {

        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Server listening on 127.0.0.1:" + port);

        Socket socket = serverSocket.accept();
        System.out.println("Client connected.");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);

        Random random = new Random();

        while (true) {

            // Read first number
            String first = in.readLine();
            if (first == null || first.equalsIgnoreCase("exit")) break;

            // Read second number
            String second = in.readLine();
            if (second == null || second.equalsIgnoreCase("exit")) break;

            try {
                int num1 = Integer.parseInt(first);
                int num2 = Integer.parseInt(second);

                int randomNumber = random.nextInt(100);
                int result = num1 + num2 + randomNumber;

                String response = "Sum(" + num1 + "+" + num2 +
                        ") + random(" + randomNumber + ") = " + result;

                out.println(response);

                System.out.println("Received: " + num1 + ", " + num2);
                System.out.println("Sent: " + response);

            } catch (NumberFormatException e) {
                out.println("Invalid input. Send two integers.");
            }
        }

        socket.close();
        serverSocket.close();
        System.out.println("Server closed.");
    }
}