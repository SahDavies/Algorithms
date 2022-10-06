import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

// This class utilises an array data structure
public class QuickFindUF {
    private int[] id;
    private int count;

    // Initialise fields
    public QuickFindUF(int n) {
        count = n;
        // create array object and initialise array
        id = new int[n];
        for (int i = 0; i < n; i++)
            id[i] = i;
    }

    // method that returns the component of site p
    private int find(int p) {
        return id[p];
    }

    // method to create a connection between two sites
    public void union(int p, int q) {
        int pId = find(p);
        int qId = find(q);

        if (pId == qId)
            return;

        for (int i = 0; i < id.length; i++) {
            if (id[i] == pId)
                id[i] = qId;
        }
        count--; // reduce the number of component
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public int getCount() {
        return count;
    }

    // test client
    public static void main(String[] args) {
        int N = StdIn.readInt();
        QuickFindUF uf= new QuickFindUF(N);

        while (!StdIn.isEmpty()) {
            int p = StdIn.readInt();
            int q = StdIn.readInt();

            if (uf.connected(p,q))
                continue;

            uf.union(p, q);
            StdOut.print(p + "," + q + "  "); // print out each connected sites
        }
        StdOut.print("\n" + uf.getCount() + " Components");
    }
}
