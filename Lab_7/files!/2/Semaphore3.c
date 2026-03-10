#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>

//limits the size of the buffer
#define BUFFER_SIZE 5
#define NUM_ITEMS 10

int buffer[BUFFER_SIZE];
int in = 0, out = 0;

sem_t empty; //counts empty slots ->counting semaphore
sem_t full;  //counts full slots -> counting semaphore
sem_t mutex; //binary semaphore for lock/mutex

void* producer(void* arg) 
{
    for(int i=0;i<NUM_ITEMS;i++) 
	{
        sem_wait(&empty);//get a key/space in the buffer
        sem_wait(&mutex);//lock the shared data area

        buffer[in] = i;//produce
        printf("Produced: %d\n", i);
        in = (in + 1) % BUFFER_SIZE;

        sem_post(&mutex);//give back the key for buffer space/ unlock
        sem_post(&full);//buffer has something
        usleep(10000);
    }
    return NULL;
}
void* consumer(void* arg)
 {
    for(int i=0;i<NUM_ITEMS;i++) 
	{
        sem_wait(&full);//take key if buffer has something
        sem_wait(&mutex);//lock the shared data

        int item = buffer[out];//consume
        printf("Consumed: %d\n", item);
        out = (out + 1) % BUFFER_SIZE;

        sem_post(&mutex);//unlcok
        sem_post(&empty);//one less thing in buffer
        usleep(15000);
    }
    return NULL;
}

int main() 
{
    pthread_t prod, cons;
    sem_init(&empty, 0, BUFFER_SIZE);
    sem_init(&full, 0, 0);
    sem_init(&mutex, 0, 1);

    pthread_create(&prod, NULL, producer, NULL);
    pthread_create(&cons, NULL, consumer, NULL);

    pthread_join(prod, NULL);
    pthread_join(cons, NULL);

    sem_destroy(&empty);
    sem_destroy(&full);
    sem_destroy(&mutex);

    return 0;
}