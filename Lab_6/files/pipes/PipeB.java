import java.io.*;
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
}