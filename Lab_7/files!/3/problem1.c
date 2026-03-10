#include <stdio.h>
#include <pthread.h>

int tickets = 10;

void* buy_ticket(void* arg) {

    if (tickets > 0) {
        tickets--;
        printf("Ticket purchased. Remaining: %d\n", tickets);
    }

    return NULL;
}

int main() {

    pthread_t t1, t2;

    pthread_create(&t1, NULL, buy_ticket, NULL);
    pthread_create(&t2, NULL, buy_ticket, NULL);

    pthread_join(t1, NULL);
    pthread_join(t2, NULL);

    printf("Final tickets: %d\n", tickets);

}