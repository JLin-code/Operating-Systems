import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.*;

public class AnnouncementServer {

    private static final int PORT = 51156;
    private static final String HOST = "127.0.0.1";

    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final Path historyFile = Paths.get("announcement_history.txt");
    private static final Object historyLock = new Object();

    public static void main(String[] args) throws IOException {
        if (!Files.exists(historyFile)) {
            Files.createFile(historyFile);
        }

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Announcement server listening on " + HOST + ":" + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ClientHandler(socket)).start();
        }
    }

    private static void appendHistory(String line) {
        synchronized (historyLock) {
            try {
                Files.write(
                        historyFile,
                        (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                System.out.println("Could not write history: " + e.getMessage());
            }
        }
    }

    private static void broadcast(String message, String excludeNickname) {
        for (ClientHandler handler : clients.values()) {
            if (excludeNickname != null && excludeNickname.equals(handler.nickname)) {
                continue;
            }
            handler.send(message);
        }
    }

    private static String userList() {
        if (clients.isEmpty()) {
            return "(none)";
        }
        return String.join(", ", clients.keySet());
    }

    static class ClientHandler implements Runnable {

        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String nickname;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        void send(String msg) {
            if (out != null) {
                out.println(msg);
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Enter nickname:");

                while (true) {
                    String name = in.readLine();
                    if (name == null) {
                        return;
                    }

                    name = name.trim();
                    if (name.isEmpty()) {
                        out.println("Nickname cannot be empty. Enter nickname:");
                        continue;
                    }

                    if (clients.putIfAbsent(name, this) != null) {
                        out.println("Nickname already in use. Enter nickname:");
                        continue;
                    }

                    nickname = name;
                    break;
                }

                String joinMsg = "[System] " + nickname + " joined. Active users (" + clients.size() + "): " + userList();
                appendHistory(joinMsg);
                broadcast(joinMsg, null);

                out.println("[System] Welcome, " + nickname + ". Type '/users' for active users or 'exit' to leave.");

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("exit")) {
                        break;
                    }

                    if (line.equalsIgnoreCase("/users")) {
                        out.println("[System] Active users (" + clients.size() + "): " + userList());
                        continue;
                    }

                    String msg = nickname + ": " + line;
                    appendHistory(msg);
                    broadcast(msg, nickname);
                }

            } catch (IOException ignored) {
            } finally {
                if (nickname != null) {
                    clients.remove(nickname);
                    String leaveMsg = "[System] " + nickname + " left. Active users (" + clients.size() + "): " + userList();
                    appendHistory(leaveMsg);
                    broadcast(leaveMsg, null);
                }

                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
