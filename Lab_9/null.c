#include <stdio.h>

int main() {
    int *ptr = NULL;

    printf("Before crash\n");
    fflush(stdout);   

    printf("%d\n", *ptr);  // crash here -> segmentation fault

    printf("After crash\n");  // never reached
    return 0;
}