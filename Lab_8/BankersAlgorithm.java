import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankersAlgorithm {
    public static int[][] computeNeed(int[][] max, int[][] allocation) {
        int n = max.length;
        int m = max[0].length;
        int[][] need = new int[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
        return need;
    }

    public static List<Integer> findSafeSequence(int[] available, int[][] allocation, int[][] need) {
        int n = allocation.length;
        int m = available.length;

        int[] work = Arrays.copyOf(available, m);
        boolean[] finish = new boolean[n];
        List<Integer> sequence = new ArrayList<>();

        boolean madeProgress;
        do {
            madeProgress = false;
            for (int i = 0; i < n; i++) {
                if (!finish[i] && lessThanOrEqual(need[i], work)) {
                    for (int j = 0; j < m; j++) {
                        work[j] += allocation[i][j];
                    }
                    finish[i] = true;
                    sequence.add(i);
                    madeProgress = true;
                }
            }
        } while (madeProgress);

        for (boolean f : finish) {
            if (!f) {
                return null;
            }
        }
        return sequence;
    }

    public static boolean canGrantRequest(
        int process,
        int[] request,
        int[] available,
        int[][] allocation,
        int[][] need
    ) {
        if (!lessThanOrEqual(request, need[process])) {
            return false;
        }
        if (!lessThanOrEqual(request, available)) {
            return false;
        }

        int[] availableCopy = Arrays.copyOf(available, available.length);
        int[][] allocationCopy = deepCopy(allocation);
        int[][] needCopy = deepCopy(need);

        for (int j = 0; j < availableCopy.length; j++) {
            availableCopy[j] -= request[j];
            allocationCopy[process][j] += request[j];
            needCopy[process][j] -= request[j];
        }

        List<Integer> safe = findSafeSequence(availableCopy, allocationCopy, needCopy);
        return safe != null;
    }

    public static int[] computeTotalInstances(int[] available, int[][] allocation) {
        int m = available.length;
        int[] total = Arrays.copyOf(available, m);
        for (int[] row : allocation) {
            for (int j = 0; j < m; j++) {
                total[j] += row[j];
            }
        }
        return total;
    }

    private static boolean lessThanOrEqual(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] > b[i]) {
                return false;
            }
        }
        return true;
    }

    private static int[][] deepCopy(int[][] matrix) {
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }
}
