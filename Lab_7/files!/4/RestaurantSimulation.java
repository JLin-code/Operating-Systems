import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class RestaurantSimulation {
    static class TrayBuffer {
        private final Queue<String> trays = new LinkedList<>();
        private final Semaphore emptySlots;
        private final Semaphore fullSlots;
        private final Semaphore mutex;

        TrayBuffer(int capacity) {
            this.emptySlots = new Semaphore(capacity, true);
            this.fullSlots = new Semaphore(0, true);
            this.mutex = new Semaphore(1, true);
        }

        void produceMeal(String chefName, String meal) throws InterruptedException {
            emptySlots.acquire();
            mutex.acquire();
            try {
                trays.offer(meal);
                System.out.println(chefName + " prepared " + meal + " (trays in use: " + trays.size() + ")");
            } finally {
                mutex.release();
                fullSlots.release();
            }
        }

        String consumeMeal(String waiterName) throws InterruptedException {
            fullSlots.acquire();
            mutex.acquire();
            try {
                String meal = trays.poll();
                System.out.println(waiterName + " served " + meal + " (trays in use: " + trays.size() + ")");
                return meal;
            } finally {
                mutex.release();
                emptySlots.release();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int chefs = 3;
        final int waiters = 2;
        final int mealsPerChef = 5;
        final int totalMeals = chefs * mealsPerChef;
        final int traysCapacity = 4;

        TrayBuffer trayBuffer = new TrayBuffer(traysCapacity);
        Random random = new Random();

        Thread[] chefThreads = new Thread[chefs];
        for (int i = 0; i < chefs; i++) {
            final int chefId = i + 1;
            chefThreads[i] = new Thread(() -> {
                for (int mealNo = 1; mealNo <= mealsPerChef; mealNo++) {
                    String meal = "Meal-C" + chefId + "-" + mealNo;
                    try {
                        Thread.sleep(200 + random.nextInt(300));
                        trayBuffer.produceMeal("Chef-" + chefId, meal);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
            chefThreads[i].start();
        }

        Thread[] waiterThreads = new Thread[waiters];
        for (int i = 0; i < waiters; i++) {
            final int waiterId = i + 1;
            waiterThreads[i] = new Thread(() -> {
                int myServed = 0;
                int target = totalMeals / waiters + (waiterId <= totalMeals % waiters ? 1 : 0);
                while (myServed < target) {
                    try {
                        Thread.sleep(250 + random.nextInt(300));
                        trayBuffer.consumeMeal("Waiter-" + waiterId);
                        myServed++;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
            waiterThreads[i].start();
        }

        for (Thread chef : chefThreads) {
            chef.join();
        }
        for (Thread waiter : waiterThreads) {
            waiter.join();
        }

        System.out.println("Restaurant simulation complete.");
    }
}
