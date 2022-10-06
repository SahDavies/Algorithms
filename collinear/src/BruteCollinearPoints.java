import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

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

        // throw an exception if the element in the array is repeated
        for (int i = 0; i < (points.length - 1); i++) {
            if (points[i].compareTo(points[i + 1]) == 0)
                throw new IllegalArgumentException("There is a problem" +
                        " with the points supplied (either there is repetition of point" +
                        " or the point is a reference to null)");
        }

        // local variables declaration
        int n = points.length;
        Point[] pointsCPY = Arrays.copyOf(points, n);
        ArrayList<LineSegment> segmentList = new ArrayList<>();

        // sort the
        Arrays.sort(pointsCPY);

        // find all line segments with 4 points
        for (int i = 0; i < n; i++) {
            Point origin = pointsCPY[i];

            // first subsegment
            for (int j = 0; j < n; j++) {
                Point first = pointsCPY[j];

                if (origin.compareTo(first) >= 0)
                    continue;

                double slope1 = origin.slopeTo(first);

                // second subsegment
                for (int k = 0; k < n; k++) {
                    Point second = pointsCPY[k];

                    if (first.compareTo(second) >= 0)
                        continue;

                    double slope2 = origin.slopeTo(second);

                    if (slope1 == slope2) {
                        // third subsegment
                        for (Point third : pointsCPY) {
                            if (second.compareTo(third) >= 0)
                                continue;

                            double slope3 = origin.slopeTo(third);

                            if (slope1 == slope3)
                                segmentList.add(new LineSegment(origin, third));
                        }
                    }
                }
            }
        }
        lineSegments = segmentList.toArray(new LineSegment[0]);
        numberOfSegments = segmentList.size();
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
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

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

    public LineSegment[] segments() {
        return Arrays.copyOf(lineSegments, lineSegments.length);
    }

    public int numberOfSegments() {
        return numberOfSegments;
    }

    private boolean isNull(Point[] points) {
        if (points == null)
            return true;

        for (Point point : points) {
            if (point == null)
                return true;
        }

        return false;
    }
}
