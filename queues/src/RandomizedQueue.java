import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Similar to a queue data structure with the difference
// being that item are removed at random instead of FIFO
public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] arr;     // data structure used
    private int size;       // keeps track of the number of items present
    private int tail;       // a pointer used to point to the closet free space

    public RandomizedQueue() {
        arr = (Item[]) new Object[1];
        size = 0;
        tail = 0;
    }

    // test client
    public static void main(String[] args) {
        // unit test goes here
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }

    // resize the array according to need
    private void resize(int n) {
        Item[] temp = (Item[]) new Object[n];

        // copy array elements to new array
        if (size() >= 0) System.arraycopy(arr, 0, temp, 0, size());
        // reset tail and reassign arr
        tail = size();
        arr = temp;
    }

    // add item to queue
    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Error! Item can not be a null value");

        // test if full then resize
        if (tail == arr.length)     resize(2 * arr.length);

        // add item to queue and update size
        arr[tail++] = item;
        size++;
    }

    // remove at random, an item from the queue
    public Item dequeue() {
        if (isEmpty())
            throw new NoSuchElementException("Error! Cannot not remove item" +
                    " from an empty queue");

        // assign a random number between 0 - tail
        int index = StdRandom.uniform(tail);

        // save the random item and delete it from the array
        Item item = arr[index];
        arr[index] = arr[--tail]; // replace arr[index] with the last element in the queue
        arr[tail] = null;
        size--;

        // resize array if condition is met
        if (size() > 0 && size() == (arr.length / 4))
            resize(arr.length / 2);

        return item;
    }

    // return a random item but do not remove
    public Item sample() {
        if (isEmpty())
            throw new NoSuchElementException("Error! Cannot not peek " +
                    "at an empty queue");
        // assign a random number between 0 and tail - 1
        int index = StdRandom.uniform(tail);
        return arr[index];
    }

    public Iterator<Item> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<Item> {
        int i = 0;
        Item[] items;

        public ArrayIterator() {
            items = (Item[]) new Object[size()];
            System.arraycopy(arr, 0, items, 0, size());
            StdRandom.shuffle(items);
        }

        @Override
        public boolean hasNext() {
            return i < items.length;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException("You have reached " +
                        "the end of the sequence");
            return items[i++];
        }
    }
}