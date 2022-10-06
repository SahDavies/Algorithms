/* *
 * This data type can only handle queries from two source vertices.
 * Modification: Create an overloaded method for length, ancestor and bfs
 * with variable length argument to accommodate for any number of source vertex.
 * Modify the constructor to include an argument for the number of sources.
 * */

import edu.princeton.cs.algs4.In;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SeparateChainingHashST;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class AncestralPath {
    private static final int SCA = 0;    // index of the shortest common ancestor, i.e array[SCA} returns SCA
    private static final int LENGTH = 1;    // index of the LENGTH associated with SCA. Array[LENGTH] returns the length
    private static final int SOURCES = 2;      // number of source vertices from which to find a common ancestor
    private final Digraph G;
    private int[] cache;    // stores most recently computed vertices
    private int[] result;

    public AncestralPath(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException("Null value supplied");

        this.G = new Digraph(G.V());

        // make a copy of digraph
        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj(v)) {
                this.G.addEdge(v, w);
            }
        }
    }

    // Method returns both the SCA and its associated path LENGTH. Lockstep approach is used
    private int[] bfs(int s1, int s2) {
        validateVertex(Arrays.asList(s1, s2));

        // Case: when source1 and source2 are equivalent
        if (s1 == s2)   return new int[] {s1, 0};

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

        // main operation in finding sca if the program flow reaches this point
        int sca = lockstepBFS(q, marked, distTo);
        int length = (sca != -1) ? distTo[0][sca] + distTo[1][sca] : -1;
        return new int[] {sca, length};
    }   // end bfs

    // Overloaded version
    private int[] bfs(Iterable<Integer> s1, Iterable<Integer> s2) {
        validateVertex(s1);
        validateVertex(s2);

        // 2d array data structure used for processing bfs operation
        boolean[][] mark = new boolean[SOURCES][G.V()];
        int[][] distTo = new int[SOURCES][G.V()];

        // queues to hold vertices belonging to source1 and source2
        Queue<Integer>[] q = (Queue<Integer>[]) new Queue[SOURCES];
        q[0] = new Queue<>();
        q[1] = new Queue<>();

        // mark and enqueue each vertex in the iterable list
        for (Integer v : s1) {
            mark[1][v] = true;
            q[1].enqueue(v);
        }
        for (Integer v: s2) {
            mark[0][v] = true;
            q[0].enqueue(v);
            // Case: when a vertex in source1 is also present in source2
            if (mark[0][v] == mark[1][v]) {
                return new int[] {v, 0};
            }
        }
        // main operation in finding sca if the program flow reaches this point
        int sca = lockstepBFS(q, mark, distTo);
        int length = (sca != -1) ? distTo[0][sca] + distTo[1][sca] : -1;
        return new int[] {sca, length};
    }

    private int lockstepBFS(Queue<Integer>[] q, boolean[][] marked, int[][] distTo) {
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
            int cycle = counter % SOURCES;    // possible values of variable cycle is in the range [0, SOURCES - 1]

            while (!q[cycle].isEmpty()) {
                int v = q[cycle].dequeue();
                // loop through the adjacency list of v
                for (int w : G.adj(v)) {
                    if (!marked[cycle][w]) {
                        marked[cycle][w] = true;
                        distTo[cycle][w] = distTo[cycle][v] + 1;
                        if (distTo[cycle][w] >= length) {
                            while (!q[cycle].isEmpty()) q[cycle].dequeue();
                            break;
                        }
                        q[cycle].enqueue(w);
                    }
                    // compare both marked array
                    if (marked[cycle][w] == marked[(cycle + 1) % SOURCES][w]) {
                        int newLength = distTo[cycle][w] + distTo[(cycle + 1) % SOURCES][w];
                        if (length > newLength) {
                            sca = w;
                            length = newLength;
                        }
                    }   // end of comparison block
                }   // end of adjacency list iteration
            }   // end of while statement
            counter++;    // update counter
        }   // end while loop
        return sca;
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
            validateVertex(v);
        }
    }

    private int[] getResult(int[] key) {
        int[] inverted = {key[1], key[0]};
        if (Arrays.equals(key, cache) || Arrays.equals(inverted, cache))
            return result;
        cache = Arrays.copyOf(key, key.length);
        return new int[0];
    }

    public int length(int v, int w) {
        // get saved result
        int[] key = {v, w};
        if (!Arrays.equals(getResult(key), new int[0]))
            return result[LENGTH];

        // otherwise, compute bfs
        result = bfs(v, w);
        return result[LENGTH];
    }

    public int ancestor(int v, int w) {
        // get saved result
        int[] key = {v, w};
        if (!Arrays.equals(getResult(key), new int[0]))
            return result[SCA];

        // otherwise, compute bfs
        result = bfs(v, w);
        return result[SCA];
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = bfs(v, w);
        return result[LENGTH];
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = bfs(v, w);
        return result[SCA];
    }

    private class LRUCache {
        final LinkedHashMap<int[], int[]> cache;
        final int size;

        protected LRUCache(int size) {
            cache = new LinkedHashMap<>(size);
            this.size = size;
        }

        protected void put(int[] key, int[] value) {
            if (cache.containsKey(key)) {
                cache.remove(key);
                cache.put(key, value);
                return;
            }

            if (cache.size() >= this.size) {
                int[] first = cache.keySet().iterator().next();
                this.cache.remove(first);
            }

            cache.put(key, value);
        }

        protected int[] get(int[] key) {
            if (!cache.containsKey(key))
                return null;

            int[] value = cache.get(key);
            cache.remove(key);
            cache.put(key, value);

            return value;
        }
    }

    public static void main(String[] args) {
        Path path = Paths.get("C:\\Users\\Sir_Davies\\Documents\\Cousera\\Algorithms Part II\\Test Files\\wordnet\\digraphs");
        SeparateChainingHashST<Digraph, Path> q = new SeparateChainingHashST<>();
        try {
            DirectoryStream<Path> paths = Files.newDirectoryStream(path);
            System.out.println("Enqueue order : ");
            for (Path src : paths) {
                System.out.println(src.toString());
                In input = new In(src.toFile());
                Digraph G = new Digraph(input);
                q.put(G, src);
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // select a digraph
        for (Digraph G : q.keys()) {
            // loop through every possible permutations
            for (int v = 0; v < G.V(); v++) {
                for (int w = 0; w < G.V(); w++) {
                    final int size = G.V();
//                    System.out.println("v : " + v + ", " + "w : " + w);

                    BreadthFirstDirectedPaths bfs_source1 = new BreadthFirstDirectedPaths(G, v);
                    BreadthFirstDirectedPaths bfs_source2 = new BreadthFirstDirectedPaths(G, w);
                    int length = Integer.MAX_VALUE, sca = -1;
                    for (int i = 0; i < size; i++) {
                        if (bfs_source1.hasPathTo(i) && bfs_source2.hasPathTo(i)) {
                            int l = bfs_source1.distTo(i) + bfs_source2.distTo(i);
                            if (l < length) {
                                sca = i;
                                length = l;
                            }
                        }
                    }
                    if (length == Integer.MAX_VALUE) length = -1;

                    AncestralPath sap = new AncestralPath(G);
                    int l = sap.length(v, w);
                    int ancestor = sap.ancestor(v, w);
                    if (length != l) {
                        System.out.println("Unequal results from " + q.get(G));
                        System.out.println("v : " + v + "," + " w : " + w);
                        System.out.println("SAP sca: " + ancestor);
                        System.out.println("BFS sca: " + sca);
                        System.out.println("SAP Length: " + l);
                        System.out.println("BFS Length: " + length);
                        System.out.println();
                    }
                }
            }
            System.out.println();
            System.out.println("=================================================");
            System.out.println();
        }
    }
}
