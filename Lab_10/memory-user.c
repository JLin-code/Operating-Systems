#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char *argv[]) {
    if (argc < 2) {
        printf("Usage: %s <MB>\n", argv[0]);
        return 1;
    }

    int mb = atoi(argv[1]);
    int size = mb * 1024 * 1024 / sizeof(int);

    int *array = malloc(size * sizeof(int));
    if (!array) {
        perror("malloc failed");
        return 1;
    }

    printf("PID: %d\n", getpid());

    while (1) {
        for (int i = 0; i < size; i++) {
            array[i] = i;  // touch memory
        }
    }

    return 0;
}