/** This class is what i would describe as a data-oriented type
 * for representing polynomials. The class is intended for use
 * by a polynomial solver class to find the root of a polynomial.
 *
 * Limitations of the class:
 * 1. The class represents a polynomial of a single variable
 * 2. The coefficients of the polynomial are of integer type
 *    it does not cover polynomials of real coefficients
 *
 * Use case for the class:
 * 1. */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Polynomial;
import edu.princeton.cs.algs4.StdIn;

import java.util.ArrayList;

public class DeluxePolynomial{
    private final int[] coef;
    private final Polynomial p;
    private final int degree;

    // the coefficient supplied as argument is expected
    // to be in order of descending polynomial degree
    public DeluxePolynomial(int... coefficients) {
        int i = 0;
        while (coefficients[i] == 0) { i++; }       // skip index i to the first non-zero coefficient

        final int size = coefficients.length - i;
        coef = new int[size];
        degree = size - 1;

        // make the array named "coef", degree addressable
        // i.e. coef[2] gives the coefficient of x^2 term
        for (int k = degree; i < coefficients.length; i++, k--) {
            coef[k] = coefficients[i];
        }

        int pow = 0;
        Polynomial p = new Polynomial(0,0);

        // constructs a monomial and then
        // add each monomial to form a polynomial
        for (int a : coef) {
            // skip every term with zero coefficient
            if (a == 0) {
                pow++;
                continue;
            }
            p = p.plus(new Polynomial(a, pow++));
        }
        this.p = p;
    }

    public int negativeRationalZeros() {
        int [] negativeOfX = new int[coef.length];
        for (int i = degree; i > 0; i--) {
            if (isOdd(i))      negativeOfX[i] = coef[i] * -1;
            else            negativeOfX[i] = coef[i];
        }
        int val = signChange(negativeOfX);
        return val;
    }

    public int positiveRationalZeros() {
        int val = signChange(coef);
        return val;
    }

    public static boolean isOdd(int n) {
        return (n & 1) != 0;
    }

    public static int signChange(int[] coefficients) {
        int count = 0;
        int i = 0;
        for (int j : coefficients) {
            if (j == 0) continue;   // skip terms with zero coefficients
            if (i * j < 0) count++;
            i = j;
        }
        return count;
    }

    public int getDegree() {
        return degree;
    }

    public Iterable<Integer> getCoefficient() {
        ArrayList<Integer> list = new ArrayList<>();

        // copy elements of coef to list in reverse order
        for (int i = 0, k = coef.length - 1; i < coef.length; i++, k--) {
            list.add(k, coef[i]);
        }
        return null;
    }

    @Override
    public String toString() {
        return p.toString();
    }

    public static void main(String[] args) {
        In input = new In("PolynomialTestFile.txt");
        while (input.hasNextLine()) {
            String[] token = input.readLine().split(" ");
            int[] poly = new int[token.length];
            for (int i = 0; i < token.length; i++) {
                poly[i] = Integer.parseInt(token[i]);
            }
            DeluxePolynomial p = new DeluxePolynomial(poly);
            System.out.print(signChange(poly) + " ");
            System.out.println(p.negativeRationalZeros());
        }
    }
}

