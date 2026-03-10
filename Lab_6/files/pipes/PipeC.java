/*
	This is a great way to practice:

Efficient reading of real files (buffered, chunked)
Piped streams for inter-thread handoff
Measuring throughput realistically
Handling large data without loading everything into memory
*/
import java.io.*;
import java.util.Random;

public class PipeC {
	public static void main(String[] args) throws Exception {
		int totalSizeMB = (args.length > 0) ? Integer.parseInt(args[0]) : 64;
		int bufferSize = (args.length > 1) ? Integer.parseInt(args[1]) : 8192;

		long totalBytes = totalSizeMB * 1024L * 1024L;

		PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream(out, Math.max(bufferSize, 1024));

		Thread producer = new Thread(() -> {
			Random random = new Random();
			byte[] buffer = new byte[bufferSize];
			long written = 0;

			try (BufferedOutputStream bos = new BufferedOutputStream(out, bufferSize)) {
				while (written < totalBytes) {
					int chunk = (int) Math.min(buffer.length, totalBytes - written);
					random.nextBytes(buffer);
					bos.write(buffer, 0, chunk);
					written += chunk;
				}
				bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "producer");

		final long[] readBytes = {0};
		Thread consumer = new Thread(() -> {
			byte[] buffer = new byte[bufferSize];
			long totalRead = 0;

			try (BufferedInputStream bis = new BufferedInputStream(in, bufferSize)) {
				int n;
				while ((n = bis.read(buffer)) != -1) {
					totalRead += n;
				}
				readBytes[0] = totalRead;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "consumer");

		long start = System.nanoTime();
		consumer.start();
		producer.start();

		producer.join();
		consumer.join();
		long end = System.nanoTime();

		double seconds = (end - start) / 1_000_000_000.0;
		double mbTransferred = readBytes[0] / (1024.0 * 1024.0);
		double throughput = mbTransferred / seconds;

		System.out.printf("Transferred: %.2f MB%n", mbTransferred);
		System.out.printf("Buffer size: %d bytes%n", bufferSize);
		System.out.printf("Elapsed: %.3f s%n", seconds);
		System.out.printf("Approx throughput: %.2f MB/s%n", throughput);
	}
}