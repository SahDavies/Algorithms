/* *
 * Caching technique: Use a single set object to store both iterable objects
 * */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class SAP {
    private static final int SCA = 0;    // index of the shortest common ancestor, i.e. array[SCA] returns SCA
    private static final int LENGTH = 1;    // index of the LENGTH associated with SCA, array[LENGTH] returns the length
    private static final int SOURCES = 2;      // number of source vertices from which to find a common ancestor
    private final Digraph G;
    private Set<Integer> cache;    // stores most recently computed vertices
    private int[] result;

    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException("Null value supplied");

        this.G = new Digraph(G.V());
        cache = new TreeSet<>();

        // make a copy of digraph
        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj(v)) {
                this.G.addEdge(v, w);
            }
        }
    }

    // Method returns both the SCA and its associated path LENGTH. Lockstep approach is used
    private int[] bfs(int s1, int s2) {
        // Case: when source1 and source2 are equivalent
        if (s1 == s2) return new int[]{s1, 0};

        // 2d array data structure used for processing bfs operation
        boolean[][] marked = new boolean[SOURCES][G.V()];
        int[][] distTo = new int[SOURCES][G.V()];

        // queues to hold vertices belonging to source1 and source2
        Queue<Integer>[] q = (Queue<Integer>[]) new Queue[SOURCES];
        q[0] = new Queue<>();
        q[1] = new Queue<>();

        // marks and enqueue visited vertex
        marked[0][s2] = true;
        marked[1][s1] = true;
        q[0].enqueue(s2);
        q[1].enqueue(s1);

        // this point in the program flow marks the main operation in finding sca
        return lockstepBFS(q, marked, distTo);
    }

    // Overloaded version
    private int[] bfs(Iterable<Integer> s1, Iterable<Integer> s2) {
        // 2d array data structure used for processing bfs operation
        boolean[][] marked = new boolean[SOURCES][G.V()];
        int[][] distTo = new int[SOURCES][G.V()];

        // queues to hold vertices belonging to source1 and source2
        Queue<Integer>[] q = (Queue<Integer>[]) new Queue[SOURCES];
        q[0] = new Queue<>();
        q[1] = new Queue<>();

        // marked and enqueue each vertex in the iterable list
        for (int v : s1) {
            marked[1][v] = true;
            q[1].enqueue(v);
        }
        for (int v: s2) {
            marked[0][v] = true;
            q[0].enqueue(v);
            // Case: when a vertex in source1 is also present in source2
            if (marked[0][v] == marked[1][v]) {
                return new int[] {v, 0};
            }
        }
        // main operation in finding sca if the program flow reaches this point
        return lockstepBFS(q, marked, distTo);
    }

    private int[] lockstepBFS(Queue<Integer>[] q, boolean[][] marked, int[][] distTo) {
        int sca = -1;     // shortest common ancestor
        int length = Integer.MAX_VALUE;

        /* *
         * Counts the number of dequeue operations.
         * When used in such expression as counter % SOURCES, it tells us
         * when to alternate the queue from which to dequeue
         * */
        int counter = 0;

        // break loop if both queues are empty
        while (!q[0].isEmpty() || !q[1].isEmpty()) {
            int cycle = counter % SOURCES;    // possible values of variable - cycle is in the range [0, SOURCES - 1]

            // this loop selects a queue and if queue selected is not empty, the block is executed
            while (!q[cycle].isEmpty()) {
                int v = q[cycle].dequeue();
                // loop through the adjacency list of v
                for (int w : G.adj(v)) {
                    if (!marked[cycle][w]) {
                        marked[cycle][w] = true;
                        distTo[cycle][w] = distTo[cycle][v] + 1;
                        // terminate search for sca in this particular queue
                        if (distTo[cycle][w] >= length) {
                            while (!q[cycle].isEmpty()) q[cycle].dequeue();
                            break;
                        }
                        q[cycle].enqueue(w);
                    }
                    // compare both marked array to find a common ancestor
                    if (marked[cycle][w] == marked[(cycle + 1) % SOURCES][w]) {
                        int newLength = distTo[cycle][w] + distTo[(cycle + 1) % SOURCES][w];
                        if (length > newLength) {
                            sca = w;
                            length = newLength;
                        }
                    }   // end of comparison block
                }
                // update counter and cycle
                counter++;
                cycle = counter % SOURCES;
            }   // end of inner while block
            counter++;  // update counter
        }   // end outer while block
        return new int[] {sca, length};
    }

    private void validateVertex(int v) {
        int V = G.V();
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    // an overloaded version that takes an Iterable object
    private void validateVertex(Iterable<Integer> vertices) {
        if (vertices == null)
            throw new IllegalArgumentException("Null value was supplied!");

        for (Integer v : vertices) {
            if (v == null) throw new IllegalArgumentException("Vertex cannot be null");
            validateVertex(v);
        }
    }

    @SafeVarargs
    private int[] getResult(Iterable<Integer>... collection) {
        Set<Integer> set = new TreeSet<>();
        for (Iterable<Integer> vertices : collection) {
            for (Integer v : vertices) {
                set.add(v);
            }
        }
        if (set.equals(cache)) return result;
        cache = set;
        result = bfs(collection[0], collection[1]);
        return result;
    }

    private int[] getResult(int... collection) {
        Set<Integer> set = new TreeSet<>();
        for (int v : collection) {
            set.add(v);
        }

        if (set.equals(cache))  return result;
        cache = set;
        result = bfs(collection[0], collection[1]);
        return result;
    }

    public int length(int v, int w) {
        validateVertex(Arrays.asList(v, w));
        // get saved result
        int[] result = getResult(v, w);
        return result[LENGTH];
    }

    public int ancestor(int v, int w) {
        validateVertex(Arrays.asList(v, w));
        // get saved result
        int[] result = getResult(v, w);
        return result[SCA];
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertex(v);
        validateVertex(w);
        int[] result = getResult(v, w);
        return (result[SCA] == -1) ? -1 : result[LENGTH];
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertex(v);
        validateVertex(w);
        int[] result = getResult(v, w);
        return result[SCA];
    }

    public static void main(String[] args) {
        // test client goes here
    }
}
