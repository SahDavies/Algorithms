import edu.princeton.cs.algs4.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Timer {
    public static void main(String[] args) {
        Path path = Paths.get("C:\\Users\\Sir_Davies\\Documents\\Cousera\\Algorithms Part II\\Test Files\\wordnet\\digraphs");
        Queue<Digraph> q1 = new Queue<>();
        Queue<Digraph> q2 = new Queue<>();
        try {
            DirectoryStream<Path> paths = Files.newDirectoryStream(path);
            for (Path src : paths) {
                In input = new In(src.toFile());
                Digraph G = new Digraph(input);
                q1.enqueue(G);
                q2.enqueue(G);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int n = 5000;
        // timer for naive solution
        Stopwatch timer2 = new Stopwatch();
        System.out.println("Loading result ...");
        for (int i = 1; i <= n; i++) {
            // select a digraph
            for (Digraph G : q2) {
                // loop through every possible permutations
                for (int v = 0; v < G.V(); v++) {
                    for (int w = 0; w < G.V(); w++) {
                        AncestralPath sap = new AncestralPath(G);
                        sap.length(v, w);
                        sap.ancestor(v, w);
                    }
                }
            }
        }
        double time2 = timer2.elapsedTime();
        StdOut.printf("Naive solution : (%.2f seconds)\n", time2);
    }
}
