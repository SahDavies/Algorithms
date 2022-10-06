import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* *
 * A LINKED LIST IMPLEMENTATION OF A STACK
 * **********************************
 * Version 1.0
 * API:
 * Stack(), isEmpty(), push(Item item), pop()
 * **********************************
 * Version 2.0
 * API:
 * Stack(), isEmpty(), push(Item item), pop()
 * Implements iterator
 * */

public class Stack<Item> implements Iterable<Item> {
    // create a record
    private class Node {
        Item item;
        Node next;
    }
    // field declaration
    private Node first = null;

    public boolean isEmpty() {
        return first == null;
    }

    public void push(Item item) {
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
    }

    public Item pop() {
        if (isEmpty())
            throw new RuntimeException("The stack is empty." +
                    " Failed attempt to pop from stack");

        Item item = first.item;
        first = first.next;
        return item;
    }

    @Override
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        @Override
        public boolean hasNext() {
            return current != null  ;
        }

        @Override
        public Item next() {
            Item item = current.item;
            current = current.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    // test client
    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();

        while(!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (item.equals("-"))
                StdOut.print(stack.pop() + " ");
            else
                stack.push(item);
        }

        StdOut.print("\nItems present in the stack: ");
        for (String item: stack) {
            StdOut.print(item + " ");
        }
    }
}
