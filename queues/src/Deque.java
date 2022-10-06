/* *
* Rules:
* If there is only one item in the deque, first and last should point to the same item
*
* API
*  // construct an empty deque
    public Deque()

    // is the deque empty?
    public boolean isEmpty()

    // return the number of items on the deque
    public int size()

    // add the item to the front
    public void addFirst(Item item)

    // add the item to the back
    public void addLast(Item item)

    // remove and return the item from the front
    public Item removeFirst()

    // remove and return the item from the back
    public Item removeLast()

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator()

    // unit testing (required)
    public static void main(String[] args)
**/
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private class Node {
        Item item;
        Node rightLink; // holds a reference to the least recent item
        Node leftLink; // holds a reference to the most recent item
    }

    private Node first;
    private Node last;
    private int size;

    public Deque() {
        first = null;
        last = null;
        size = 0;
    }

    public void addFirst(Item item) {
        // throw an exception if null argument is supplied
        if (item == null)
            throw new IllegalArgumentException("Argument must not be null");

        if (first == null) {
            first = new Node();
            first.item = item;
            last = first;       // let last save a reference to the first item ever added
            size++;
            return;
        }

        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.rightLink = oldFirst;
        oldFirst.leftLink = first;
        size++; // update size
    }

    public Item removeFirst() {
        // throw an exception if collection is empty
        if (isEmpty())
            throw new NoSuchElementException();

        if (size == 1) {
            Item item = first.item;
            first = null;
            last = null;
            size--;
            return item;
        }

        Item item = first.item;
        first = first.rightLink;
        first.leftLink = null;
        size--; // update size
        return item;
    }

    public void addLast(Item item) {
        // throw an exception if null argument is supplied
        if (item == null)
            throw new IllegalArgumentException("Argument must not be null");

        if (last == null) {
            last = new Node();
            last.item = item;
            first = last;       // let last save a reference to the first item ever added
            size++;
            return;
        }

        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.leftLink = oldLast;
        oldLast.rightLink = last;
        size++; // update size
    }

    public Item removeLast() {
        // throw an exception if collection is empty
        if (isEmpty())
            throw new NoSuchElementException();

        if (size == 1) {
            Item item = last.item;
            last = null;
            first = null;
            size--;
            return item;
        }

        Item item = last.item;
        last = last.leftLink;
        last.rightLink = null;
        size--; // update size
        return item;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    // define the class that implements iterator
    private class ListIterator implements Iterator<Item> {
        Node current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();

            Item item = current.item;
            current = current.rightLink;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // test client
    public static void main(String[] args) {
        Deque<Integer> stack = new Deque<>();
        Deque<Integer> queue = new Deque<>();

        // push items in from standard input
        for (int i = 0; i < 10; i++) {
            stack.addFirst(i);
        }
        // display the number of items in stack
        StdOut.print("Number of items present in stack: " + stack.size());

        // display the items read
        StdOut.print("\nItems present in stack: ");
        for (int x : stack) {
            System.out.print(x + " ");
        }

        // remove the least recent item
        StdOut.println();
        StdOut.println("Least recent item: " + stack.removeLast());

        // pop items from the stack
        StdOut.print("\nItems in reverse order: ");
        while (!stack.isEmpty()) {
            StdOut.print(stack.removeFirst() + " ");
        }
        StdOut.println("\nItems present in stack after pop operation: " + stack.size());

        // add items in from standard input
        for (int i = 0; i < 10; i++) {
            queue.addLast(i);
        }
        // display the number of items in queue
        StdOut.print("\nNumber of items present in queue: " + queue.size());

        // display the items read
        StdOut.print("\nItems present in queue:");
        for (int x : queue) {
            System.out.print(x + " ");
        }

        // remove items from the queue
        StdOut.print("\nItems in original order: ");
        while (!queue.isEmpty())
            StdOut.print(queue.removeFirst() + " ");

        // display the number of items in queue
        StdOut.println("\nNumber of items present in queue after dequeue operation: " + queue.size());
    }
}
