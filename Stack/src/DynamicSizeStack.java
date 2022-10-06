import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

/* *
 * AN ARRAY IMPLEMENTATION OF A STACK
 * **********************************
 * Version 1.0
 * API:
 * DynamicSizeStack(), isEmpty(), push(Item item), pop()
 * **********************************
 * Version 2.0
 * API:
 * DynamicSizeStack(), isEmpty(), push(Item item), pop()
 * Implements iterator
 * */

public class DynamicSizeStack<Item> implements Iterable<Item> {
    private Item[] arr;
    private int N = 0; // size of stack

    public DynamicSizeStack () {
        arr = (Item[]) new Object[1];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    private void resize(int size) {
        Item[] temp = (Item[]) new Object[size];

        for (int i = 0; i < N; i++)
            temp[i] = arr[i];

        arr = temp;
    }

    public void push(Item item) {
        if (N == arr.length)
            resize(2 * arr.length);

        arr[N++] = item;
    }

    public Item pop() {
        if (isEmpty())
            throw new ArrayIndexOutOfBoundsException("Error! Stack Underflow:" +
                    " There is no item to be removed");

        Item item = arr[--N];
        arr[N] = null; //statement for handling loitering

        if (N > 0 && N == (arr.length / 4))
            resize(arr.length / 2);

        return item;
    }

    @Override
    public Iterator<Item> iterator() {
        return new ReverseArrayIterator();
    }

    private class ReverseArrayIterator implements Iterator<Item> {
        private int i = N;

        @Override
        public boolean hasNext() {
            return i > 0;
        }

        @Override
        public Item next() {
            return arr[--i];
        }
    }
    // test client
    public static void main(String[] args) {
        DynamicSizeStack<String> stack = new DynamicSizeStack<>();

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
