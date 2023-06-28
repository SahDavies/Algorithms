import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class BoggleSolver {
    private static final int R = 26;    // Alphabets of the English language

    private Node root;
    private char[][] _board;
    private boolean[][] marked;
    private int rows;
    private int cols;

    private static class Node {
        private boolean isWord;
        final private Node[] next = new Node[R];
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null || dictionary.length == 0)
            throw new IllegalArgumentException("Empty constructor argument");

        root = new Node();
        for (String word : dictionary) { addToTrie(word); }
    }

    private void addToTrie(String word) {
        root = put(root, word, 0);
    }

    private static Node put(Node x, String key, int d) {
        if (x == null)  x = new Node();
        if (d == key.length())  { x.isWord = true; return x; }
        char c = key.charAt(d);
        x.next[c - 'A'] = put(x.next[c - 'A'], key,d + 1 );
        return x;
    }

    private boolean contains(String word) { return _contains(root, word, 0); }

    private boolean _contains(Node x, String key, int d) {
        if (x == null) return false;
        if (d == key.length()) return x.isWord;
        char c = key.charAt(d);
        return _contains(x.next[c - 'A'], key, d+1);
    }

    private Iterable<String> keys() {
        Queue<String> queue = new Queue<>();
        collect(root, "", queue);
        return queue;
    }

    private void collect(Node x, String prefix, Queue<String> q) {
        if (x == null) return;
        if (x.isWord) q.enqueue(prefix);
        for (char c = 'A'; c <= 'Z'; c++)
            collect(x.next[c - 'A'], prefix + c, q);
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
        char offset = 'A';

        x = x.next[c - offset];
        if (x == null) return;  // backtrack
        else marked[i][j] = true;   // mark as visited

        if (c == 'Q' && (x.next['U' - offset] != null)) {
            prefix = prefix + "QU";
            x = x.next['U' - offset];
        } else prefix = prefix + c;

        if (prefix.length() > 2 && x.isWord)
            words.add(prefix);

        // data used to visit the neighbors of a cell
        final int[] shift_I = { -1, -1, -1, 0, 0, 0, 1, 1, 1};
        final int[] shift_J = { -1, 0, 1 ,-1, 0, 1, -1, 0, 1};

        for (int k = 0; k < shift_I.length; k++) {
            int _i = i + shift_I[k];
            int _j = j + shift_J[k];
            if (inRange(_i, _j) && !marked[_i][_j]) {
                int cell = _i*cols + _j;
                DFS(cell, words, x, prefix);
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
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int count = 0, newLine = 0;

        /*// print dictionary content
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
        StdOut.println("Word count in dictionary file: " + dictionary.length);*/

        System.out.println(board.toString());
        System.out.println();

        // solve board
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
