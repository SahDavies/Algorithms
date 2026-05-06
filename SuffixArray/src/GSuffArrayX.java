import edu.princeton.cs.algs4.Quick3string;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This implementation uses the bit packing strategy which significantly
 * improves performance unlike the GSuffArray that uses binary search and an offset.
 * By bit-packing the textId and the posInText into a single int (or long),
 * we transform that binary search into a few bitwise instructions,
 * which is effectively $O(1)$.
 * */
public class GSuffArrayX {
    // Bit-packing constants
    private static final int BIT_SHIFT = 32;
    private static final long POS_MASK = 0xFFFFFFFFL;
    private static final int CUTOFF = 5;   // cutoff to insertion sort
    private final int m;               // number of input texts
    private final long[] index;         // flattened suffix indices
    private final char[][] texts;

    public GSuffArrayX(String... inputTexts) {
        this.m = inputTexts.length;
        this.texts = new char[m][];
        int totalChars = 0;

        for (int t = 0; t < m; t++) {
            this.texts[t] = (inputTexts[t].toLowerCase() + '\0').toCharArray();
            totalChars += texts[t].length;
        }

        this.index = new long[totalChars];
        int cursor = 0;
        for (int t = 0; t < m; t++) {
            for (int p = 0; p < texts[t].length; p++) {
                // High 32 bits: text index | Low 32 bits: position
                index[cursor++] = ((long) t << BIT_SHIFT) | (p & POS_MASK);
            }
        }

        sort(0, totalChars - 1, 0);
    }

    /**
     * Find index of which text a flat code belongs to via offsets[].
     * Since offsets array contains a monotonic sequence, we use binary search
     * to return this index in O(log n) time.
     * */
    private int textId(long code) {
        return (int) (code >>> BIT_SHIFT);
    }

    private int posInText(long code) {
        return (int) (code & POS_MASK);
    }

    // 3-way string quicksort on index[lo..hi], comparing starting at d-th character
    private void sort(int lo, int hi, int d) {
        if (hi <= lo + CUTOFF) {
            insertion(lo, hi, d);
            return;
        }
        int lt = lo, gt = hi;
        long pivotCode = index[lo];
        int pi = textId(pivotCode), pj = posInText(pivotCode);
        char v = texts[pi][pj + d];

        int i = lo + 1;
        while (i <= gt) {
            long code = index[i];
            int ti = textId(code), tj = posInText(code);
            char c = texts[ti][tj + d];
            if (c < v)      exch(lt++, i++);
            else if (c > v) exch(i, gt--);
            else            i++;
        }

        sort(lo, lt - 1, d);
        if (v != '\0') sort(lt, gt, d + 1);
        sort(gt + 1, hi, d);
    }

