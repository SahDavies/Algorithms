import edu.princeton.cs.algs4.StdIn;

public class UnitTest {
    public static void toStringUnitTest(int[] coefficients) {
        // object's toString unit test
        DeluxePolynomial p = new DeluxePolynomial(coefficients);
        System.out.println(p);
    }
    public static void isOddUnitTest() {
        // isOdd unit test
        int oddCount = 0;
        int constant = 1_000_000;
        for (int i = 0; i < constant; i++) {
            if (DeluxePolynomial.isOdd(i)) oddCount++;
        }
        System.out.print("isOdd unit test result: ");
        System.out.println(oddCount == (constant / 2));
    }
    public static void signChangeUnitTest(int[] coefficients) {
        int val = DeluxePolynomial.signChange(coefficients);
        // multiply all coefficients
        int i = 1;
        for (int k : coefficients) {
            i *= k;
        }
        System.out.print("signChange unit test result: ");
        // an if then statement; if val is odd, then the product of all coefficient is a negative number,
        // if val is even, then the product of all coefficient is a positive number
        // a true output proves the method correct
        if (DeluxePolynomial.isOdd(val))
            System.out.println(i < 0);
        if (!DeluxePolynomial.isOdd(val))
            System.out.println(i > 0);
    }
}
