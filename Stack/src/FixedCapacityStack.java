import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

/* *
* AN ARRAY IMPLEMENTATION OF A STACK
* **********************************
* Version 1.0
* API:
* FixedCapacityStack, isEmpty(),
* push(Item item), pop()
* **********************************
* Version 2.0
* API:
* FixedCapacityStack, isEmpty(),
* push(Item item), pop()
* Implements iterator
* */
public class FixedCapacityStack<Item> implements Iterable<Item> {
    private Item[] arr;
    private int N = 0; // size of stack

    public FixedCapacityStack(int capacity) {
        arr = (Item[]) new Object[capacity];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public void push(Item item) {
        if (N == arr.length) {
            StdOut.print("Warning! Stack Overflow: Cannot add " + item);
            return;
        }
        arr[N++] = item;
    }

    public Item pop() {
        if (isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("Warning! Stack Underflow:" +
                    " There is no item to be removed");
        }

        Item item = arr[--N];
        arr[N] = null; //statement for handling loitering
        return item;
    }


    @Override
    public Iterator<Item> iterator() {
        return new ReverseArrayIterator();
    }

    private class ReverseArrayIterator implements Iterator<Item>
    {
        private int i = N;

        public boolean hasNext() {
             return i > 0;
        }

        public Item next() {
            return arr[--i];
        }
    }
    // test client
    public static void main(String[] args) {
        int capacity = StdIn.readInt();
        FixedCapacityStack<String> stack = new FixedCapacityStack<>(capacity);

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