    // insertion sort for small subarrays
    private void insertion(int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(index[j], index[j - 1], d); j--)
                exch(j, j - 1);
    }

    // compare two suffixes starting at d
    private boolean less(long code1, long code2, int d) {
        if (code1 == code2) return false;
        int t1 = textId(code1), p1 = posInText(code1) + d;
        int t2 = textId(code2), p2 = posInText(code2) + d;
        while (p1 < texts[t1].length && p2 < texts[t2].length) {
            if (texts[t1][p1] < texts[t2][p2]) return true;
            if (texts[t1][p1] > texts[t2][p2]) return false;
            p1++; p2++;
        }
        // if one runs out of chars, the longer original suffix is "greater"
        return code1 > code2;
    }

    // swap two entries in the index
    private void exch(int i, int j) {
        long tmp = index[i];
        index[i] = index[j];
        index[j] = tmp;
    }

    /**
     * Returns the i-th smallest suffix as a string.
     */
    public String select(int i) {
        if (i < 0 || i >= index.length) throw new IllegalArgumentException();
        long code = index[i];
        int t = textId(code);
        int p = posInText(code);
        return new String(texts[t], p, texts[t].length - p);
    }

    /**
     * Returns a pair of the i-th smallest suffix
     * and the source text that produced it as an array of two Strings.
     * 0 is the index of the suffix, 1 is the index of the source text.
     */
    public String[] pair(int i) {
        long code = index[i];
        int t = textId(code);
        return new String[] {select(i), new String(texts[t], 0, texts[t].length - 1)};
    }

    /**
     * Given a query prefix, returns an iterable of original texts that contain the given prefix.
     */
    public Iterable<String> texts(String query) {
        return mapIdsToTexts(indices(query));
    }
    public Iterable<String> permutationAgnosticSearch(String query) {
        String[] subTexts = query.split("\\s+");
        if (subTexts.length == 0) return Collections.emptySet();

        // 1) Seed the accumulator with the indices of the first word
        Set<Integer> accumulator = new HashSet<>(indices(subTexts[0]));

        // 2) Intersect with the indices of all remaining words
        for (int i = 1; i < subTexts.length; i++) {
            if (accumulator.isEmpty()) break; // Early exit if no common documents remain
            accumulator.retainAll(indices(subTexts[i]));
        }

        return mapIdsToTexts(accumulator);
    }

    public Set<Integer> indices(String query) {
        String queryLowerCase = query.toLowerCase();
        // find the range of suffixes that start with 'query'
        int start = rank(queryLowerCase);
        // use a high “sentinel” to find the upper bound
        String hiQuery = queryLowerCase + '\uffff';
        int end = rank(hiQuery);

        Set<Integer> set = new HashSet<>();

        // collect each distinct text ID in [start..end)
        for (int i = start; i < end; i++) {
            long code = index[i];
            int t = textId(code);
            set.add(t);
        }

        return set;
    }

    private Set<String> mapIdsToTexts(Set<Integer> ids) {
        Set<String> result = new HashSet<>();
        for (int id : ids) {
            char[] txt = texts[id];
            result.add(new String(txt, 0, txt.length - 1));
        }
        return result;
    }


    /**
     * Similar to binary search, rank returns the number of suffixes strictly less than the {@code query} string.
     * We note that {@code rank(select(i))} equals {@code i} for each {@code i}
     * between 0 and totalChars−1.
     * @param query the query string
     * @return the number of suffixes strictly less than {@code query}
     */
    public int rank(String query) {
        int lo = 0, hi = index.length - 1;
        String queryLowerCase = query.toLowerCase();
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            long code = index[mid];
            int cmp = compareSuffixToQuery(code, queryLowerCase);
            if (cmp < 0) {
                // suffix < query → go right
                lo = mid + 1;
            } else {
                // suffix ≥ query → go left
                hi = mid - 1;
            }
        }
        return lo;
    }

    // Compare the suffix encoded by `code` against `query`:
    //   <0 if suffix < query,
    //    0 if equal up through length(query),
    //   >0 if suffix > query
    private int compareSuffixToQuery(long code, String query) {
        int t = textId(code);
        int i = posInText(code), j = 0;
        char[] txt = texts[t];

        // walk both strings
        while (i < txt.length && j < query.length()) {
            char c1 = txt[i];
            char c2 = query.charAt(j);
            if (c1 != c2) return c1 - c2;
            i++; j++;
        }
        // if we ran out of the suffix (hit '\0'), or ran out of the query:
        if (i == txt.length) {
            // suffix ended (“\0”) before or exactly at query’s end → suffix ≤ query
            // but if query also ended, then equal up to query’s length ⇒ return 0
            return (j == query.length()) ? 0 : (txt[i-1] - query.charAt(j));
        }
        if (j == query.length()) {
            // query exhausted but suffix still has chars → suffix > query
            return 1;
        }
        // unreachable
        return 0;
    }


    /**
     * Returns the total number of suffixes.
     */
    public int length() {
        return index.length;
    }

    public static void main(String[] args) {
        String s1 = "banana";
        String s2 = "apple";
        GSuffArray sa = new GSuffArray(s1, s2);
        int n = sa.length();

        // Collect all suffixes from both strings (with sentinel)
        String t1 = s1 + '\0';
        String t2 = s2 + '\0';
        List<String> all = new java.util.ArrayList<>();
        for (int i = 0; i < t1.length(); i++) all.add(t1.substring(i));
        for (int i = 0; i < t2.length(); i++) all.add(t2.substring(i));

        // Sort expected list
        String[] expected = all.toArray(new String[0]);
        Quick3string.sort(expected);

        System.out.printf("Total suffix count %s\n", (expected.length == n) ? "Match" : "Did not Match");

        boolean allMatch = true;
        for (int i = 0; i < n; i++) {
            if (!expected[i].equals(sa.select(i))) {
                System.out.println(String.format("Suffix #%d should be '%s'\n Found - %s", i, expected[i], sa.select(i)));
                allMatch = false;
            }
        }

        if (allMatch) {
            System.out.println("Suffix array was correctly implemented!");
            visualise(sa, expected);
        }
    }

    private static void visualise(GSuffArray sa, String[] expected) {
        for (int i = 0; i < expected.length; i++) {
            System.out.println("Expected: " + expected[i] + " ;" + "Suffix array: " + sa.select(i));
        }
    }
}
