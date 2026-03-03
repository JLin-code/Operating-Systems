import java.io.*;

public class TwoWayPipesThreads {

    public static void main(String[] args) throws IOException {

        // Pipe 1: Parent → Child
        PipedOutputStream parentToChildOut = new PipedOutputStream();
        PipedInputStream  parentToChildIn  = new PipedInputStream(parentToChildOut);

        // Pipe 2: Child → Parent
        PipedOutputStream childToParentOut = new PipedOutputStream();
        PipedInputStream  childToParentIn  = new PipedInputStream(childToParentOut);

        // "Child" thread (simulates forked child process)
        Thread child = new Thread(() -> {
            try (
                BufferedReader readerFromParent = new BufferedReader(new InputStreamReader(parentToChildIn));
                PrintWriter writerToParent = new PrintWriter(childToParentOut, true)
            ) {
                // Read number from parent
                String line = readerFromParent.readLine();
                if (line != null) {
                    try {
                        int number = Integer.parseInt(line.trim());
                        int result = number * 2;

                        // Send result back to parent
                        writerToParent.println(result);
                        System.out.println("[Child] Received " + number + ", sending back " + result);
                    } catch (NumberFormatException e) {
                        writerToParent.println("ERROR: not a number");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Start child first (important to avoid deadlock)
        child.start();

        // Parent code
        try (
            PrintWriter writerToChild = new PrintWriter(parentToChildOut, true);
            BufferedReader readerFromChild = new BufferedReader(new InputStreamReader(childToParentIn))
        ) {
            // Send a number to child
            int numberToSend = 42;
            System.out.println("[Parent] Sending number: " + numberToSend);
            writerToChild.println(numberToSend);
            writerToChild.flush();

            // Read result from child
            String response = readerFromChild.readLine();
            if (response != null) {
                System.out.println("[Parent] Received from child: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Wait for child thread to finish
        try {
            child.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Communication complete.");
    }
}