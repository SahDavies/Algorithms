import java.util.ArrayList;

public class SyntheticDivision {
    Integer[] quotient;
    int divisor;

    public SyntheticDivision(DeluxePolynomial p, int d) {
        ArrayList<Integer> quotient = new ArrayList<>();
        int sum = 0;
        for (int i : p.getCoefficient()) {
            sum *= d;
            sum += i;
            quotient.add(sum);
        }
        divisor = d;
        this.quotient = quotient.toArray(new Integer[0]);
    }

    public DeluxePolynomial getQuotient() {
        int[] aux = new int[quotient.length];
        int i = 0;
        for (int n : quotient) {
            aux[i] = n;
            i++;
        }
        DeluxePolynomial p = new DeluxePolynomial(aux);
        return p;
    }
}
