
/* *
* Ascending order sort
* API:
* sort(Comparable[] arr), less(Comparable v, Comparable w),
* exch(Comparable[] arr, int i, int j), isSorted(Comparable[] arr)
* */

import edu.princeton.cs.algs4.StdRandom;

public class Selection {
    public static void sort(Comparable[] arr) {
        int N = arr.length;

        // outer loop traverses through each element
        for (int i = 0; i < N; i++) {
            int min = i;    // index holding the minimum element

            // loop through the array to find the index with minimum element
            // compare the current minimum with elements to the right
            // update minimum index when required
            for (int j = i + 1; j < N; j++) {
                if (less(arr[j], arr[min]))
                    min = j;    // update min
                else
                    break;
            }
            exch(arr, i, min);
        }
    } // end of sort

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
