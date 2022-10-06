public class HeapSort {
    // move node k to its place down the tree
    private static void sink(Comparable[] a, int k, int N) {

        while (2*k <= N) {
            int j = 2*k;

            // let j be the index of the larger child element of node k
            if (j < N && less(a, j, j+1))
                j++;
            // end loop if node k is greater than child node, j
            if (!less(a, k, j))
                break;
            exch(a, k, j);
            k = j;
        }
    }

    private static void exch(Comparable[] a, int i, int j) {
        // convert to zero based index by shifting one place to the left
        int lShift = -1;
        Comparable temp = a[i + lShift];

        a[i + lShift] = a[j + lShift];
        a[j + lShift] = temp;
    }

    private static boolean less(Comparable[] a, int i, int j) {
        // convert to zero based index by shifting one place to the left
        int lShift = -1;
        return a[i+lShift].compareTo(a[j+lShift]) < 0;
    }

    public static void sort(Comparable[] a) {
        int N = a.length;
        // build heap using bottom-up method
        for (int k = N/2; k >= 1; k--)
            sink(a, k, N);

        // sort heap by exchanging the root node with the bottom leaf node
        while (N > 1) {
            exch(a, 1, N);
            sink(a, 1, --N);
        }
    }
}
