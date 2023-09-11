import java.util.*;

public class Inversion {
    /**
     * This program computes the inversion of any given permutation.
     * It does so in O(NlogN) time.
     * */
    private final Map<Integer, List<Integer>> inversion;
    private int size;

    // prevent constructor instantiation
    private Inversion (Map<Integer, List<Integer>> inversion, int size) {
        this.inversion = inversion;
        this.size = size;
    }
    public int inversionCount() {
        return size;
    }
    public Map<Integer, List<Integer>> kendallTauDistance(Inversion that) {
        int i = 0, j = 0;       // different pointers to control each key set
        Map<Integer, List<Integer>> accumulator = new TreeMap<>();
        Integer[] theseKeys = this.inversion.keySet().toArray(new Integer[0]);
        Integer[] thoseKeys = that.inversion.keySet().toArray(new Integer[0]);

        boolean neitherPointersExceedRange = true;

        // disjoint of two set algorithm
        while (neitherPointersExceedRange) {

            boolean bothPointersAreInRange = i < theseKeys.length && j < thoseKeys.length;
            if (bothPointersAreInRange) {
                if (theseKeys[i] < thoseKeys[j]) {
                    accumulator.put(theseKeys[i], this.inversion.get(theseKeys[i]));
                    i++;    // update pointer
                } else if (theseKeys[i] > thoseKeys[j]) {
                    accumulator.put(thoseKeys[j], that.inversion.get(thoseKeys[j]));
                    j++;    // update pointer
                } else {
                    List<Integer> theseValues = this.inversion.get(theseKeys[i]);
                    List<Integer> thoseValues = that.inversion.get(thoseKeys[j]);
                    List<Integer> list = disjoint(theseValues, thoseValues);
                    accumulator.put(theseKeys[i], list);
                    i++; j++;   // update pointers
                }
            } else if (i < theseKeys.length) {
                accumulator.put(theseKeys[i], this.inversion.get(theseKeys[i]));
                i++;    // update pointer
            } else {
                accumulator.put(thoseKeys[j], that.inversion.get(thoseKeys[j]));
                j++;    // update pointer
            }
            neitherPointersExceedRange = i < theseKeys.length || j < thoseKeys.length;
        }
        return accumulator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        inversion.forEach((key, values) ->
                values.forEach(val -> sb.append(key)
                                .append("-")
                                .append(val)
                                .append(", ")
        ));
        return sb.toString();
    }

    // uses the index merge sort algorithm to find
    // the inversions of any given permutation
    public static Inversion build(Integer[] a) {
        int n = a.length;
        int[] index = new int[n];
        for (int i = 0; i < n; i++)
            index[i] = i;

        int[] aux = new int[n];
        Map<Integer, List<Integer>> accumulator = new TreeMap<>();
        int size = sort(a, index, aux, 0, n-1, accumulator);
        return new Inversion(accumulator, size);
    }

    // mergesort a[lo..hi] using auxiliary array aux[lo..hi]
    private static int sort(Integer[] a,
                             int[] index, int[] aux,
                             int lo, int hi,
                             Map<Integer, List<Integer>> accumulator
    ) {
        int count = 0;
        if (hi <= lo) return count;
        int mid = lo + (hi - lo) / 2;
        count = sort(a, index, aux, lo, mid, accumulator) + sort(a, index, aux, mid + 1, hi, accumulator);
        return merge(a, index, aux, lo, mid, hi, accumulator) + count;
    }

    private static int merge(
            Integer[] a,
            int[] index, int[] aux,
            int lo, int mid, int hi,
            Map<Integer, List<Integer>> accumulator
    ) {

        // copy to aux[]
        if (hi + 1 - lo >= 0) System.arraycopy(index, lo, aux, lo, hi + 1 - lo);

        // merge back to a[]
        int i = lo, j = mid+1;
        int count = 0;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)   index[k] = aux[j++];
            else if (j > hi)    index[k] = aux[i++];
            else if (less(a[aux[j]], a[aux[i]])) {
                if (!accumulator.containsKey(a[aux[i]])) {
                    accumulator.put(a[aux[i]], new ArrayList<>());
                }
                accumulator.get(a[aux[i]]).add(a[aux[j]]);
                index[k] = aux[j++];
                ++count;
            }
            else index[k] = aux[i++];
        }
        return count;
    }

    private static boolean less(Integer v, Integer w) {
        return v.compareTo(w) < 0;
    }

    private List<Integer> disjoint(List<Integer> theseValues, List<Integer> thoseValues) {
        int m = 0, n = 0;   // array pointers
        List<Integer> list = new ArrayList<>();

        boolean neitherPointersExceedRange = true;

        // disjoint of two set algorithm
        while (neitherPointersExceedRange) {
            boolean bothPointersAreInRange = m < theseValues.size() && n < thoseValues.size();
            if (bothPointersAreInRange) {
                if (theseValues.get(m) < thoseValues.get(n)) {
                    list.add(theseValues.get(m));
                    m++;    // update pointer
                } else if (theseValues.get(m) > thoseValues.get(n)) {
                    list.add(thoseValues.get(n));
                    n++;    // update pointer
                } else {
                    m++; n++;   // update pointers
                }
            } else if (m < theseValues.size()) {
                list.add(theseValues.get(m));
                m++;    // update pointer
            } else {
                list.add(thoseValues.get(n));
                n++;    // update pointer
            }
            neitherPointersExceedRange = m < theseValues.size() || n < thoseValues.size();
        }
        return list;
    }

    public static void printKendallTau(Map<Integer, List<Integer>> map) {
        map.forEach((key, values) ->
                values.forEach(value -> System.out.print(key + "-" + value + ", "))
        );
    }

    public static void main(String[] args) {
        Inversion inversionA = Inversion.build(new Integer[]{5,1,2,0,4,3,6});
        Inversion inversionB = Inversion.build(new Integer[]{1,0,3,6,4,2,5});
        System.out.printf("Permutation_A: %d inversions\n", inversionA.inversionCount());
        System.out.printf("Permutation_B: %d inversions\n", inversionB.inversionCount());
        var map = inversionA.kendallTauDistance(inversionB);
        System.out.printf("Permutation_A: %s\n", inversionA);
        System.out.printf("Permutation_B: %s\n", inversionB);
        System.out.print("KendalTau Distance: ");
        Inversion.printKendallTau(map);
    }
}
