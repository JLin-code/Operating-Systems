#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <string.h>

int main(void) {
    int pipe1[2];   // Parent → Child
    int pipe2[2];   // Child → Parent

    if (pipe(pipe1) == -1) {
        perror("pipe1 failed");
        exit(1);
    }
    if (pipe(pipe2) == -1) {
        perror("pipe2 failed");
        exit(1);
    }

    pid_t pid = fork();
    if (pid == -1) {
        perror("fork failed");
        exit(1);
    }

    if (pid == 0) {
        // ──────────────── CHILD PROCESS ────────────────

        // Close unused ends
        close(pipe1[1]);   // don't write to parent→child
        close(pipe2[0]);   // don't read from child→parent

        // Read number from parent
        char buffer[32];
        ssize_t n = read(pipe1[0], buffer, sizeof(buffer) - 1);
        if (n <= 0) {
            fprintf(stderr, "[Child] Read failed or empty\n");
            close(pipe1[0]);
            close(pipe2[1]);
            exit(1);
        }

        buffer[n] = '\0';  // ensure null-terminated
        int number;
        if (sscanf(buffer, "%d", &number) != 1) {
            fprintf(stderr, "[Child] Invalid number: %s\n", buffer);
            close(pipe1[0]);
            close(pipe2[1]);
            exit(1);
        }

        int result = number * 2;

        // Send result back to parent
        char result_str[32];
        snprintf(result_str, sizeof(result_str), "%d\n", result);

        write(pipe2[1], result_str, strlen(result_str));

        printf("[Child] Received %d, sending back %d\n", number, result);
        fflush(stdout);

        // Clean up
        close(pipe1[0]);
        close(pipe2[1]);

        exit(0);
    }
    else {
        // ──────────────── PARENT PROCESS ────────────────

        // Close unused ends
        close(pipe1[0]);   // don't read from parent→child
        close(pipe2[1]);   // don't write to child→parent

        // Send a number to child
        int number_to_send = 42;
        char msg[32];
        snprintf(msg, sizeof(msg), "%d\n", number_to_send);

        printf("[Parent] Sending number: %d\n", number_to_send);
        fflush(stdout);

        write(pipe1[1], msg, strlen(msg));

        // Read result from child
        char buffer[32];
        ssize_t n = read(pipe2[0], buffer, sizeof(buffer) - 1);
        if (n > 0) {
            buffer[n] = '\0';
            int received;
            if (sscanf(buffer, "%d", &received) == 1) {
                printf("[Parent] Received from child: %d\n", received);
            } else {
                printf("[Parent] Received invalid response: %s\n", buffer);
            }
        } else {
            fprintf(stderr, "[Parent] Read from child failed\n");
        }

        // Clean up
        close(pipe1[1]);
        close(pipe2[0]);

        // Wait for child to finish
        wait(NULL);

        printf("Communication complete.\n");
    }

    return 0;
}