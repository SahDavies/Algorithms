import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

// This class utilises an array implementation of a tree data structure
public class WeightedQuickUnionUF {
    private int[] id;   // a site-indexed array of component
    private int[] size; // stores the size of each tree
    private int count;  // counts number of component left

    // Initialise the fields
    public WeightedQuickUnionUF(int n) {
        count = n;

        id = new int[n];
        for (int i = 0; i < n; i++)
            id[i] = i;

        size = new int[n];
        for (int i = 0; i < n; i++)
            size[i] = 1;
    }

    // Method that returns the root(component) of each site
    private int find(int p) {
        while (p != id[p])
            p = id[p];
        return p;
    }

    // Method to create a connection
    public void union(int p, int q) {
        int i = find(p);
        int j = find(q);

        if (i == j)
            return;

       if (size[i] < size[j]) {
           id[i] = j;
           size[j] += size[i];
       }
       else {
           id[j] = i;
           size[i] += size[j];
       }
        count--;
    }

    // Method to check for connection
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    // returns the number of component left
    public int getCount() {
        return count;
    }

    // test client
    public static void main(String[] args) {
        int N = StdIn.readInt();
        WeightedQuickUnionUF uf= new WeightedQuickUnionUF(N);

        while (!StdIn.isEmpty()) {
            int p = StdIn.readInt();
            int q = StdIn.readInt();

            if (uf.connected(p,q))
                continue;

            uf.union(p,q);
            StdOut.print(p + "," + q + "  "); // print out each connected sites
        }
        StdOut.print("\n" + uf.getCount() + " Components");
    }
}
