import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * API:
 * public BruteCollinearPoints(Point[] points)    // finds all line segments containing 4 points
 * public           int numberOfSegment()        // the number of line segments
 * public LineSegment[] segments()                // the line segments
 */

public class BruteCollinearPoints {
    private final LineSegment[] lineSegments;
    private final int numberOfSegments;

    public BruteCollinearPoints(Point[] points) {
        // throw an exception if the argument is null
        if (isNull(points))
            throw new IllegalArgumentException("You have not supplied any point; " +
                    "The array is either empty or some element points to null ");

        // local variables declaration
        int size = points.length;
        Point[] pointsCPY = Arrays.copyOf(points, size);
        double[] slopes = new double[size];
        ArrayList<LineSegment> segmentList = new ArrayList<>();

        // sort the
        Arrays.sort(pointsCPY);
        // throw an exception if the element in the array is repeated
        for (int i = 0; i < (size - 1); i++) {
            if (points[i].compareTo(points[i + 1]) == 0)
                throw new IllegalArgumentException("There is a problem" +
                        " with the points supplied (either there is repetition of point" +
                        " or the point is a reference to null)");
        }

        // Pick a pivot
        for (int idx = 0; idx < size; idx++) {
            Point pivot = pointsCPY[idx];

            // build array of slopes with respect to pivot
            for (int j = 0; j < size; j++) {
                slopes[j] = pivot.slopeTo(pointsCPY[j]);
            }

            // find points collinear with pivot
            for (int i = 0; i < size; i++) {
                if (pivot.compareTo(pointsCPY[i]) >= 0) continue; // skip duplicate segment

                int freq = 0, endPoint = 0;
                // count the frequency of occurrence of the selected slope, i
                for (int j = 0; j < size; j++) {
                    if (slopes[i] == slopes[j]) {
                        if (j < i) break;   // skip repeated slope
                        freq++;
                        endPoint = j;   // track endpoint
                    }
                }
                // if it is a maximal collinear point, add segment
                if (freq >= 3)
                    segmentList.add(new LineSegment(pivot, pointsCPY[endPoint]));
            }
        }
        lineSegments = segmentList.toArray(new LineSegment[0]);
        numberOfSegments = segmentList.size();
    }

    public LineSegment[] segments() {
        return Arrays.copyOf(lineSegments, lineSegments.length);
    }

    public int numberOfSegments() {
        return numberOfSegments;
    }

    private boolean isNull(Point[] points) {
        if (points == null) return true;
        for (Point point : points) {
            if (point == null) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        int n = StdIn.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = StdIn.readInt();
            int y = StdIn.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        Stopwatch timer = new Stopwatch();
        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
        double time = timer.elapsedTime();
        StdOut.println("Number of segments: " + collinear.numberOfSegments);
        StdOut.printf("\nElapsed time : (%.2f seconds)\n", time);
    }
}
