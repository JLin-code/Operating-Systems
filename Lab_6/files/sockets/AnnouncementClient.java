import java.io.*;
import java.net.*;

public class AnnouncementClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 51156;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(HOST, PORT);

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Thread receiver = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException ignored) {
            }
        });
        receiver.setDaemon(true);
        receiver.start();

        String input;
        while ((input = keyboard.readLine()) != null) {
            out.println(input);
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
        }

        socket.close();
    }
}
