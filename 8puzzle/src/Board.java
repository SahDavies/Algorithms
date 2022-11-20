import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;

public class Board {
    private final int[][] tiles;        // tiles represented as integer
    private final int n;                // side length

    public Board(int[][] tiles) {
        if (tiles == null)
            throw new IllegalArgumentException("You supplied a null argument");

        if (tiles.length != tiles[0].length)
            throw new IllegalArgumentException("You supplied an invalid argument");

        n = tiles[0].length;
        this.tiles = clone(tiles);
    }

    // deep copy the constructor's argument
    private static int[][] clone(int[][] original) {
        int n = original[0].length;
        int[][] clone = new int[n][n];
        // manually copy the contents of the argument
        for (int i = 0; i < n; i++)
            System.arraycopy(original[i], 0, clone[i], 0, n);

        return clone;
    }

    // board dimension
    public int dimension() {
        return n;
    }

    // it returns the number of tiles out of place
    public int hamming() {
        int count = 0;  // counts the number of tiles out of place

        // check if each tile on the board is in place
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                // do not process the last tile, because all valid tiles have been processed
                if (row == (n-1) && col == (n-1))
                    break;

                int currentTile = tiles[row][col];
                int expectedTile = (row * n) + (col+1);
                if (currentTile != expectedTile)
                    count++;
            }
        }
        return count;
    }

    // returns the sum of distances between tiles and goal
    public int manhattan() {
        int sumOfDistances = 0;

        // calculate the distance to goal board for each tile and sums them up
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int currentTile = tiles[row][col];

                // exclude blank tile, it is not a valid tile
                if (currentTile == 0)
                    continue;

                sumOfDistances += distance(currentTile, row, col);
            }
        }
        return sumOfDistances;
    }

    // calculates the distance of each tile from its current position to its destination
    private int distance(int tile, int row, int col) {
        int destinationR;   // row
        int destinationC;   // column

        if (tile % n == 0) {
            destinationR = (tile / n) - 1;
            destinationC = n-1;
        } else {
            destinationR = tile / n;
            destinationC = (tile % n) - 1;
        }

        int changeInRow = Math.abs(row - destinationR);
        int changeInColumn = Math.abs(col - destinationC);

        return changeInRow + changeInColumn;
    }

    public boolean isGoal() {
        // check if each tile on the board is in place
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                // do not process the last cell, because all valid tiles have been processed
                if (row == (n-1) && col == (n-1))
                    break;

                int currentTile = tiles[row][col];
                int expectedTile = (row * n) + (col+1);
                if (currentTile != expectedTile)
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object y) {
        // check whether argument is null
        if (y == null)  return false;
        // check if both objects belong to the same class
        if (this.getClass() != y.getClass())
            return false;

        Board that = (Board) y;
        // check if they point to the same reference
        if (this == that)
            return true;

        // compare the fields of both objects
        if (this.n != that.n) return false;
        else {
            for (int row = 0; row < n; row++) {
                for (int col = 0; col < n; col++) {
                    int thisTile = this.tiles[row][col];
                    int thatTile = that.tiles[row][col];

                    // check if both boards have corresponding tiles
                    if (thisTile != thatTile)
                        return false;
                }
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        Stack<Board> neighbors = new Stack<>();     // a collection of neighboring boards
        // find the blank tile (zero) and add its neighbors to stack
        nested_loop:
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                // find blank tile (zero) in board
                if (this.tiles[row][col] == 0) {
                    addNeighbors(neighbors, row, col);
                    break nested_loop;
                }
            }   // end of inner for loop
        }   // end of outer for loop
        return neighbors;
    }
    private void addNeighbors(Stack<Board> neighbors, int row, int col) {
        // a minimum of two if statements (shifts) is executed
        if (row > 0) { shiftDown(neighbors, row, col); }
        if (col < (n-1)) { shiftLeft(neighbors, row, col); }
        if (row < (n-1)) { shiftUp(neighbors, row, col); }
        if (col > 0) { shiftRight(neighbors, row, col); }
    }

    // create new neighboring board by swapping blank tile with the one to its left
    private void shiftRight(Stack<Board> neighbors, int row, int col) {
        Board neighbor;
        neighbor = new Board(this.tiles);
        neighbor.tiles[row][col] = neighbor.tiles[row][col -1];
        neighbor.tiles[row][col -1] = 0;
        // add to stack
        neighbors.push(neighbor);
    }
    // create new neighboring board by swapping blank tile with the tile below
    private void shiftUp(Stack<Board> neighbors, int row, int col) {
        Board neighbor;
        neighbor = new Board(this.tiles);
        neighbor.tiles[row][col] = neighbor.tiles[row +1][col];
        neighbor.tiles[row +1][col] = 0;
        // add to stack
        neighbors.push(neighbor);
    }
    // create new neighboring board by swapping blank tile with the one to its right
    private void shiftLeft(Stack<Board> neighbors, int row, int col) {
        Board neighbor;
        neighbor = new Board(this.tiles);
        neighbor.tiles[row][col] = neighbor.tiles[row][col +1];
        neighbor.tiles[row][col +1] = 0;
        // add to stack
        neighbors.push(neighbor);
    }
    // create new neighboring board by swapping blank tile with tile above
    private void shiftDown(Stack<Board> neighbors, int row, int col) {
        Board neighbor;
        neighbor = new Board(this.tiles);
        neighbor.tiles[row][col] = neighbor.tiles[row -1][col];
        neighbor.tiles[row -1][col] = 0;
        // add to stack
        neighbors.push(neighbor);
    }

    /* *
     * returns a twin board; a board that is obtained from the
     * original board by exchanging any pair of valid tile
     * */
    public Board twin() {
        Board twin = new Board(this.tiles);

        // exchange the first two non-zero tiles
        nested_loop:
        for (int row = 0; row < n; row++)
            for (int col = 0; col < (n-1); col++) {
                if (twin.tiles[row][col] == 0 || twin.tiles[row][col+1] == 0)
                    continue;

                // exchange adjacent tiles
                int tempTile = twin.tiles[row][col];
                twin.tiles[row][col] = twin.tiles[row][col+1];
                twin.tiles[row][col+1] = tempTile;

                break nested_loop;
            }
        return twin;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n).append("\n");
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                s.append(String.format("%2d ", tiles[row][col]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {
        int length = StdIn.readInt();
        int[][] tiles = new int[length][length];
        for (int row = 0; row < length; row++) {
            for (int col = 0; col < length; col++) {
                tiles[row][col] = StdIn.readInt();
            }
        }

        Board board = new Board(tiles);
        System.out.println("8puzzle\nDimension: " + board.dimension() + " by " + board.dimension());
        System.out.println(board);
        System.out.printf("Is this the goal board?%nAnswer: %s%n%n", (board.isGoal()) ? "Yes" : "No");
        System.out.println("How far from goal board?\n");
        System.out.println("Hamming distance: " + board.hamming() + " tiles out of place");
        System.out.println("Manhattan distance: " + board.manhattan() +
                " moves in total is needed to reach goal board");

        System.out.println("\nNeighboring boards: \n");
        Iterable<Board> stack = board.neighbors();
        for (Board neighbor:
             stack) {
            System.out.printf("%s", neighbor);
        }

        Board twin = board.twin();
        System.out.print("\nTwin Board:\n");
        for (int i = 0; i < 3; i++) {
            System.out.printf("%s\n", twin.toString());
        }
        System.out.println("Is original board equal to twin board?\nAnswer: " +
                board.equals(twin));
    }
}
