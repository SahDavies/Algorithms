
/* *
 * AN ARRAY IMPLEMENTATION OF A QUEUE
 * **********************************
 * Version 1.0
 * API:
 * DynamicSizeQueue(), isEmpty(), dequeue(),
 * enqueue(Item item), size()
 * **********************************
 * Version 2.0
 * API:
 * DynamicSizeQueue(), isEmpty(), dequeue(),
 * enqueue(Item item), size()
 * Implements iterator
 * */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DynamicSizeQueue<Item> implements Iterable<Item> {
    private Item[] queue;
    private int head = 0;
    private int tail = 0;

    public DynamicSizeQueue() {
        queue = (Item[]) new Object[1];
    }

    // check if queue is empty
    public boolean isEmpty() {
        return head == tail;
    }

    // return the number of items in the queue
    public int size() {
        return tail - head;
    }

    private void resize(int newSize) {
        int qLength = queue.length;
        Item[] temp = (Item[]) new Object[newSize];

        for(int i = 0; i < qLength; i++)
            temp[i] = queue[head + i];

        head = 0;
        tail = qLength;
        queue = temp;
    }

    // add an item to the list
    public void enqueue(Item item) {
        if (tail == queue.length)
            resize(2 * queue.length);

        queue[tail++] = item;
    }

    // remove an item from the list
    public Item dequeue() {
        if (isEmpty())
            throw new NoSuchElementException("Error! Cannot not remove item" +
                    " from an empty queue");

        Item item = queue[head];
        queue[head++] = null; // statement for handling loitering

        if (size() > 0 && size() == (queue.length / 4))
            resize(queue.length / 2);
        return item;
    }

    @Override
    // iterator method
    public Iterator<Item> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<Item> {

        private int i = head;

        @Override
        public boolean hasNext() {
            return i < tail;
        }

        @Override
        public Item next() {
            return queue[i++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    // test client
    public static void main(String[] args) {
        DynamicSizeQueue<String> stack = new DynamicSizeQueue<>();

        while(!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (item.equals("-"))
                StdOut.print(stack.dequeue() + " ");
            else
                stack.enqueue(item);
        }

        StdOut.print("\nItems present in the queue: ");
        for (String item: stack) {
            StdOut.print(item + " ");
        }
    }
}
