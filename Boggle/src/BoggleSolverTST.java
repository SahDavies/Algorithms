import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

/* Boggle solver using ternary search trie */
public class BoggleSolverTST {
    private static class Node {
        private boolean isWord;
        private char c;
        private Node left, mid, right;
    }

    private Node root;
    private char[][] _board;
    private boolean[][] marked;
    private int rows;
    private int cols;

    public BoggleSolverTST(String[] dictionary) {
        if (dictionary == null || dictionary.length == 0)
            throw new IllegalArgumentException("Empty constructor argument");

        for (String word : dictionary) { addToTrie(word); }
    }

    private void addToTrie(String word) {
        root = put(root, word, 0);
    }

    private Node put(Node x, String key, int d) {
        char c = key.charAt(d);
        if (x == null)      { x = new Node(); x.c = c; }
        if (c < x.c)        x.left = put(x.left, key, d);
        else if (c > x.c)   x.right = put(x.right, key, d);
        else if (d < key.length() - 1)  x.mid = put(x.mid, key, d+1);
        else                x.isWord = true;
        return x;
    }

    private boolean contains(String word) { return _Contains(root, word, 0); }

    private boolean _Contains(Node x, String key, int d) {
        if (x == null) return false;
        char c = key.charAt(d);

        if (c < x.c)        return _Contains(x.left, key, d);
        else if (c > x.c)   return _Contains(x.right, key, d);
        else if (d < key.length() - 1)  return _Contains(x.mid, key, d+1);
        else return x.isWord;
    }

    public Iterable<String> keys() {
        Queue<String> queue = new Queue<>();
        collect(root, "", queue);
        return queue;
    }

    private void collect(Node x, String prefix, Queue<String> q) {
        if (x == null) return;
        collect(x.left, prefix, q);
        if (x.isWord) q.enqueue(prefix);
        collect(x.mid, prefix + x.c, q);
        collect(x.right, prefix, q);
    }

    // Returns the set of all valid words in the given Boggle board, as Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null)
            throw new IllegalArgumentException("Null reference");

        // initialise fields
        rows = board.rows();
        cols = board.cols();
        _board = new char[rows][cols];
        marked = new boolean[rows][cols];

        // populate internal data structure _board
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                _board[i][j] = board.getLetter(i, j);
            }

        SET<String> words = new SET<>();
        // call DFS on each cell on the board
        for (int cell = 0; cell < rows * cols; cell++) {
            DFS(cell, words, root, "");
        }
        return words;
    }

    private void DFS(int index, SET<String> words, Node x, String prefix) {
        int i = index / cols;
        int j = index % cols;
        char c = _board[i][j];

        // find the node that matches c in the data structure and update x to reference that node
        while(x != null && c != x.c) {
            if (c < x.c)    x = x.left;
            else            x = x.right;
        }
        // if no match
        if (x == null) return;  // backtrack

        // handling the unit QU
        if (c == 'Q' && x.mid != null && x.mid.c == 'U' ) {
            prefix = prefix + "QU";
            x = x.mid;
        } else prefix = prefix + c;

        if (prefix.length() > 2 && x.isWord)
            words.add(prefix);

        marked[i][j] = true;   // mark as visited
        // data used to visit the neighbors of a cell
        final int[] shift_I = { -1, -1, -1, 0, 0, 0, 1, 1, 1};
        final int[] shift_J = { -1, 0, 1 ,-1, 0, 1, -1, 0, 1};

        for (int k = 0; k < shift_I.length; k++) {
            int _i = i + shift_I[k];
            int _j = j + shift_J[k];
            if (inRange(_i, _j) && !marked[_i][_j]) {
                int cell = _i*cols + _j;
                DFS(cell, words, x.mid, prefix);
            }
        }
        marked[i][j] = false;   // undo marking
    }

    private boolean inRange(int x, int y) {
        return (x < rows && x >= 0) && (y < cols && y >= 0);
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null)
            throw new IllegalArgumentException("Null reference");

        if (!contains(word)) return 0;
        if (word.length() < 3) return 0;
        if (word.length() < 5) return 1;
        if (word.length() == 5) return 2;
        if (word.length() == 6) return 3;
        if (word.length() == 7) return 5;
        return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolverTST solver = new BoggleSolverTST(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);

        /*int count = 0, newLine = 0;
        // print dictionary content
        for (String word: solver.keys()) {
            if (word.matches("[A-Z]")) {
                newLine = 0;
                System.out.println();
                System.out.println(word);
            } else {
                StdOut.printf("%20s", word);
                StdOut.printf("\t");
            }
            if (newLine % 3 == 0) { StdOut.println(); }
            count++;
            newLine++;
        }
        StdOut.println();
        StdOut.println("Word count in dictionary data structure: " + count);
        StdOut.println("Word count in dictionary file: " + dictionary.length);

        System.out.println(board.toString());
        System.out.println();*/

        // solve board
        int scores = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            int score = solver.scoreOf(word);
            System.out.println(score);
        }
        StdOut.println("Score = " + scores);
    }
}
