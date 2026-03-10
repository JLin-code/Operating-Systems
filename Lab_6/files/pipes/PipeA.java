import java.io.*;
public class PipeA
{
	public static void main(String[] args) throws Exception
	{
		/*
			Byte-oriented:
				-most common for binary/general data
		*/
		PipedOutputStream pout=new PipedOutputStream();
		PipedInputStream pin=new PipedInputStream(pout); 
		// can also use pin.connect(pout);

		/*
			Character-oriented:
				-good when you're dealing with text/Readers/Writers
		*/
		PipedWriter pw=new PipedWriter();
		PipedReader pr=new PipedReader(pw);
		
		/**
			Goal: create a Hello World! pipe
			Try to create using a byte approach:
				Solution 1: Use PipedOutputStream, PipedInputStream, BufferedReader
			
			Try to create using a Character approach:
				Solution 2: Use PipedWriter, PipedReader
				
			Hint:
			Producer thread writes "Hello World!"
			Consumer thread reads everything and prints it	
		*/
		Thread byteProducer = new Thread(() -> {
			try (PipedOutputStream out = pout) {
				out.write("Hello World!\n".getBytes());
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread byteConsumer = new Thread(() -> {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(pin))) {
				String line;
				while ((line = in.readLine()) != null) {
					System.out.println("[Byte Pipe] " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread charProducer = new Thread(() -> {
			try (PipedWriter out = pw) {
				out.write("Hello World!\n");
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread charConsumer = new Thread(() -> {
			try (BufferedReader in = new BufferedReader(pr)) {
				String line;
				while ((line = in.readLine()) != null) {
					System.out.println("[Char Pipe] " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		byteConsumer.start();
		byteProducer.start();
		charConsumer.start();
		charProducer.start();

		byteProducer.join();
		byteConsumer.join();
		charProducer.join();
		charConsumer.join();
	}
}