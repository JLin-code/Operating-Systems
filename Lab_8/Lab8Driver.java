import java.util.List;

public class Lab8Driver {
    public static void main(String[] args) {
        runBankerCases();
        runDetectionCases();
        runSlideExtraCases();
    }

    private static void runBankerCases() {
        System.out.println("=== BANKER CASES (Lab 8) ===");

        // 1a
        int[] av1a = {3, 1, 2};
        int[][] alloc1a = {
            {1, 0, 1},
            {2, 1, 0},
            {1, 1, 1},
            {0, 0, 2}
        };
        int[][] max1a = {
            {3, 2, 2},
            {2, 2, 2},
            {4, 2, 2},
            {2, 1, 3}
        };
        solveBankerCase("1a", av1a, alloc1a, max1a, null, -1);

        // 1b
        int[] av1b = {2, 1, 0};
        int[][] alloc1b = {
            {1, 0, 0},
            {1, 1, 0},
            {1, 0, 1},
            {0, 1, 1}
        };
        int[][] max1b = {
            {3, 2, 2},
            {1, 2, 1},
            {3, 1, 2},
            {1, 1, 2}
        };
        int[] req1b = {1, 0, 1};
        solveBankerCase("1b", av1b, alloc1b, max1b, req1b, 2);

        // 1c
        int[] av1c = {3, 3, 2};
        int[][] alloc1c = {
            {0, 1, 0},
            {2, 0, 0},
            {3, 0, 2},
            {2, 1, 1},
            {0, 0, 2}
        };
        int[][] max1c = {
            {7, 5, 3},
            {3, 2, 2},
            {9, 0, 2},
            {2, 2, 2},
            {4, 3, 3}
        };
        int[] req1c = {1, 0, 2};
        solveBankerCase("1c", av1c, alloc1c, max1c, req1c, 1);

        // 1d
        int[] av1d = {1, 1, 1};
        int[][] alloc1d = {
            {0, 1, 0},
            {2, 0, 0},
            {3, 0, 3},
            {2, 1, 1},
            {0, 0, 2}
        };
        int[][] max1d = {
            {7, 5, 3},
            {3, 2, 2},
            {9, 0, 4},
            {2, 2, 2},
            {4, 3, 3}
        };
        solveBankerCase("1d", av1d, alloc1d, max1d, null, -1);
    }

    private static void solveBankerCase(
        String label,
        int[] available,
        int[][] allocation,
        int[][] max,
        int[] request,
        int requestProcess
    ) {
        int[][] need = BankersAlgorithm.computeNeed(max, allocation);
        List<Integer> safe = BankersAlgorithm.findSafeSequence(available, allocation, need);

        System.out.println("\nCase " + label);
        printMatrix("Need", need);

        if (label.equals("1b")) {
            int[] total = BankersAlgorithm.computeTotalInstances(available, allocation);
            System.out.println("Total instances: " + vec(total));
        }

        if (request != null) {
            boolean canGrant = BankersAlgorithm.canGrantRequest(
                requestProcess,
                request,
                available,
                allocation,
                need
            );
            System.out.println("Request T" + requestProcess + " " + vec(request) + " granted? " + canGrant);
        }

        if (safe == null) {
            System.out.println("Safe sequence: NONE (unsafe state)");
        } else {
            System.out.println("One safe sequence: " + seq(safe));
        }
    }

    private static void runDetectionCases() {
        System.out.println("\n=== DETECTION CASES (Lab 8) ===");

        // 2a
        solveDetectionCase(
            "2a",
            new int[] {1, 2, 1},
            new int[][] {
                {1, 0, 0},
                {0, 1, 1},
                {1, 1, 0},
                {0, 0, 1}
            },
            new int[][] {
                {0, 1, 0},
                {1, 0, 0},
                {1, 0, 1},
                {0, 0, 1}
            }
        );

        // 2b
        solveDetectionCase(
            "2b",
            new int[] {0, 1, 1},
            new int[][] {
                {0, 1, 0},
                {2, 0, 1},
                {1, 1, 0},
                {0, 0, 2}
            },
            new int[][] {
                {0, 0, 1},
                {1, 0, 0},
                {0, 1, 1},
                {0, 0, 1}
            }
        );

        // 2c
        solveDetectionCase(
            "2c",
            new int[] {1, 0, 1},
            new int[][] {
                {0, 1, 0},
                {1, 0, 1},
                {1, 1, 0},
                {0, 0, 2},
                {1, 0, 0}
            },
            new int[][] {
                {0, 0, 1},
                {0, 1, 0},
                {1, 0, 0},
                {0, 0, 1},
                {0, 1, 1}
            }
        );

        // 2d
        solveDetectionCase(
            "2d",
            new int[] {0, 0, 0},
            new int[][] {
                {0, 1, 0},
                {2, 0, 0},
                {3, 0, 3},
                {2, 1, 1},
                {0, 0, 2}
            },
            new int[][] {
                {0, 0, 0},
                {2, 0, 2},
                {0, 0, 1},
                {1, 0, 0},
                {0, 0, 2}
            }
        );

        // 2e
        solveDetectionCase(
            "2e",
            new int[] {1, 0, 0},
            new int[][] {
                {1, 0, 0},
                {0, 1, 0},
                {1, 1, 0},
                {0, 0, 1}
            },
            new int[][] {
                {0, 0, 0},
                {0, 0, 2},
                {1, 0, 0},
                {0, 2, 0}
            }
        );
    }

    private static void solveDetectionCase(String label, int[] available, int[][] allocation, int[][] request) {
        DeadlockDetection.DetectionResult result = DeadlockDetection.detect(available, allocation, request);
        System.out.println("\nCase " + label);
        System.out.println("Finish sequence: " + seq(result.finishSequence));
        if (result.deadlockedProcesses.isEmpty()) {
            System.out.println("Deadlocked: NONE");
        } else {
            System.out.println("Deadlocked: " + seq(result.deadlockedProcesses));
        }
    }

    private static void runSlideExtraCases() {
        System.out.println("\n=== EXTRA BANKER SLIDE REQUESTS ===");

        int[] available = {3, 3, 2};
        int[][] allocation = {
            {0, 1, 0},
            {2, 0, 0},
            {3, 0, 2},
            {2, 1, 1},
            {0, 0, 2}
        };
        int[][] max = {
            {7, 5, 3},
            {3, 2, 2},
            {9, 0, 2},
            {2, 2, 2},
            {4, 3, 3}
        };

        int[][] need = BankersAlgorithm.computeNeed(max, allocation);

        boolean t4req = BankersAlgorithm.canGrantRequest(4, new int[] {3, 3, 0}, available, allocation, need);
        boolean t0req = BankersAlgorithm.canGrantRequest(0, new int[] {0, 2, 0}, available, allocation, need);

        System.out.println("T4 request (3,3,0) granted? " + t4req);
        System.out.println("T0 request (0,2,0) granted? " + t0req);
    }

    private static void printMatrix(String name, int[][] matrix) {
        System.out.println(name + ":");
        for (int i = 0; i < matrix.length; i++) {
            System.out.println("T" + i + " " + vec(matrix[i]));
        }
    }

    private static String vec(int[] v) {
        return "(" + v[0] + "," + v[1] + "," + v[2] + ")";
    }

    private static String seq(List<Integer> s) {
        StringBuilder sb = new StringBuilder("<");
        for (int i = 0; i < s.size(); i++) {
            sb.append("T").append(s.get(i));
            if (i < s.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(">\n");
        return sb.toString();
    }
}
