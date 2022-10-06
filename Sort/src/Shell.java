
/* *
 * Ascending order sort
 * API:
 * sort(Comparable[] arr), less(Comparable v, Comparable w),
 * exch(Comparable[] arr, int i, int j), isSorted(Comparable[] arr)
 * */

public class Shell {
    public static void sort(Comparable[] arr) {
        int N = arr.length;
        int h = 1;
        // Of the sequence: 1, 4, 13, 40, 121,..., 3x + 1
        // assign the greatest number less than N to h
        while (h < N / 3)
            h = 3 * h + 1;

        while (h >= 1) {
            for (int i = h; i < N; i++) {
                for (int j = i; j >= h && less(arr[j], arr[j - h]); j -= h)   // (j >= h && less(arr[j], arr[j-h]) was cleverly crafted to prevent an IndexOutOfBoundException
                    // swap the element in index j and j-h
                    exch(arr, j, j - h);
            }
            h = h/3;    // move to the next increment
        }
    }

    // a method that swaps elements in the given array indices
    private static void exch(Comparable[] arr, int i, int j) {
        Comparable swap = arr[i];
        arr[i] = arr[j];
        arr[j] = swap;
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
