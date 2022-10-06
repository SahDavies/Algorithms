
/* *
 * Ascending order sort
 * API:
 * sort(Comparable[] arr), less(Comparable v, Comparable w),
 * exch(Comparable[] arr, int i, int j), isSorted(Comparable[] arr)
 * */

public class Insertion {
    public static void sort(Comparable[] arr) {
        int N = arr.length;

        // loop through the array from left to right
        for (int i = 0; i < N; i++) {

            /* *
             * for each current element, compare with
             * the previous element and exchange when required
             * */
            for (int j = i; j > 0; j--)
                if (less(arr[j], arr[j-1]))
                    exch(arr, j, j-1);
                else
                    break;
        }
    }   // end of sort

    // sort method with a different signature
    public static void sort(Comparable[] arr, int lo, int hi) {
        for (int i = lo; i <= hi; i++) {

            for (int j = i; j > lo; j--)
                if (less(arr[j], arr[j-1]))
                    exch(arr, j, j-1);
                else
                    break;
        }
    }   // end of sort

    // a method that swaps elements in the given array indices
    private static void exch(Comparable[] arr, int i, int j) {
        Comparable temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // a method that compares if one element is less than the other
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    // a method that checks if the array is sorted
    public static boolean isSorted(Comparable[] arr) {
        for (int i = 0; i < arr.length; i++)
            if (less(arr[i], arr[i-1]))
                return false;

        return true;
    }
}
