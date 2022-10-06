import edu.princeton.cs.algs4.StdRandom;

public class QuickSort {
    private static int partition(Comparable[] a, int lo, int hi) {
        int i = lo, j = hi+1;

        while(true) {
            /* *
             * move pointer i to the right as long as the ith
             * element is less than the partition element
             * */
            while (less(a[++i], a[lo])) {
                if (i == hi)
                    break;
            }

            /* *
             * move pointer j to the left as long as the jth
             * element is greater than the partition element
             * */
            while (less(a[lo], a[--j])) {
                if (j == lo)
                    break;
            }

            // if pointers cross, break outer loop
            if (i >= j)
                break;
            exch(a, i, j);
        }
        // put the partition element in its place
        exch(a, lo, j);
        return j;
    }

    private static void exch(Comparable[] a, int i, int j) {
        Comparable temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    private static boolean less(Comparable item, Comparable item1) {
        return item.compareTo(item1) < 0;
    }

    private static void sort(Comparable[] a, int lo, int hi) {
        if (hi <= lo)
            return;

        // partition the array and return the partition point index
        int j = partition(a, lo, hi);

        sort(a, lo, j-1);   // sort left sub-array
        sort(a, j+1, hi);   // sort right sub-array
    }

    public static void sort(Comparable[] a) {
        StdRandom.shuffle(a);
        sort(a, 0, a.length-1);
    }
}
