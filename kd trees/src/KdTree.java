import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;


public class KdTree {
    private static final boolean VERTICAL = true;  // the split line that divides a rectangle into two.
    private Node root;
    private int size;

    public KdTree() {
        root = null;
        size = 0;
    }
    public boolean isEmpty() { return root == null; }

    public int size() { return size; }

    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Null value was supplied");

        RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
        root = insert(root, p, rect, VERTICAL);
    }

    private Node insert(Node node, Point2D point, RectHV rect, boolean orientation) {
        if (node == null) {
            size += 1;
            return new Node(point, rect);
        }
        if (point.equals(node.point))   return node;

        boolean isLess = isLess(orientation, point, node.point);
        rect = assignRectHV(orientation, isLess, node);

        if (isLess) {
            node.lb = insert(node.lb, point, rect, !orientation);
        } else {
            node.rt = insert(node.rt, point, rect, !orientation);
        }
        return node;
    }

    private boolean isLess(boolean orientation, Point2D queryPoint, Point2D p) {
        int cmp;
        if (orientation == VERTICAL)
            cmp = Double.compare(queryPoint.x(), p.x());
        else
            cmp = Double.compare(queryPoint.y(), p.y());
        return cmp < 0;
    }

    private RectHV assignRectHV(boolean orientation, boolean isLess, Node node) {
        // instantiate a RectHV data type if subtree link is null otherwise use the cached instance
        RectHV rect;
        if (isLess && node.lb == null)
            rect = instantiateRectHV(orientation, isLess, node);
        else if (isLess)
            rect = node.lb.rect;
        else if (node.rt == null)
            rect = instantiateRectHV(orientation, isLess, node);
        else
            rect = node.rt.rect;
        return rect;
    }

    private RectHV instantiateRectHV(boolean orientation, boolean isLess, Node node) {
        RectHV rect = node.rect;
        Point2D point = node.point;
        RectHV newInstance;

        if (orientation == VERTICAL && isLess) {
            newInstance = new RectHV(rect.xmin(),
                    rect.ymin(),
                    point.x(),
                    rect.ymax());
        }
        else if (orientation == VERTICAL && !isLess) {
            newInstance = new RectHV(point.x(),
                    rect.ymin(),
                    rect.xmax(),
                    rect.ymax());
        }
        else if (orientation == !VERTICAL && isLess) {
            newInstance = new RectHV(rect.xmin(),
                    rect.ymin(),
                    rect.xmax(),
                    point.y());
        }
        else {
            newInstance = new RectHV(rect.xmin(),
                    point.y(),
                    rect.xmax(),
                    rect.ymax());
        }
        return newInstance;
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Null value was supplied");

        Node x = root;
        return contains(x, p, VERTICAL);
    }
    private boolean contains(Node node, Point2D p, boolean orientation) {
        boolean result;

        if (node == null)   return false;           // exit condition
        if (p.equals(node.point))   return true;    // exit condition

        // traverse the tree if the above conditions fail
        boolean isLess = isLess(orientation, p, node.point);
        if (isLess)        result = contains(node.lb, p, !orientation);
        else                result = contains(node.rt, p, !orientation);

        return result;
    }
    public void draw() {
        draw(root, VERTICAL);
    }

    private void draw(Node node, boolean orientation) {
        if (node == null)   return;     // exit condition

        drawPoint(node.point);
        drawSplittingLine(node, orientation);
        draw(node.lb, !orientation);
        draw(node.rt, !orientation);
    }

    private void drawPoint(Point2D point) {
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        point.draw();
    }

    private void drawSplittingLine(Node node, boolean orientation) {
        StdDraw.setPenRadius();
        if (orientation == VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.point.x(), node.rect.ymin(),
                         node.point.x(), node.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.point.y(),
                         node.rect.xmax(), node.point.y());
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("Null value was supplied");

        Queue<Point2D> points = new Queue<>();
        range(root, VERTICAL, rect, points);
        return points;
    }

    private void range(Node node, boolean orientation,
                       RectHV queryRect, Queue<Point2D> points) {
        if (node == null)   return;     // exit condition

        // populate queue with valid points
        if (queryRect.contains(node.point))    points.enqueue(node.point);

        int cmpMinPoint;
        int cmpMaxPoint;
        if (orientation == VERTICAL) {
            cmpMinPoint = Double.compare(queryRect.xmin(), node.point.x());
            cmpMaxPoint = Double.compare(queryRect.xmax(), node.point.x());
        } else {
            cmpMinPoint = Double.compare(queryRect.ymin(), node.point.y());
            cmpMaxPoint = Double.compare(queryRect.ymax(), node.point.y());
        }
        /* *
         * Recursively traverse the tree.
         * Search both left and right subtree if query rectangle
         * intersects the rectangle at the current node. Otherwise,
         * search only the left subtree or only the right subtree
         * */
        if (cmpMinPoint < 0)
            range(node.lb, !orientation, queryRect, points);   // left subtree traversal
        if (cmpMaxPoint >= 0)
            range(node.rt, !orientation, queryRect, points);   // right subtree traversal
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Null value was supplied");

        double minDistance = Double.POSITIVE_INFINITY;
        return nearest(minDistance, p, null, root, VERTICAL);
    }

    private Point2D nearest(double distance, Point2D p, Point2D champion,
                            Node node, boolean orientation) {
        if (node == null) return champion;  // exit condition
        boolean isLess = isLess(orientation, p, node.point);
        double minDistance = distance;
        distance = p.distanceSquaredTo(node.point);

        if (distance < minDistance) {
            minDistance = distance;
            champion = node.point;
        }

        // explore subtrees closer to the query point
        if (isLess) {
            champion = nearest(minDistance, p, champion, node.lb, !orientation);
            minDistance = champion.distanceSquaredTo(p);

            /* * after exploring left subtree, explore right subtree
            * if the distance from query point to champion is greater than
            * the distance from the query point to rectangle enclosing
            * the right subtree
            * */
            if (node.rt != null
                    && minDistance > node.rt.rect.distanceSquaredTo(p))
                champion = nearest(minDistance, p, champion, node.rt, !orientation);
        } else {
            champion = nearest(minDistance, p, champion, node.rt, !orientation);
            minDistance = champion.distanceSquaredTo(p);

            /* * after exploring right subtree, explore left subtree
             * if the distance from query point to champion is greater than
             * the distance from the query point to rectangle enclosing
             * the left subtree
             * */
            if (node.lb != null
                    && minDistance > node.lb.rect.distanceSquaredTo(p))
                champion = nearest(minDistance, p, champion, node.lb, !orientation);
        }
        return champion;
    }

    private static class Node {
        private final Point2D point;
        private final RectHV rect;    // rectangle enclosing the point
        private Node lb;        // left node or bottom rectangle
        private Node rt;        // right node or top rectangle

        Node(Point2D point, RectHV rect) {
            this.point = point;
            this.rect = rect;
        }
    }

    public static void main(String[] args) {
        RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
        StdDraw.enableDoubleBuffering();
        // initialize the data structures from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            if (rect.contains(p)) {
                StdOut.printf("%8.6f %8.6f\n", x, y);
            }
        }
        kdtree.draw();
        StdDraw.show();
    }
}
