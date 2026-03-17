import java.util.Random;
import java.util.concurrent.Semaphore;

public class ParkingReservationSimulation {
    static class ParkingLot {
        private final Semaphore spots;

        ParkingLot(int totalSpots) {
            this.spots = new Semaphore(totalSpots, true);
        }

        void park(String carName) throws InterruptedException {
            System.out.println(carName + " arrives and looks for a spot.");
            spots.acquire();
            System.out.println(carName + " parked. Spots left: " + spots.availablePermits());
        }

        void leave(String carName) {
            spots.release();
            System.out.println(carName + " left. Spots left: " + spots.availablePermits());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int totalSpots = 3;
        final int totalCars = 8;
        ParkingLot parkingLot = new ParkingLot(totalSpots);
        Random random = new Random();

        Thread[] cars = new Thread[totalCars];
        for (int i = 0; i < totalCars; i++) {
            final String carName = "Car-" + (i + 1);
            cars[i] = new Thread(() -> {
                try {
                    parkingLot.park(carName);
                    Thread.sleep(600 + random.nextInt(700));
                    parkingLot.leave(carName);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            cars[i].start();
        }

        for (Thread car : cars) {
            car.join();
        }

        System.out.println("Parking simulation complete.");
    }
}
