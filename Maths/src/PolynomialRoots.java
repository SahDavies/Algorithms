import edu.princeton.cs.algs4.StdIn;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;

public class PolynomialRoots {
    private final DeluxePolynomial p;

    public PolynomialRoots(DeluxePolynomial p) {
        this.p = p;
    }

    public double[] solve() {
        if (p.getDegree() < 0)
            throw new UnsupportedOperationException("The polynomial is a zero polynomial");

        // solver for quadratics
        if (p.getDegree() == 2)
            return useFormula();

        return null;
    }

    // the quadratic formula
    private double[] useFormula() {
        int a, b, c;
        Iterator<Integer> i = p.getCoefficient().iterator();

        a = i.next();
        b = i.next();
        c = i.next();

        double bSqr = Math.pow(b, 2.0);
        double discriminant = bSqr - (4*a*c);
        double x1 = (-b + Math.sqrt(discriminant)) / (2*a);
        double x2 = (-b - Math.sqrt(discriminant)) / (2*a);

        return new double[]{x1, x2};
    }

    public static void main(String[] args) {
        int[] input;
        System.out.println("Enter the coefficient of the polynomial "
                + "in descending order of the degree (ctrl D to submit): ");

        // store all inputs in an array
        input = StdIn.readAllInts();

        DeluxePolynomial p = new DeluxePolynomial(input);
        System.out.println("f(x) = " + p);

        PolynomialRoots roots = new PolynomialRoots(p);
        double[] solution = roots.solve();
        for (double s :
                solution) {
            System.out.print(s + " ");
        }
    }
}
