import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeadlockDetection {
    public static class DetectionResult {
        public final List<Integer> finishSequence;
        public final List<Integer> deadlockedProcesses;

        public DetectionResult(List<Integer> finishSequence, List<Integer> deadlockedProcesses) {
            this.finishSequence = finishSequence;
            this.deadlockedProcesses = deadlockedProcesses;
        }
    }

    public static DetectionResult detect(int[] available, int[][] allocation, int[][] request) {
        int n = allocation.length;
        int m = available.length;

        int[] work = Arrays.copyOf(available, m);
        boolean[] finish = new boolean[n];
        List<Integer> sequence = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            finish[i] = isZeroRow(allocation[i]);
            if (finish[i]) {
                sequence.add(i);
            }
        }

        boolean madeProgress;
        do {
            madeProgress = false;
            for (int i = 0; i < n; i++) {
                if (!finish[i] && lessThanOrEqual(request[i], work)) {
                    for (int j = 0; j < m; j++) {
                        work[j] += allocation[i][j];
                    }
                    finish[i] = true;
                    sequence.add(i);
                    madeProgress = true;
                }
            }
        } while (madeProgress);

        List<Integer> deadlocked = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!finish[i]) {
                deadlocked.add(i);
            }
        }

        return new DetectionResult(sequence, deadlocked);
    }

    private static boolean lessThanOrEqual(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] > b[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isZeroRow(int[] row) {
        for (int v : row) {
            if (v != 0) {
                return false;
            }
        }
        return true;
    }
}
