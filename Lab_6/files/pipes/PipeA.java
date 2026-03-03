import java.io.*;
public class PipeA
{
	public static void main(String[] args)
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
		
	}
}