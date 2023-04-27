import edu.princeton.cs.algs4.Quick3way;

public class CircularSuffixArray {
    private final int[] indices;
    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("Constructor argument cannot be null");

        indices = new int[s.length()];
        class CircularSuffix implements Comparable<CircularSuffix> {
            final int start;
            public CircularSuffix(int idx) {
                if (idx < 0 || idx >= s.length())
                    throw new IllegalArgumentException("Index " + idx + " is out of bound");

                start = idx;
            }
            public char charAt(int i) {
                if (i < 0 || i >= s.length())
                    throw new IllegalArgumentException("Index " + i + " is out of bound");
                return s.charAt((i + start) % s.length());
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
                    sb.append(s.charAt((i+start) % s.length()));
                }
                return sb.toString();
            }
        }

        CircularSuffix[] suffixes = new CircularSuffix[s.length()];
        // build the suffix array
        for (int i = 0; i < s.length(); i++) {
            suffixes[i] = new CircularSuffix(i);
        }
        // sort suffix array
        Quick3way.sort(suffixes);
        for (int i = 0; i < s.length(); i++) {
            indices[i] = suffixes[i].start;
        }
    }

    // length of s
    public int length() { return indices.length; }

    // returns index of ith sorted suffix
    public int index(int i) { return indices[i]; }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray cs = new CircularSuffixArray("ABRACADABRA!");
    }
}
