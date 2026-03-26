#include <stdio.h>
#include <stdlib.h>

int main() {
    int *data;

    // Allocate array of 100 integers
    data = (int *) malloc(100 * sizeof(int));

    if (data == NULL) {
        printf("Memory allocation failed\n");
        return 1;
    }

    printf("Allocated 100 integers.\n");

    //Out-of-bounds write
    printf("Setting data[100] = 0 (out-of-bounds)\n");
    data[100] = 0; // invalid index

    printf("Done\n");

    // Normally we should free
    free(data);
	
	//question 6
	//printf("data[10] after free: %d\n", data[10]);
	

    return 0;
}