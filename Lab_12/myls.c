#include <dirent.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#ifndef _WIN32
#include <grp.h>
#include <pwd.h>
#endif

#ifdef _WIN32
#define lstat stat
#endif

#ifndef PATH_MAX
#define PATH_MAX 4096
#endif

static void format_permissions(mode_t mode, char perms[11]) {
    perms[0] = S_ISDIR(mode)
                   ? 'd'
#ifdef S_ISLNK
                   : S_ISLNK(mode) ? 'l'
#endif
                                   : '-';
    perms[1] = (mode & S_IRUSR) ? 'r' : '-';
    perms[2] = (mode & S_IWUSR) ? 'w' : '-';
    perms[3] = (mode & S_IXUSR) ? 'x' : '-';
    perms[4] = (mode & S_IRGRP) ? 'r' : '-';
    perms[5] = (mode & S_IWGRP) ? 'w' : '-';
    perms[6] = (mode & S_IXGRP) ? 'x' : '-';
    perms[7] = (mode & S_IROTH) ? 'r' : '-';
    perms[8] = (mode & S_IWOTH) ? 'w' : '-';
    perms[9] = (mode & S_IXOTH) ? 'x' : '-';
    perms[10] = '\0';
}

static int print_long_entry(const char *dirpath, const char *name) {
    char fullpath[PATH_MAX];
    struct stat st;
    char perms[11];
    const char *owner_name = "unknown";
    const char *group_name = "unknown";

#ifndef _WIN32
    struct passwd *pw;
    struct group *gr;
#endif

    if (snprintf(fullpath, sizeof(fullpath), "%s/%s", dirpath, name) >= (int)sizeof(fullpath)) {
        fprintf(stderr, "Path too long: %s/%s\n", dirpath, name);
        return -1;
    }

    if (lstat(fullpath, &st) != 0) {
        fprintf(stderr, "stat failed for '%s': %s\n", fullpath, strerror(errno));
        return -1;
    }

    format_permissions(st.st_mode, perms);
#ifndef _WIN32
    pw = getpwuid(st.st_uid);
    gr = getgrgid(st.st_gid);
    owner_name = pw ? pw->pw_name : "unknown";
    group_name = gr ? gr->gr_name : "unknown";
#endif

    printf("%s %2lu %-8s %-8s %8lld %s\n",
           perms,
           (unsigned long)st.st_nlink,
           owner_name,
           group_name,
           (long long)st.st_size,
           name);
    return 0;
}

int main(int argc, char *argv[]) {
    int long_format = 0;
    const char *dirpath = NULL;
    DIR *dir;
    struct dirent *entry;
    char cwd[PATH_MAX];

    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-l") == 0) {
            long_format = 1;
        } else if (dirpath == NULL) {
            dirpath = argv[i];
        } else {
            fprintf(stderr, "Usage: %s [-l] [directory]\n", argv[0]);
            return EXIT_FAILURE;
        }
    }

    if (dirpath == NULL) {
        if (getcwd(cwd, sizeof(cwd)) == NULL) {
            fprintf(stderr, "getcwd failed: %s\n", strerror(errno));
            return EXIT_FAILURE;
        }
        dirpath = cwd;
    }

    dir = opendir(dirpath);
    if (dir == NULL) {
        fprintf(stderr, "opendir failed for '%s': %s\n", dirpath, strerror(errno));
        return EXIT_FAILURE;
    }

    while ((entry = readdir(dir)) != NULL) {
        if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) {
            continue;
        }

        if (long_format) {
            (void)print_long_entry(dirpath, entry->d_name);
        } else {
            printf("%s\n", entry->d_name);
        }
    }

    if (closedir(dir) != 0) {
        fprintf(stderr, "closedir failed for '%s': %s\n", dirpath, strerror(errno));
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}
