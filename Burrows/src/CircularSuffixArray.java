import edu.princeton.cs.algs4.Quick3way;

public class CircularSuffixArray {
    private final CircularSuffix[] suffixes;

    private static class CircularSuffix implements Comparable<CircularSuffix> {
        static String s = null;
        final int OFFSET;
        public CircularSuffix(int idx) {
            if (idx < 0 || idx >= s.length())
                throw new IllegalArgumentException("Index " + idx + " is out of bound");

            OFFSET = idx;
        }
        public char charAt(int i) {
            if (i < 0 || i >= s.length())
                throw new IllegalArgumentException("Index " + i + " is out of bound");
            return s.charAt((i + OFFSET) % s.length());
        }

        @Override
        public int compareTo(CircularSuffix other) {
            for (int i = 0; i < s.length(); i++) {
                if (this.charAt(i) < other.charAt(i)) return -1;
                if (this.charAt(i) > other.charAt(i)) return 1;
            }
            return 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                sb.append(s.charAt((i+ OFFSET) % s.length()));
            }
            return sb.toString();
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("Constructor argument cannot be null");

        CircularSuffix.s = s;
        suffixes = new CircularSuffix[s.length()];
        // build the suffix array
        for (int i = 0; i < s.length(); i++) {
            suffixes[i] = new CircularSuffix(i);
        }
        // sort suffix array
        Quick3way.sort(suffixes);
    }

    // length of s
    public int length() { return suffixes.length; }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= suffixes.length)
            throw new IllegalArgumentException("Index " + i + " is out of bound");
        return suffixes[i].OFFSET;
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray cs = new CircularSuffixArray("ABRACADABRA!");
        for (CircularSuffix s :
                cs.suffixes) {
            System.out.println(s.toString());
        }
    }
}
