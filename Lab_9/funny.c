#include <stdio.h>
#include <stdlib.h>

int main() {
    int *data = malloc(100 * sizeof(int));

    printf("Trying to free a pointer in the middle of the array...\n");

    free(data + 50);

    printf("This may never print!\n");
    return 0;
}