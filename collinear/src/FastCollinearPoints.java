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
        int n = points.length;
        Point[] pointsCPY = Arrays.copyOf(points, n);
        ArrayList<LineSegment> segmentList = new ArrayList<>();

        // sort array by natural order
        Arrays.sort(pointsCPY);

        // throw an exception if the element in the array is repeated
        for (int i = 0; i < (pointsCPY.length - 1); i++) {
            if (pointsCPY[i].compareTo(pointsCPY[i + 1]) == 0)
                throw new IllegalArgumentException("There is a problem" +
                        " with the points supplied (either there is repetition of point" +
                        " or the point is a reference to null)");
        }

        // pick a distinct pivot for each iteration
        for (int i = 0; i < n; i++) {
            Arrays.sort(pointsCPY);
            Point pivot = pointsCPY[i];
            Arrays.sort(pointsCPY, pivot.slopeOrder());

            // pick a second point and form a subsegment with pivot
            for (int j = 1; j < n; j++) {
                Point secondPT = pointsCPY[j];

                // evaluate slope and store in slope1
                double slope1 = pivot.slopeTo(secondPT);

                // find a third point collinear with, form a subsegment with pivot
                while (++j < n) {
                    boolean flag = false;   // is true if 4 collinear point has been found
                    Point endPoint = pointsCPY[j];
                    double slope2 = pivot.slopeTo(endPoint);

                    if (slope1 != slope2) {
                        j--;
                        break;
                    } else {
                        if (++j == n) break;
                        endPoint = pointsCPY[j];
                        slope2 = pivot.slopeTo(endPoint);
                    }
                    // find a fourth or more points collinear with pivot
                    while (slope1 == slope2) {
                        if (pivot.compareTo(secondPT) < 0) flag = true;
                        if (++j == n) break;
                        endPoint = pointsCPY[j];
                        slope2 = pivot.slopeTo(endPoint);
                    }
                    endPoint = pointsCPY[--j];

                    // add new line segment
                    if (flag) {
                        segmentList.add(new LineSegment(pivot, endPoint));
                        break;
                    }
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

        // print and draw the line segments
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
