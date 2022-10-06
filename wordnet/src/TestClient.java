import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestClient {
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

                    // naive solution to the ancestral path problem
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

                    // optimised solution to the ancestral path problem
                    SAP sap = new SAP(G);
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
