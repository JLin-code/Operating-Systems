#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>

int main(int argc, char *argv[]) {
    struct stat st;

    if (argc != 2) {
        fprintf(stderr, "Usage: %s <path>\n", argv[0]);
        return EXIT_FAILURE;
    }

    if (stat(argv[1], &st) != 0) {
        fprintf(stderr, "stat failed for '%s': %s\n", argv[1], strerror(errno));
        return EXIT_FAILURE;
    }

    printf("Path: %s\n", argv[1]);
    printf("Size: %lld bytes\n", (long long)st.st_size);
#ifdef _WIN32
    printf("Blocks: n/a\n");
#else
    printf("Blocks: %lld\n", (long long)st.st_blocks);
#endif
    printf("Links: %lu\n", (unsigned long)st.st_nlink);

    return EXIT_SUCCESS;
}
