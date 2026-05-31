#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#define BLOCK_SIZE 4096

static void usage(const char *prog) {
    fprintf(stderr, "Usage: %s -n <lines> <file>\n", prog);
}

int main(int argc, char *argv[]) {
    int fd;
    int n;
    const char *path;
    struct stat st;
    off_t file_size;
    off_t pos;
    off_t start = 0;
    char buf[BLOCK_SIZE];
    int newline_count = 0;
    int ignore_trailing_newline = 0;

    if (argc != 4 || strcmp(argv[1], "-n") != 0) {
        usage(argv[0]);
        return EXIT_FAILURE;
    }

    n = atoi(argv[2]);
    if (n <= 0) {
        fprintf(stderr, "Invalid line count: %s\n", argv[2]);
        return EXIT_FAILURE;
    }

    path = argv[3];
    fd = open(path, O_RDONLY);
    if (fd < 0) {
        fprintf(stderr, "open failed for '%s': %s\n", path, strerror(errno));
        return EXIT_FAILURE;
    }

    if (fstat(fd, &st) != 0) {
        fprintf(stderr, "fstat failed for '%s': %s\n", path, strerror(errno));
        close(fd);
        return EXIT_FAILURE;
    }

    file_size = st.st_size;
    if (file_size == 0) {
        close(fd);
        return EXIT_SUCCESS;
    }

    if (lseek(fd, file_size - 1, SEEK_SET) == (off_t)-1) {
        fprintf(stderr, "lseek failed: %s\n", strerror(errno));
        close(fd);
        return EXIT_FAILURE;
    }

    if (read(fd, buf, 1) == 1 && buf[0] == '\n') {
        ignore_trailing_newline = 1;
    }

    pos = file_size;
    while (pos > 0 && newline_count < n) {
        ssize_t to_read;
        ssize_t got;

        to_read = (pos >= BLOCK_SIZE) ? BLOCK_SIZE : (ssize_t)pos;
        pos -= to_read;

        if (lseek(fd, pos, SEEK_SET) == (off_t)-1) {
            fprintf(stderr, "lseek failed: %s\n", strerror(errno));
            close(fd);
            return EXIT_FAILURE;
        }

        got = read(fd, buf, to_read);
        if (got < 0) {
            fprintf(stderr, "read failed: %s\n", strerror(errno));
            close(fd);
            return EXIT_FAILURE;
        }

        for (ssize_t i = got - 1; i >= 0; i--) {
            if (buf[i] != '\n') {
                continue;
            }

            if (ignore_trailing_newline) {
                ignore_trailing_newline = 0;
                continue;
            }

            newline_count++;
            if (newline_count == n) {
                start = pos + i + 1;
                break;
            }
        }
    }

    if (lseek(fd, start, SEEK_SET) == (off_t)-1) {
        fprintf(stderr, "lseek failed: %s\n", strerror(errno));
        close(fd);
        return EXIT_FAILURE;
    }

    while (1) {
        ssize_t got = read(fd, buf, sizeof(buf));
        if (got < 0) {
            fprintf(stderr, "read failed: %s\n", strerror(errno));
            close(fd);
            return EXIT_FAILURE;
        }
        if (got == 0) {
            break;
        }
        if (write(STDOUT_FILENO, buf, (size_t)got) < 0) {
            fprintf(stderr, "write failed: %s\n", strerror(errno));
            close(fd);
            return EXIT_FAILURE;
        }
    }

    close(fd);
    return EXIT_SUCCESS;
}
