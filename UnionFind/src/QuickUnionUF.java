import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

// This class utilises an array implementation of a tree data structure
public class QuickUnionUF {
    private int[] id;   // a site-indexed array of component
    private int count;  // counts number of component left

    // Initialise the fields
    public QuickUnionUF(int n) {
        count = n;
        id = new int[n];

        for (int i = 0; i < n; i++)
            id[i] = i;
    }

    // Method that returns the root(component) of each site
    private int find(int p) {
        while (p != id[p])
            p = id[p];
        return p;
    }

    // Method to create a connection
    public void union(int p, int q) {
        int pRoot = find(p);
        int qRoot = find(q);

        if (pRoot == qRoot)
            return;

        id[pRoot] = qRoot;
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
        QuickUnionUF uf= new QuickUnionUF(N);

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
