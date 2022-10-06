/* *
 * Data Structure: An array implementation of Tree
 * API:
 * public void insert(Key key) - inserts an item, public Key delMax() - removes maximum item
 * public boolean isEmpty(), public MaxPQ(int capacity) - constructor
 * Methods:
 * private void swim(int k)             moves node at k upward and places it in it's position
 * private void sink(int k)             moves node at k downward and places it in it's position
 * private boolean less(int i, int j)   compares node at i if it is less than node at j
 * private void exch(int i, int j)      swaps the node at i with node at j
 * */

public class MaxPQ<Key extends Comparable<Key>> {
    private Key[] pq;
    private int N;  // stores the number of items in the collection

    public MaxPQ() {
        pq = (Key[]) new Comparable[1];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public void insert(Key x) {
        if (N == pq.length)
            resize(2 * pq.length);

        pq[++N] = x;
        swim(N);
    }

    public Key delMax() {
        if (N == 0) {
            throw new IllegalArgumentException(
                    "There is no item in the queue. Cannot remove from an empty queue");
        }
        Key max = pq[1];
        exch(1, N--);
        sink(1);
        pq[N+1] = null;     // prevent loitering

        if (N > 0 && N == (pq.length / 4))
            resize(pq.length / 2);
        
        return max;
    }

    private void resize(int size) {
        Key[] temp = (Key[]) new Comparable[size];
        // copy items from pq to temp
        System.arraycopy(pq, 0, temp, 0, pq.length);

        pq = temp;
    }

    private void swim(int k) {
        while (k > 1 && less(k/2, k)) {
            exch(k, k/2);   // swap parent node and child node
            k = k/2;           // update pointer k
         }
    }

    private void sink(int k) {
        while (2*k <= N) {
            int j = 2*k;

            // let j be the pointer to the largest of the children node of k
            if (j < N && less(j, j+1))
                j++;
            // break loop if parent node is greater than child node
            if (!less(k, j))
                break;

            exch(k, j);     // swap parent node with child node
            k = j;          // k now points to its child node
        }
    }

    private boolean less(int i, int j) {
        return pq[i].compareTo(pq[j]) < 0;
    }
    private void exch(int i, int j) {
        Key temp = pq[i];
        pq[i] = pq[j];
        pq[j] = temp;
    }
}
