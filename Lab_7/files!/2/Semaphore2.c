#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h> 

// number of keys/rooms
#define KEYS 3      
#define NUM_PEOPLE 7

// counting semaphore for keys
sem_t keysDesk;

typedef struct 
{
    char* name;
} Person;

void* person_thread(void* arg)
 {
    Person* p = (Person*)arg;

    printf("%s wants a room key.\n", p->name);
    sem_wait(&keysDesk); // take a key
    printf("%s got a key and entered a room.\n", p->name);

    sleep(2); // simulate time in room

    printf("%s is leaving room and returning key.\n", p->name);
    sem_post(&keysDesk); // return key

    return NULL;
}
int main()
{
    char* peopleNames[NUM_PEOPLE] = {"Alice", "Bob", "Charlie", "David", "Eve", "Faye", "Greg"};
    pthread_t threads[NUM_PEOPLE];
    Person people[NUM_PEOPLE];

    // initialize counting semaphore with KEYS resources
    sem_init(&keysDesk, 0, KEYS);

    for(int i = 0; i < NUM_PEOPLE; i++) {
        people[i].name = peopleNames[i];
        pthread_create(&threads[i], NULL, person_thread, &people[i]);
    }

    for(int i = 0; i < NUM_PEOPLE; i++) {
        pthread_join(threads[i], NULL);
    }

    sem_destroy(&keysDesk);

    return 0;
}