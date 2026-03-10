#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>

#define NUM_ITER 1000

int counter = 0;//shared memory
sem_t s; //binary semaphore

void* increment(void* arg)
{
    for(int i=0;i<NUM_ITER;i++) 
	{
        sem_wait(&s); //enter critical section -> take a key
        counter++;//perform update
        sem_post(&s); //leave critical section -> return key
    }
    return NULL;
}
int main()
{
    pthread_t t1, t2;
    sem_init(&s, 0, 1); // binary semaphore initialized to 1

    pthread_create(&t1, NULL, increment, NULL);
    pthread_create(&t2, NULL, increment, NULL);

    pthread_join(t1, NULL);
    pthread_join(t2, NULL);

    printf("Final counter: %d\n", counter);
    sem_destroy(&s);
    return 0;
}