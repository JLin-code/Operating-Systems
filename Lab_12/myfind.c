#include <dirent.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#ifdef _WIN32
#define lstat stat
#endif

#ifndef PATH_MAX
#define PATH_MAX 4096
#endif

typedef struct {
    const char *name_filter;
} options_t;
static void usage(const char *prog) {
    fprintf(stderr,
            "Usage: %s [start_dir] [-name <pattern>]\n",
            prog);
}
static int name_matches(const char *name, const char *pattern) {
    if (pattern == NULL) {
        return 1;
    }
    return strstr(name, pattern) != NULL;
}
static int walk(const char *path, const options_t *opts) {
    struct stat st;

    if (lstat(path, &st) != 0) {
        fprintf(stderr, "lstat failed for '%s': %s\n", path, strerror(errno));
        return -1;
    }
    const char *base = strrchr(path, '/');
    base = (base == NULL) ? path : base + 1;

    if (name_matches(base, opts->name_filter)) {
        printf("%s\n", path);
    }
    if (!S_ISDIR(st.st_mode)) {
        return 0;
    }
    DIR *dir = opendir(path);
    if (dir == NULL) {
        fprintf(stderr, "opendir failed for '%s': %s\n", path, strerror(errno));
        return -1;
    }
    struct dirent *entry;
    while ((entry = readdir(dir)) != NULL) {
        char child[PATH_MAX];
        if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) {
            continue;
        }
        if (snprintf(child, sizeof(child), "%s/%s", path, entry->d_name) >= (int)sizeof(child)) {
            fprintf(stderr, "Path too long: %s/%s\n", path, entry->d_name);
            continue;
        }
        (void)walk(child, opts);
    }
    if (closedir(dir) != 0) {
        fprintf(stderr, "closedir failed for '%s': %s\n", path, strerror(errno));
        return -1;
    }
    return 0;
}
int main(int argc, char *argv[]) {
    options_t opts = {NULL};
    const char *start_dir = ".";

    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-name") == 0) {
            if (i + 1 >= argc) {
                usage(argv[0]);
                return EXIT_FAILURE;
            }
            opts.name_filter = argv[++i];
        } else if (argv[i][0] == '-') {
            usage(argv[0]);
            return EXIT_FAILURE;
        } else {
            start_dir = argv[i];
        }
    }

    if (walk(start_dir, &opts) != 0) {
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}
