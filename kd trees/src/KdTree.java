import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;


public class KdTree {
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
        root = insert(root, p, rect, Orientation.VERTICAL);
    }

    private Node insert(Node node, Point2D point, RectHV rect, Orientation orientation) {
        if (node == null) {
            size += 1;
            return new Node(point, rect);
        }
        if (point.equals(node.point))   return node;

        boolean isLess = isLess(orientation, point, node.point);
        rect = assignRectHV(orientation, isLess, node);

        if (isLess) {
            node.left = insert(node.left, point, rect, orientation.other());
        } else {
            node.right = insert(node.right, point, rect, orientation.other());
        }
        return node;
    }

    private boolean isLess(Orientation orientation, Point2D queryPoint, Point2D p) {
        int cmp;
        if (orientation == Orientation.VERTICAL)
            cmp = Double.compare(queryPoint.x(), p.x());
        else
            cmp = Double.compare(queryPoint.y(), p.y());
        return cmp < 0;
    }

    private RectHV assignRectHV(Orientation orientation, boolean isLess, Node node) {
        // instantiate a RectHV data type if it is leaf node otherwise use the cached instance
        RectHV rect;
        if (isLess && node.left == null)
            rect = instantiateRectHV(orientation, isLess, node);
        else if (isLess)
            rect = node.left.rect;
        else if (node.right == null)
            rect = instantiateRectHV(orientation, isLess, node);
        else
            rect = node.right.rect;
        return rect;
    }

    private RectHV instantiateRectHV(Orientation orientation, boolean isLess, Node node) {
        RectHV rect = node.rect;
        Point2D point = node.point;
        RectHV newInstance;

        if (isLess) {
            newInstance = switch(orientation) {
                case VERTICAL -> new RectHV(rect.xmin(), rect.ymin(), point.x(), rect.ymax());
                case HORIZONTAL -> new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), point.y());
            };
        } else {
            newInstance = switch (orientation) {
                case VERTICAL -> new RectHV(point.x(), rect.ymin(), rect.xmax(), rect.ymax());
                case HORIZONTAL -> new RectHV(rect.xmin(), point.y(), rect.xmax(), rect.ymax());
            };
        }

        return newInstance;
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Null value was supplied");

        Node x = root;
        return contains(x, p, Orientation.VERTICAL);
    }
    private boolean contains(Node node, Point2D p, Orientation orientation) {
        boolean result;

        if (node == null)   return false;           // exit condition
        if (p.equals(node.point))   return true;    // exit condition

        // traverse the tree if the above conditions fail
        boolean isLess = isLess(orientation, p, node.point);
        if (isLess)        result = contains(node.left, p, orientation.other());
        else                result = contains(node.right, p, orientation.other());

        return result;
    }
    public void draw() {
        draw(root, Orientation.VERTICAL);
    }

    private void draw(Node node, Orientation orientation) {
        if (node == null)   return;     // exit condition

        drawPoint(node.point);
        drawSplittingLine(node, orientation);
        draw(node.left, orientation.other());
        draw(node.right, orientation.other());
    }

    private void drawPoint(Point2D point) {
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        point.draw();
    }

    private void drawSplittingLine(Node node, Orientation orientation) {
        StdDraw.setPenRadius();
        if (orientation == Orientation.VERTICAL) {
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
        range(root, Orientation.VERTICAL, rect, points);
        return points;
    }

    private void range(Node node, Orientation orientation,
                       RectHV queryRect, Queue<Point2D> points) {
        if (node == null)   return;     // exit condition

        // populate queue with valid points
        if (queryRect.contains(node.point))    points.enqueue(node.point);

        int cmpMinPoint;
        int cmpMaxPoint;
        if (orientation == Orientation.VERTICAL) {
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
            range(node.left, orientation.other(), queryRect, points);   // left subtree traversal
        if (cmpMaxPoint >= 0)
            range(node.right, orientation.other(), queryRect, points);   // right subtree traversal
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Null value was supplied");

        double minDistance = Double.POSITIVE_INFINITY;
        return nearest(minDistance, p, null, root, Orientation.VERTICAL);
    }

    private Point2D nearest(double distance, Point2D p, Point2D champion,
                            Node node, Orientation orientation) {
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
            champion = nearest(minDistance, p, champion, node.left, orientation.other());
            minDistance = champion.distanceSquaredTo(p);

            /* * after exploring left subtree, explore right subtree
            * if the distance from query point to champion is greater than
            * the distance from the query point to rectangle enclosing
            * the right subtree
            * */
            if (node.right != null
                    && minDistance > node.right.rect.distanceSquaredTo(p))
                champion = nearest(minDistance, p, champion, node.right, orientation.other());
        } else {
            champion = nearest(minDistance, p, champion, node.right, orientation.other());
            minDistance = champion.distanceSquaredTo(p);

            /* * after exploring right subtree, explore left subtree
             * if the distance from query point to champion is greater than
             * the distance from the query point to rectangle enclosing
             * the left subtree
             * */
            if (node.left != null
                    && minDistance > node.left.rect.distanceSquaredTo(p))
                champion = nearest(minDistance, p, champion, node.left, orientation.other());
        }
        return champion;
    }

    private static class Node {
        private final Point2D point;
        private final RectHV rect;    // rectangle enclosing the point
        private Node left;        // left node or bottom rectangle
        private Node right;        // right node or top rectangle

        Node(Point2D point, RectHV rect) {
            this.point = point;
            this.rect = rect;
        }
    }

    // a representation of the split line that partitions the tree
    private enum Orientation {
        VERTICAL, HORIZONTAL;
        Orientation other() {
            return (this.equals(VERTICAL)) ? HORIZONTAL : VERTICAL;
        }
    }

    public static void main(String[] args) {
        RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
        StdDraw.enableDoubleBuffering();
        // initialize the data structures from file
        In in = new In("C:\\Users\\HP\\Documents\\Cousera\\Algorithms Part II\\Test files\\kdtree\\circle10.txt");
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
