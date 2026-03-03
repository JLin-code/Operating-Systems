import java.io.*;
import java.net.*;

public class ClientA {
    public static void main(String[] args) throws IOException {

        String host = "localhost";
        int port = 55590; 

        Socket socket = new Socket(host, port);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String number = in.readLine();
        System.out.println("Received random number from server: " + number);

        socket.close();
    }
}