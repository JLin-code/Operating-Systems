public class MatrixMult_par
{
	public static int[][] matrixMult(int[][] A, int[][] B) throws InterruptedException 
	{
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;

        int[][] ans=new int[rowsA][colsB];

        // Create an array of threads
        Thread[] threads=new Thread[rowsA];

        // Create and start threads
        for (int i=0;i<rowsA;i++)
        {
            final int row=i; // For lambda expression, row must be effectively final
            threads[i]=new Thread(()->{
                for(int j = 0;j<colsB;j++)
                {
                    ans[row][j]=0;
                    for(int k=0;k<colsA;k++)
                    {
                        ans[row][j]+=A[row][k]*B[k][j];
                    }
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for(Thread t:threads)
        {
            t.join();
        }

        return ans;
    }
}
