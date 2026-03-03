import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.IOException;

public class CharacterPipeExample
{
    public static void main(String[] args)
	{
        try 
		{
            // Creates an unconnected PipedWriter
			//Note: must later be connected to a reader
            PipedWriter writer1=new PipedWriter();
            
			PipedReader reader1=new PipedReader();
            // Connect the writer to the reader
            writer1.connect(reader1);
			
            // Create connected PipedWriter
            PipedReader reader2=new PipedReader();
            PipedWriter writer2=new PipedWriter(reader2);
                                    
			// Write some data
            writer1.write("Hello from PipedWriter".toCharArray());
            
            // Read the data
            char[] buffer=new char[100];
            int bytesRead=reader1.read(buffer);
            System.out.println("Read from PipedReader: "+new String(buffer,0,bytesRead));						
									
			writer1.close();
            writer2.close();
            reader1.close();
			reader2.close();
        }catch(IOException e) 
		{
            e.printStackTrace();
        }
    }
}
