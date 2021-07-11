import static org.junit.Assert.*;

import org.junit.Test;


public class DieTestRoutine {

	@Test
	public void averageTest() {


		Die testDie = new Die(50,50);
		
		int numTests = 10000;
		double	sum = 0;
		
		for (int i=0; i<numTests; i++) {
			sum += testDie.rollDice();
		}
		
		double average = sum/numTests;
		double tolerance = 1.0;
		
//		System.out.println("Sum: " + sum);
//		System.out.println("Number of Tets: " + numTests);
//		System.out.println("Average: " + average);
		assertEquals("Average is not right", 3.0, average, tolerance);
		
		
	}

}
