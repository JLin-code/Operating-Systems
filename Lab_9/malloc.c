#include <stdio.h>
#include <stdlib.h>

int main() {
    // allocate memory for one integer
    int *ptr = (int *) malloc(sizeof(int));

    // check if allocation succeeded
    if (ptr == NULL) {
        printf("Memory allocation failed\n");
        return 1;
    }

    // use the memory
    *ptr = 42;
    printf("Value: %d\n", *ptr);

    //forgot to free(ptr);
    // free(ptr);  <-- intentionally missing

    return 0;
}