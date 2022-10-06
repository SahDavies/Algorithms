import edu.princeton.cs.algs4.Digraph;

import java.util.*;

/**
 * Shortest ancestral path.
 */
public class SCA {
    private Digraph digraph;
    private LRUCache cache;

    /**
     * constructor takes a digraph (not necessarily a DAG)
     * @param G: an digraph
     */
    public SCA(Digraph G) {
        //make a deep copy of G
        digraph = new Digraph(G);
        cache = new LRUCache(100);
    }

    /**
     *  length of shortest ancestral path between v and w;
     *  -1 if no such path
     * @param v: a vertex
     * @param w: a vertex
     * @throws IllegalArgumentException if any of the vertex is out of range
     */
    // -1 if no such path
    public int length(int v, int w) {
        // validate vertex
        validateVertex(v);
        validateVertex(w);

        // already calculated?
        int[] p = new int[]{v, w};
        if (cache.containsKey(p))
            return cache.get(p)[1];

        // bfs search for ancestor
        int[] res = bfs(Arrays.asList(v), Arrays.asList(w));
        if (res[1] != -1) {
            cache.put(p, res);
            cache.put(new int[]{w, v}, res);
        }
        return res[1];
    }

    /**
     * a common ancestor of v and w that participates in a shortest ancestral path;
     * -1 if no such path
     * @param v: a vertex
     * @param w: a vertex
     * @throws IllegalArgumentException if any of the vertex is out of range
     *
     */
    public int ancestor(int v, int w) {
        // validate vertex
        validateVertex(v);
        validateVertex(w);

        // already calculated?
        int[] p = new int[]{v, w};
        if (cache.containsKey(p))
            return cache.get(p)[0];

        // bfs search for ancestor
        int[] res = bfs(Arrays.asList(v), Arrays.asList(w));
        if (res[0] != -1) {
            cache.put(p, res);
            cache.put(new int[]{w, v}, res);
        }
        return res[0];
    }

    /**
     * length of shortest ancestral path between any vertex in v and any vertex in w;
     * -1 if no such path
     * @param v: a vertex
     * @param w: a vertex
     * @throws IllegalArgumentException if any of the vertex is out of range
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();
        // validate vertex
        for (Integer vertex : v)
            if (vertex == null) throw new IllegalArgumentException();
            else  validateVertex(vertex);
        for (Integer vertex : w)
            if (vertex == null) throw new IllegalArgumentException();
            else  validateVertex(vertex);

        // bfs search for ancestor
        int[] res = bfs(v, w);
        return res[1];
    }

    /**
     *  a common ancestor that participates in shortest ancestral path;
     *  -1 if no such path
     * @param v: a vertex
     * @param w: a vertex
     * @throws IllegalArgumentException if any of the vertex is out of range
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();
        // validate vertex
        for (Integer vertex : v)
            if (vertex == null) throw new IllegalArgumentException();
            else  validateVertex(vertex);
        for (Integer vertex : w)
            if (vertex == null) throw new IllegalArgumentException();
            else  validateVertex(vertex);

        // bfs search for ancestor
        int[] res = bfs(v, w);
        return res[0];
    }


    /**
     * Initialize BFS.
     * @param queueListv: a list of queue
     * @param v : a list of vertex to add
     * @param visitedThis : a set of visisted vertex
     * @param visitedOther : a set of visisted vertex
     * @return -1 if all vertexes are unvisited,
     *      otherwise return the visited vertex;
     */
    private int initializeBFS(List<Queue<Integer>> queueListv, Iterable<Integer> v,
                              Map<Integer, Integer> visitedThis, Map<Integer, Integer> visitedOther) {
        for (Integer i : v) {
            Queue<Integer> queue = new LinkedList<>();
            queue.offer(i);
            queueListv.add(queue);
            if (visitedThis.containsKey(i)) return i;
            if (visitedOther.containsKey(i)) return i;
            visitedThis.put(i, 0);
        }
        return -1;
    }

