import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.NoSuchElementException;

// Client of RandomizedQueue
public class Permutation {
    public static void main(String[] args) {
        if (args[0] == null)
            throw new NoSuchElementException("Command-line argument is empty");

        String item;
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<>();

        if (k < 0)
            throw new IllegalArgumentException("Invalid assignment");


        // reads from standard input
        while (!StdIn.isEmpty()) {

            item = StdIn.readString();
            // add item to queue
            queue.enqueue(item);
        }
        // print k random items from the queue
        while (k > 0) {
            StdOut.println(queue.dequeue());
            k--;
        }
    }
}
