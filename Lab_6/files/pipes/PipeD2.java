import java.io.*;

public class PipeD2 {

    private static void runParent() throws IOException, InterruptedException {

        // We will run the SAME program as child, but detect mode via argument
        ProcessBuilder pb = new ProcessBuilder(
            "java", "-cp", System.getProperty("java.class.path"), 
            PipeD2.class.getName(), "--child"
        );

        // Connect pipes
        pb.redirectInput(ProcessBuilder.Redirect.PIPE);
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);

        Process child = pb.start();

        // Parent → Child
        try (PrintWriter toChild = new PrintWriter(child.getOutputStream(), true)) {
            int number = 25;
            System.out.println("[Parent] Sending: " + number);
            toChild.println(number);
        }

        // Child → Parent
        try (BufferedReader fromChild = new BufferedReader(new InputStreamReader(child.getInputStream()))) {
            String response = fromChild.readLine();
            if (response != null) {
                System.out.println("[Parent] Child returned: " + response);
            }
        }

        // Wait for child to finish
        int exitCode = child.waitFor();
        System.out.println("Child exited with code: " + exitCode);
    }

    // This part runs in the child process
    public static void mainChild() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(System.out, true);

        String line = reader.readLine();
        if (line != null) {
            try {
                int num = Integer.parseInt(line.trim());
                int result = num * 2;
                writer.println(result);
                System.err.println("[Child] Processed " + num + " → " + result); // goes to parent's stderr
            } catch (NumberFormatException e) {
                writer.println("ERROR");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--child")) {
            mainChild();
            return;
        }
        runParent();
    }
}