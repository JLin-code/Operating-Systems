public class Processors 
{
	public static void main(String[] args) 
	{
		int cores = Runtime.getRuntime().availableProcessors();
		//retrieves number of cpu cores available to the JVM
	    System.out.println("Number of available processor cores: " + cores);
	}
}
