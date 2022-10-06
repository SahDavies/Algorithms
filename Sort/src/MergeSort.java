public class MergeSort {
    private static final int CUTOFF = 7;

    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
        assert isSorted(a, lo, mid);    // precondition: a[lo...mid] sorted
        assert isSorted(a, mid+1, hi);  // precondition: a[mid+1...hi] sorted

        // copy elements in 'a' to 'aux'
        if (hi - lo >= 0) {
            System.arraycopy(a, lo, aux, lo, hi - lo);
        }

        // merge sub-arrays a[lo...mid] and a[mid+1...hi]
        int i = lo, j = mid+1;
        for (int k = lo; k < hi; k++) {
            if (i > mid)
                a[k] = aux[j++];
            else if (j > hi)
                a[k] = aux[i++];
            else if (less(aux[j], aux[i]))
                a[k] = a[j++];
            else
                a[k] = aux[i++];
        }
        assert isSorted(a, lo, hi);
    }

    private static boolean less(Comparable item, Comparable item1) {
        return item.compareTo(item1) < 0;
    }

    private static boolean isSorted(Comparable[] a, int lo, int hi) {
        while (lo < hi) {
            if (less(a[lo+1], a[lo]))
                return false;
            lo++;
        }
        return true;
    }

    // helper sort method
    private static void sort(Comparable[] a, Comparable[] aux, int lo, int hi) {
        /* *
        * Switch to insertion sort on
        * smaller sub-arrays to save memory
        * */
        if (hi <= (lo + CUTOFF - 1)) {
            Insertion.sort(a, lo, hi);
            return;
        }

        int mid = lo + (hi-lo) / 2;
        sort(a, aux, lo, mid);          // sort the left half
        sort(a, aux, mid+1, hi);    // sort the right half

        /* *
         * compare the first item of right sub-array
         * to last item of the left sub-array.
         * Skip merge if already in order.
         * */
        if (!less(a[mid+1], a[mid]))
            return;
        merge(a, aux, lo, mid, hi);
    }

    public static void sort(Comparable[] a) {
        Comparable[] aux = new Comparable[a.length];
        sort(a, aux, 0, a.length-1);
    }
}