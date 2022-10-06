import java.util.Arrays;

public class ReverseString {
    private char[] charArray;
    private boolean isReversed;

    public ReverseString(String s) {
        if (s == null)
            throw new IllegalArgumentException("Null value was supplied");

        charArray = s.toCharArray();
        isReversed = false;
    }

    public void reverseString() {
        reverseString(charArray, 0, charArray.length-1);
        isReversed = true;
    }
    public void undoReverse() {
        if (!isReversed) {
            System.out.println("Nothing to undo here");
        }

        reverseString(charArray, 0, charArray.length-1);
        isReversed = false;
    }

    private void reverseString(char[] chars, int lo, int hi) {
        if (hi <= lo) return;   // exit condition
        reverseString(chars, lo+1, hi-1);
        char temp = chars[hi];
        chars[hi] = chars[lo];
        chars[lo] = temp;
    }

    public void setString(String s) {
        if (s == null)
            throw new IllegalArgumentException("Null value was supplied");

        charArray = s.toCharArray();
        isReversed = false;
    }

    public void print() {
        String s = Arrays.toString(charArray);
        System.out.printf("State: %s, \tString: %s",
                (isReversed) ? "Reversed": "Original", s);
    }

    public static void main(String[] args) {

    }
}

/* *
 * Runtime of method reverseString: N/2
 * Optimization : Use loop instead of recursion
 * to save memory usage
 * */