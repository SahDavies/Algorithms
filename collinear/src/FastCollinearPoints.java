/* *
 * what is a collinear points?
 * Answer - These are points that lie on the same line
 * When is a point said to be collinear with a two other point?
 * Answer - Let the three points be A,B,C. C is collinear with A and B if slope(AB) == slope(AC)
 * If the points A,B,C,D are collinear, with line segment
 * A->B->c->D, how do you avoid repeating the same segment e.g B->A->C->D?
 * Answer - Each point from the left endpoint to the right endpoint of the segment has to be in ascending order,
 * always test for this before proceeding.
 *
 * */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private final LineSegment[] lineSegments;
    private final int numberOfSegments;

    public FastCollinearPoints(Point[] points) {
        // throw an exception if the argument is null
        if (isNull(points))
            throw new IllegalArgumentException("You have not supplied any point; " +
                    "The array is either empty or some element points to null ");

        // local variable declarations
        int size = points.length;
        Point[] pointsCPY = Arrays.copyOf(points, size);
        double[] slopes = new double[size];
        ArrayList<LineSegment> segmentList = new ArrayList<>();

        // sort array by natural order
        Arrays.sort(pointsCPY);

        // throw an exception if the element in the array is repeated
        for (int i = 0; i < (pointsCPY.length - 1); i++) {
            if (pointsCPY[i].compareTo(pointsCPY[i + 1]) == 0)
                throw new IllegalArgumentException("There is a problem" +
                        " with the points supplied (either there is duplicate points" +
                        " or null reference)");
        }

        // outer loop picks a distinct pivot for each iteration
        for (int i = 0; i < size; i++) {
            Arrays.sort(pointsCPY);
            Point pivot = pointsCPY[i];
            Arrays.sort(pointsCPY, pivot.slopeOrder());

            // Build the array of slopes
            for (int j = 0; j < size; j++) {
                slopes[j] = pivot.slopeTo(pointsCPY[j]);
            }

            // find points collinear with pivot
            for (int start = 0, index = 0; index < size; index++) {

                if (slopes[start] != slopes[index]) {
                    boolean maximalCollinearPoint = index - start >= 3;
                    // add line segment only if it has found the maximal collinear point
                    if (maximalCollinearPoint && pivot.compareTo(pointsCPY[start]) < 0) {
                        segmentList.add(new LineSegment(pivot, pointsCPY[index-1]));
                    }
                    start = index;
                }
            }
        }

        lineSegments = segmentList.toArray(new LineSegment[0]);
        numberOfSegments = segmentList.size();
    }

    // test client
    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
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

        // draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
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

    // returns the number of line segment
    public int numberOfSegments() {
        return numberOfSegments;
    }

    public LineSegment[] segments() {
        return Arrays.copyOf(lineSegments, lineSegments.length);
    }
}
