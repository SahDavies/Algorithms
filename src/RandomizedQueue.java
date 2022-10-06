// A debugged version of the class that provides error tracing code
/* *
 * // construct an empty randomized queue
 * public RandomizedQueue()
 *
 * // is the randomized queue empty?
 * public boolean isEmpty()
 *
 * // return the number of items on the randomized queue
 * public int size()
 *
 * // add the item
 * public void enqueue(Item item)
 *
 * // remove and return a random item
 * public Item dequeue()
 *
 * // return a random item (but do not remove it)
 * public Item sample()
 *
 * // return an independent iterator over items in random order
 * public Iterator<Item> iterator()
 *
 * // unit testing (required)
 * public static void main(String[] args)
 ** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] arr;
    private int size;
    private int tail;

    public RandomizedQueue() {
        arr = (Item[]) new Object[1];
        size = 0;
        tail = 0;
    }

    // test client
    public static void main(String[] args) {
        RandomizedQueue<String> randomizedQueue = new RandomizedQueue<>();

        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (item.equals("-"))
                StdOut.println("Removed: " + randomizedQueue.dequeue() + "\n");
            else if (item.equals("~"))
                StdOut.print("\n\"" + randomizedQueue.sample() + "\" was displayed at random");
            else
                randomizedQueue.enqueue(item);
        }

        StdOut.print("\nItems present in the queue: ");
        Iterator<String> iterator = randomizedQueue.iterator();
        while (iterator.hasNext())
            StdOut.print(iterator.next() + " ");
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }

    private void resize(int size) {
        Item[] temp = (Item[]) new Object[size];

        // debugging statement
        StdOut.println("Resizing...");

        // copy array elements to new array
        int shift = 0;
        int i = 0;
        while (i < size()) {
            if (arr[shift + i] == null) {
                shift++; // update shift
                continue;
            }
            temp[i] = arr[shift + i];
            i++; // update i
        }

        tail = size();
        arr = temp;
        // debugging statement
        StdOut.println("********************************************************");
        StdOut.print("Items copied: ");
        for (i = 0; i < tail; i++) {
            StdOut.print(arr[i] + " ");
        }
        StdOut.println("\n********************************************************");
        StdOut.println("Array size after resizing: " + arr.length + "\t new index:" + tail);
    }

    public void enqueue(Item item) {
        // debugging statement
        StdOut.printf("%s%d\t%s%d\n", "Current Array Size: ", arr.length, "Current index: ", tail);

        // test if full then resize
        if (tail == arr.length) {
            if (size() > 0 && (size() <= (tail / 4)))
                resize(arr.length / 2);
            else
                resize(2 * arr.length);
        }
        // debugging statement
        StdOut.println("Enqueuing...");

        arr[tail++] = item;
        size++;
        // debugging statements
        StdOut.printf("%s%d\t%s%s\n\n", "Item count: ", size(), "Added: ", arr[tail - 1]);

    }

    public Item dequeue() {
        if (isEmpty())
            throw new NoSuchElementException("Error! Cannot not remove item" +
                    " from an empty queue");

        // debugging statement
        StdOut.println("Deque Processing...");

        int index = StdRandom.uniform(tail);
        while (arr[index] == null)
            index = StdRandom.uniform(tail);

        Item item = arr[index];
        arr[index] = null; // statement for handling loitering
        size--;

        // debugging statement
        StdOut.print("Item count: " + size() + "\t");

        // resize array if condition is met
        if (size() > 0 && size() == (arr.length / 4))
            resize(arr.length / 2);

        return item;
    }

    // return a random item but do not remove
    public Item sample() {
        if (isEmpty())
            throw new NoSuchElementException("Error! Cannot not remove item" +
                    " from an empty queue");

        int index = StdRandom.uniform(tail);
        while (arr[index] == null) {
            index = StdRandom.uniform(tail);
        }
        return arr[index];
    }

    public Iterator<Item> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<Item> {
        int i = 0;

        @Override
        public boolean hasNext() {
            return i < tail;
        }

        @Override
        public Item next() {
            while (arr[i] == null)
                i++;

            return arr[i++];
        }
    }
}
