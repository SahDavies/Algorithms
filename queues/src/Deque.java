import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private class Node {
        Item item;
        Node right, left;
    }

    private Node first, last;
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

        Node node = new Node();
        node.item = item;
        node.right = first;
        if (first == null) last = node;
        else first.left = node;
        first = node;
        size++; // update size
    }

    public Item removeFirst() {
        // throw an exception if collection is empty
        if (isEmpty())
            throw new NoSuchElementException();

        Item item = first.item;

        if (first == last) {
            first = null;
            last = null;
        } else {
            first = first.right;
            first.left = null;
        }
        size--; // update size
        return item;
    }

    public void addLast(Item item) {
        // throw an exception if null argument is supplied
        if (item == null)
            throw new IllegalArgumentException("Argument must not be null");

        Node node = new Node();
        node.item = item;
        node.left = last;
        if (last == null) first = node;     // last equalling null tells us that the deque was initially empty
        else last.right = node;
        last = node;
        size++;
    }

    public Item removeLast() {
        // throw an exception if collection is empty
        if (isEmpty())
            throw new NoSuchElementException();

        Item item = last.item;

        if (last == first) {
            first = null;
            last = null;
        } else {
            last = last.left;
            last.right = null;
        }
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
            current = current.right;
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
            stack.addLast(i);
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
        StdOut.println("Most recent item: " + stack.removeLast());

        // pop items from the stack
        StdOut.print("\nItems in reverse order: ");
        while (!stack.isEmpty()) {
            StdOut.print(stack.removeLast() + " ");
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
        StdOut.print("\nPopping the queue...: ");
        while (!queue.isEmpty())
            StdOut.print(queue.removeFirst() + " ");

        // display the number of items in queue
        StdOut.println("\nNumber of items present in queue after dequeue operation: " + queue.size());
    }
}
