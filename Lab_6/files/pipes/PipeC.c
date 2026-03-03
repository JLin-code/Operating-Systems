#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>

int main() {
    int fd[2]; pipe(fd);
    pid_t pid = fork();
    srand(time(NULL));

    if (pid == 0) {
        // Consumer
        close(fd[1]);
        int num;
        while (read(fd[0], &num, sizeof(num)) > 0) {
            if (num == -1) break;
            printf("Consumer got %d squared = %d\n", num, num*num);
        }
    } else {
        // Producer
        close(fd[0]);
        for (int i=0; i<10; i++) {
            int n = rand() % 10;
            write(fd[1], &n, sizeof(n));
        }
        int end = -1; write(fd[1], &end, sizeof(end));
    }
    return 0;
}