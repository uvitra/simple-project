//Util2
import java.util.Arrays;

public class Util {
    
	/**
     * Multiplies any number of integers
	 *
     * @param numbers numbers to multiply
     * @return int Result of multiplying all elements of numbers
     */
    public static int multiply(int... numbers) {
        return Arrays.stream(numbers).reduce(1,(result,n) -> result*n);
    }
}
