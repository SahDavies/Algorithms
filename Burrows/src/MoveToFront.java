import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;   // alphabet radix
    public static void encode() {
        char[] ascii = new char[R];
        // populate ascii array
        for (int i = 0; i < R; i++) { ascii[i] = (char) i; }

        // read and process input
        while(!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            char hold = ascii[0];
            for (int i = 0; i < R; i++) {
                if (c == ascii[i]) {
                    ascii[0] = c;
                    ascii[i] = hold;
                    BinaryStdOut.write((char) i);
                    break;
                }

                char temp = ascii[i];
                ascii[i] = hold;
                hold = temp;
            }
        }
        BinaryStdOut.close();
    }

    public static void decode() {
        char[] ascii = new char[R];
        // populate ascii array
        for (int i = 0; i < R; i++) { ascii[i] = (char) i; }

        // read and process input
        while(!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            char hold = ascii[0];
            BinaryStdOut.write(ascii[index]);

            for (int i = 0; i < R; i++) {
                if (i == index) {
                    ascii[0] = ascii[index];
                    ascii[index] = hold;
                    break;
                }
                char temp = ascii[i];
                ascii[i] = hold;
                hold = temp;
            }
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if (args[0] == "-") encode();
        if (args[0] == "+") decode();
    }
}
