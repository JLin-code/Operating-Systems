import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
class StreamEx
{
	/**
	A stream of data is a continuous flow of data elements 
	that are processed as they are generated.
	Lots of data today is a stream. 
	*/
	public static void main(String[] args)
	{
		/*
		filter a list of numbers to get only even numbers:
		*/
		List<Integer> numbers = Arrays.asList(4,1,367,9,0,-12,4214,6,-121,3);
		List<Integer> evenNumbers = numbers.stream()
                                   .filter(n -> n % 2 == 0)
                                   .collect(Collectors.toList());
		System.out.println(evenNumbers);
		
		/*
		Sum
		*/
		int sum = numbers.stream().collect(Collectors.summingInt(Integer::intValue));
        System.out.println("Sum: " + sum);		
	}
}