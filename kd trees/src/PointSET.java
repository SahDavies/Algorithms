import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;

public class PointSET {
    private final SET<Point2D> pointSet;

    public PointSET() {
        pointSet = new SET<>();
    }

    public boolean isEmpty() {
        return pointSet.isEmpty();
    }

    public int size() {
        return pointSet.size();
    }

    public void insert(Point2D p)   {
        pointSet.add(p);
    }

    public boolean contains(Point2D p) {
        return pointSet.contains(p);
    }

    public void draw() {
        if (pointSet.isEmpty())
            return;

        for (Point2D point:
             pointSet) {
            point.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("Argument supplied is null");

        Stack<Point2D> pointStack = new Stack<>();
        for (Point2D point:
             pointSet) {
            if (rect.contains(point))   pointStack.push(point);
        }
        return pointStack;
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Argument supplied is null");

        Point2D nearestPoint = null;
        double minDistance = Double.POSITIVE_INFINITY;
        double distance;

        for (Point2D point:
             pointSet) {

            distance = p.distanceSquaredTo(point);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint = point;   // save the point that gave the minimum distance
            }
        }
        return nearestPoint;
    }

    public static void main(String[] args) {
        // your client code goes here
    }
}
