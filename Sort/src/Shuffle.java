
/* *
 * Random order sort using shell algorithm to compare and exchange
 * API:
 * sort(Comparable[] arr), less(Comparable v, Comparable w),
 * exch(Comparable[] arr, int i, int j), isSorted(Comparable[] arr)
 * */

import edu.princeton.cs.algs4.StdRandom;

// A shuffle class that uses key to shuffle
public class Shuffle {
    public static void sort (Comparable[] arr) {
        int N = arr.length;
        int h = 1; // size of sub-array
        double[] key = new double[N];

        // initialise key with random real numbers between [0,1)
        for (int i = 0; i < N; i++)
            key[i] = StdRandom.uniform();

        // Of the sequence: 1, 4, 13, 40, 121,..., 3x + 1
        // assign the greatest number less than N to h
        while (h < N / 3)
            h = 3 * h + 1;

        // sort the elements in arr using key
        while (h >= 1) {
            for (int i = h; i < N; i++) {
                for (int j = i; j >= h && less(key[j], key[j - h]); j -= h)   // (j >= h && less(arr[j], arr[j-h]) was cleverly crafted to prevent an IndexOutOfBoundException
                    exch(arr, j, j - h);
            }
            h = h/3;    // update size of sub-array
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
}
