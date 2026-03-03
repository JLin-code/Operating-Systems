#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

int main() {
    int fd[2]; pipe(fd);
    int numbers[5] = {2,4,6,8,10};

    for(int i=0;i<3;i++){
        pid_t pid = fork();
        if(pid==0){
            // Child process
            close(fd[0]);
            int result = numbers[i] * (i+1);
            write(fd[1], &result, sizeof(result));
            exit(0);
        }
    }

    // Parent process
    close(fd[1]);
    int sum=0, val;
    for(int i=0;i<3;i++){
        read(fd[0], &val, sizeof(val));
        sum += val;
    }
    printf("Total sum from children: %d\n", sum);
    return 0;
}