    /**
     * Search for the shortest Common Ancestor by bfs.
     * search from v(start) to w(end)
     * @param v: a list of vertex
     * @param w: a list of vertex
     * @return: a common ancestor ( -1 if not found),
     *      and corresponding length of path (-1 if not found).
     */
    private int[] bfs(Iterable<Integer> v, Iterable<Integer> w) {
        ArrayList<Queue<Integer>> queueListv = new ArrayList<>(),
                queueListw = new ArrayList<>();
        // visited vertex
        HashMap<Integer, Integer> visitedV = new HashMap<>(),
                visitedW = new HashMap<>();
        // initialize bfs, if vertexes are repeated,
        // which means their shortest Common Ancestor is repeated vertex,
        // return {repeated vertex, 0}
        int state;
        state = initializeBFS(queueListv, v, visitedV, visitedW);
        if (state != -1) return new int[]{state, 0};
        state = initializeBFS(queueListw, w, visitedW, visitedV);
        if (state != -1) return new int[]{state, 0};

        // number of bfs steps
        int numOfSteps = 0;

        // commonAncestor
        int commonAncestor, shortestCommonAncestor = -1;
        int sap = Integer.MAX_VALUE;
        while (true) {
            // bfs for v
            commonAncestor = bfsOneStep(queueListv, visitedV, visitedW, numOfSteps);
            if (commonAncestor != -1 && visitedV.get(commonAncestor) < sap) {
                shortestCommonAncestor = commonAncestor;
                sap = visitedV.get(commonAncestor);
            }

            // bfs for w
            commonAncestor = bfsOneStep(queueListw, visitedW, visitedV, numOfSteps);
            if (commonAncestor != -1 && visitedW.get(commonAncestor) < sap) {
                shortestCommonAncestor = commonAncestor;
                sap = visitedW.get(commonAncestor);
            }

            // number of step plus one
            numOfSteps++;

            // break if all queues are empty
            boolean allEmpty = true;
            for (Queue<Integer> q : queueListv)
                allEmpty &= q.isEmpty();
            for (Queue<Integer> q : queueListw)
                allEmpty &= q.isEmpty();
            if (allEmpty) break;
        }
        if (shortestCommonAncestor == -1)
            return new int[]{-1, -1};
        return new int[]{shortestCommonAncestor, sap};
    }


    /**
     * A step of Binary First Search.
     * @param queueList: a list of queues maintains the vertexes to visit
     * @param visitedThis: a set of vertexes that has been visited
     * @param visitedOther: a set of vertexes that has been visited
     * @param length: the length of path bfs already visited
     * @return: a common ancestor, or -1 if not found.
     */
    private int bfsOneStep(Iterable<Queue<Integer>> queueList,
                           Map<Integer, Integer> visitedThis, Map<Integer, Integer> visitedOther,  int length) {
        int commonAncestor = -1;
        for (Queue<Integer> q : queueList) {
            // queue is emtpy
            if (q.isEmpty()) continue;

            // visit all unvisited neighbors
            int lenOfQueue = q.size();
            for (int i = 0; i < lenOfQueue; i++) {
                for (int neighbor : digraph.adj(q.poll())) {
                    // another set has already visited this vertex!!
                    // the neighbor is the common ancestor we try to find.
                    if (visitedOther.containsKey(neighbor)) {
                        if (!visitedThis.containsKey(neighbor))  q.offer(neighbor);
                        visitedThis.put(neighbor, visitedOther.get(neighbor) + length + 1);
                        if (commonAncestor == -1)
                            commonAncestor = neighbor;
                        else if (visitedThis.get(neighbor)  < visitedThis.get(commonAncestor))
                            commonAncestor = neighbor;
                    }

                    // visit vertex
                    if (!visitedThis.containsKey(neighbor)) {
                        visitedThis.put(neighbor, length + 1);
                        q.offer(neighbor);
                    }
                }
            }

        }
        return commonAncestor;
    }


    /**
     * validate vertex
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    private void validateVertex(int v) {
        if (v < 0 || v >= digraph.V())
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (digraph.V() - 1));
    }

    /**
     * LRU cache.
     */
    private static class LRUCache extends LinkedHashMap<int[], int[]> {
        private final int CAPACITY;
        LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.CAPACITY = capacity;
        }

        public int[] put(int[] key, int[] value) {
            return super.put(key, value);
        }

        public int[] get(int[] key) {
            return super.get(key);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<int[], int[]> eldest) {
            return size() > this.CAPACITY;
        }
    }
}