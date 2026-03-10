import java.io.*;
import java.net.*;
import java.util.Random;

public class GuessingServer {

    private static final int PORT = 51157;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Guessing server listening on 127.0.0.1:" + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new GuessHandler(clientSocket)).start();
        }
    }
}

class GuessHandler implements Runnable {

    private final Socket socket;
    private final Random random = new Random();

    GuessHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        int secret = random.nextInt(100) + 1;
        int guessCount = 0;

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println("Guess a number from 1 to 100. Type 'exit' to quit.");

            String line;
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("exit")) {
                    out.println("Bye.");
                    break;
                }

                int guess;
                try {
                    guess = Integer.parseInt(line.trim());
                } catch (NumberFormatException e) {
                    out.println("Please send a valid integer.");
                    continue;
                }

                if (guess < 1 || guess > 100) {
                    out.println("Out of range. Enter a number from 1 to 100.");
                    continue;
                }

                guessCount++;

                if (guess < secret) {
                    out.println("Too low");
                } else if (guess > secret) {
                    out.println("Too high");
                } else {
                    out.println("Correct! Took " + guessCount + " guesses.");
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
