import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final SearchNode solution;
    private final boolean isSolvable;
    private final Stack<Board> stack;

    // A record
    private static class SearchNode implements Comparable<SearchNode> {
        final Board board;            // current board
        final int moves;              // number of moves made to reach current board
        final SearchNode previous;    // previous search node
        private final int priority;

        // initialise object's state
        SearchNode(Board board, int moves, SearchNode previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
            priority = moves + board.manhattan();
        }

        @Override
        public int compareTo(SearchNode that) {
            return Integer.compare(this.priority, that.priority);
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("Error! You did not supply a valid argument");
        // instantiate a SearchNode
        SearchNode node = new SearchNode(initial, 0, null);
        SearchNode twinNode = new SearchNode(initial.twin(), 0, null);

        // create priority queue instances
        MinPQ<SearchNode> nodePQ = new MinPQ<>();
        MinPQ<SearchNode> twinPQ = new MinPQ<>();

        // add node to priority queue
        nodePQ.insert(node);
        twinPQ.insert(twinNode);

        while (true) {
            // break loop when solution has been found
            if (node.board.isGoal()) {
                solution = node;
                isSolvable = true;
                break;
            }
            if (twinNode.board.isGoal()) {
                solution = null;
                isSolvable = false;
                break;
            }
            // add new search nodes to the priority queue
            addSearchNode(node, nodePQ);
            addSearchNode(twinNode, twinPQ);

            // update break condition; delete search node with minimum priority
            node = nodePQ.delMin();
            twinNode = twinPQ.delMin();
        }   // end of while block
        stack = solution(isSolvable, solution);
    }
    // adds new search node to priority queue
    private static void addSearchNode(SearchNode node, MinPQ<SearchNode> minPQ) {
        boolean isRepeated;
        for (Board board:
                node.board.neighbors()) {
            /* *
             * Optimisation: do not add unnecessary search nodes;
             * check that neighboring boards doesn't correspond to a previous board
             * */
            // check whether previous node points to null
            if (node.previous == null) isRepeated = false;
            else isRepeated = board.equals(node.previous.board);
            if (!isRepeated) minPQ.insert(new SearchNode(board, node.moves + 1, node));
        }
    }

    // check if the initial board is solvable
    public boolean isSolvable() {
        return isSolvable;
    }

    // returns minimum number of moves to solve initial board; returns -1 if unsolvable
    public int moves() {
        if (isSolvable)
            return solution.moves;

        return -1;
    }

    // sequence of boards in the shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return stack;
    }

    private static Stack<Board> solution( boolean isSolvable, SearchNode solution) {
        if (!isSolvable)    return null;

        SearchNode node = solution;
        Stack<Board>stack = new Stack<>();

        while (node != null) {
            stack.push(node.board);
            node = node.previous;
        }
        return stack;
    }
    public static void main(String[] args) {
        // int n = StdIn.readInt();
        int[][] tiles = {{0,1,3}, {4,2,5}, {7,8,6}};    //new int[n][n];
        /*for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                tiles[row][col] = StdIn.readInt();
            }
        }*/
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
