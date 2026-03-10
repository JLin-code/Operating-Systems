#include <stdio.h>
#include <pthread.h>

int balance = 1000;

void* withdraw(void* arg) {

    int amount = 700;

    if (balance >= amount) {
        balance = balance - amount;
        printf("Withdraw successful. Balance: %d\n", balance);
    }

    return NULL;
}