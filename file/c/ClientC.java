import java.io.*;
import java.net.*;

public class ClientC {

    public static void main(String[] args) throws IOException {

        String host = "127.0.0.1";   // Loopback address
        int port = 8080;

        Socket socket = new Socket(host, port);

        BufferedReader keyboard =
                new BufferedReader(new InputStreamReader(System.in));

        BufferedReader in =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));

        PrintWriter out =
                new PrintWriter(socket.getOutputStream(), true);

        System.out.println("Connected to 127.0.0.1:" + port);

        while (true) {

            System.out.print("Enter first number (or 'exit'): ");
            String first = keyboard.readLine();
            out.println(first);

            if (first.equalsIgnoreCase("exit")) break;

            System.out.print("Enter second number: ");
            String second = keyboard.readLine();
            out.println(second);

            if (second.equalsIgnoreCase("exit")) break;

            // Only now do we wait for ONE response
            String response = in.readLine();
            System.out.println("Server says: " + response);
        }

        socket.close();
        System.out.println("Client closed.");
    }
}