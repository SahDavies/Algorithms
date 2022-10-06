import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class KdTreeGenerator {

    public static void main(String[] args) {
        KdTree tree = new KdTree();
        Point2D[] points = new Point2D[10];

        int n = Integer.parseInt(args[0]);
        for (int i = 0; i < n; i++) {
            double x = StdRandom.uniform(0.0, 1.0);
            double y = StdRandom.uniform(0.0, 1.0);
            Point2D point = new Point2D(x, y);
            tree.insert(point);
            points[i] = point;
            StdOut.printf("%8.6f %8.6f\n", x, y);
        }
        System.out.println("\nTree size: " + tree.size());
        System.out.println();

        Arrays.sort(points);
        for (Point2D point:
             points) {
            if (tree.contains(point)) System.out.println("Tree contains point: " + point);
        }

    }
}