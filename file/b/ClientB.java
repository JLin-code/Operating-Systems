import java.io.*;
import java.net.*;

public class ClientB {

    public static void main(String[] args) throws IOException {

        String host = "localhost";
        int port = 51155; 

        Socket socket = new Socket(host, port);

        BufferedReader keyboard =
                new BufferedReader(new InputStreamReader(System.in));

        BufferedReader in =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));

        PrintWriter out =
                new PrintWriter(socket.getOutputStream(), true);

        String userInput;

        System.out.println("Connected to server on port " + port);
        System.out.println("Enter a number or 'exit':");

        while ((userInput = keyboard.readLine()) != null) {

            out.println(userInput);

            String response = in.readLine();
            System.out.println("Server says: " + response);

            if (userInput.equalsIgnoreCase("exit")) {
                break;
            }
        }

        socket.close();
        System.out.println("Client closed.");
    }
}