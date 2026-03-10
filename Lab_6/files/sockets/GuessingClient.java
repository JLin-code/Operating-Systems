import java.io.*;
import java.net.*;

public class GuessingClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 51157;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(HOST, PORT);

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String welcome = in.readLine();
        if (welcome != null) {
            System.out.println(welcome);
        }

        String input;
        while ((input = keyboard.readLine()) != null) {
            out.println(input);

            String response = in.readLine();
            if (response == null) {
                break;
            }

            System.out.println(response);

            if (response.startsWith("Correct!") || response.equals("Bye.")) {
                break;
            }
        }

        socket.close();
    }
}
