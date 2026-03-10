import java.io.*;
import java.util.Random;
public class PipeB
{
	/**
		Pipes can be chained together in a pipeline
		
		pipe 1 -> pipe 2 -> pipe 3 -> ... -> pipe n
		
		Note: This is very much like using streams with functional programming
		in Java 
		
		Goal: 
			1. Create a producer that writes lines of number data
			2. Create a filter that will apply some sort of filter to only
			   send some data foward. (ex. evens)
			3. Create a map that will apply some function 
			   to each data that it recieves. (ex. compute x*x for each data)
			4. Create a consumer that will read and write the final data
			
			one flow example: 
			producer generates 5 random values from [0,100]
			5, 10, 54, 87, 3
			filter keeps only evens
			10, 54
			map applys x*x to each 
			100, 2916
			consumer prints data it receives
			100, 2916
			
			Note: output of one is fed as input to next
	*/
	public static void main(String[] args) throws Exception
	{
		PipedOutputStream producerToFilterOut = new PipedOutputStream();
		PipedInputStream producerToFilterIn = new PipedInputStream(producerToFilterOut);

		PipedOutputStream filterToMapOut = new PipedOutputStream();
		PipedInputStream filterToMapIn = new PipedInputStream(filterToMapOut);

		PipedOutputStream mapToConsumerOut = new PipedOutputStream();
		PipedInputStream mapToConsumerIn = new PipedInputStream(mapToConsumerOut);

		Thread producer = new Thread(() -> {
			Random random = new Random();
			try (PrintWriter out = new PrintWriter(producerToFilterOut, true)) {
				for (int i = 0; i < 10; i++) {
					int value = random.nextInt(101);
					System.out.println("[Producer] " + value);
					out.println(value);
				}
			}
		});

		Thread filter = new Thread(() -> {
			try (
				BufferedReader in = new BufferedReader(new InputStreamReader(producerToFilterIn));
				PrintWriter out = new PrintWriter(filterToMapOut, true)
			) {
				String line;
				while ((line = in.readLine()) != null) {
					int value = Integer.parseInt(line.trim());
					if (value % 2 == 0) {
						System.out.println("[Filter-even] " + value);
						out.println(value);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread map = new Thread(() -> {
			try (
				BufferedReader in = new BufferedReader(new InputStreamReader(filterToMapIn));
				PrintWriter out = new PrintWriter(mapToConsumerOut, true)
			) {
				String line;
				while ((line = in.readLine()) != null) {
					int value = Integer.parseInt(line.trim());
					int squared = value * value;
					System.out.println("[Map x*x] " + value + " -> " + squared);
					out.println(squared);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread consumer = new Thread(() -> {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(mapToConsumerIn))) {
				String line;
				while ((line = in.readLine()) != null) {
					System.out.println("[Consumer] " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		consumer.start();
		map.start();
		filter.start();
		producer.start();

		producer.join();
		filter.join();
		map.join();
		consumer.join();
	}
}