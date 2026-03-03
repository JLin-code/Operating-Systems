#include <stdio.h>
#include <unistd.h>

int main() {
    int p2c[2], c2p[2];
    pipe(p2c); pipe(c2p);

    pid_t pid = fork();
    if (pid == 0) {
        // Child
        close(p2c[1]); close(c2p[0]);
        int num;
        read(p2c[0], &num, sizeof(num));
        num *= 2;
        write(c2p[1], &num, sizeof(num));
    } else {
        // Parent
        close(p2c[0]); close(c2p[1]);
        int num = 7;
        write(p2c[1], &num, sizeof(num));
        read(c2p[0], &num, sizeof(num));
        printf("Parent received: %d\n", num); // should print 14
    }
    return 0;
}