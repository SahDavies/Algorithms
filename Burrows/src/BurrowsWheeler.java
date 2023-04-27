import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String file = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(file);

        int first = Integer.MIN_VALUE;
        int len = csa.length();
        for (int i = 0; i < len; i++) {
            if (csa.index(i) == 0) { first = i; break; }
        }
        BinaryStdOut.write(first);
        for (int i = 0; i < csa.length(); i++) {
            int lastChar = ((csa.index(i) + len) - 1) % len;
            BinaryStdOut.write(file.charAt(lastChar));
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        final int first = BinaryStdIn.readInt();
        String transform = BinaryStdIn.readString();
        int len = transform.length();
        int[] next = new int[len];
        int[] count = new int[R + 1];

        // key-indexed counting
        for (int i = 0; i < len; i++) {
            count[transform.codePointAt(i) + 1]++;
        }
        for (int i = 0; i < R; i++) {
            count[i+1] = count[i];
        }
        for (int i = 0; i < len; i++) {
            next[count[transform.codePointAt(i)]++] = i;
        }
        // write back the original file
        for (int i = next[first]; true; i = next[i]) {
            BinaryStdOut.write(transform.charAt(i));
            if (next[i] == next[first]) break;
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0] == "-") transform();
        if (args[0] == "+") inverseTransform();
    }
}
