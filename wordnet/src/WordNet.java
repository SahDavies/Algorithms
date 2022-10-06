import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.ST;


public class WordNet {
    private Digraph G;                              // Directed Acyclic Graph
    private final ST<String, SET<Integer>> st;    // the mapping of a noun to the synsets it appears in
    private final String[] nouns;                 // a collection of nouns indexed by their synset id
    private final SAP sap;                          // shortest ancestral path reference type


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("Null value supplied!");

        G = constructDigraph(hypernyms);        // builds G from hypernym file

        if (!isRooted(G))
            throw new IllegalArgumentException("Graph supplied is not a rooted digraph.");

        nouns = cacheSynsetData(synsets, G);
        st = buildST(nouns);
        sap = new SAP(G);
    }

    private boolean isRooted(Digraph digraph) {
        for (int v = 0; v < digraph.V(); v++) {
            if (digraph.outdegree(v) == 0) return true;
        }
        return false;
    }

    private static Digraph constructDigraph(String hypernyms) {
        In input = new In(hypernyms);
        String[] lines = input.readAllLines();
        Digraph G = new Digraph(lines.length);

        // outer loop selects a vertex
        for (String line : lines) {
            String[] tokens = line.split(",");
            int v = Integer.parseInt(tokens[0]);
            // inner loop selects adjacent vertices to v and adds edge
            for (int i = 1; i < tokens.length; i++) {
                int w = Integer.parseInt(tokens[i]);
                G.addEdge(v, w);
            }
        }
        input.close();
        return G;
    }

    private static String[] cacheSynsetData(String synsets, Digraph G) {
        String[] nouns = new String[G.V()];
        In input = new In(synsets);

        // populate nouns array with the second value in the CSV file that holds the nouns
        for (int i = 0; input.hasNextLine(); i++) {
            String[] token = input.readLine().split(",");
            nouns[i] = token[1];
        }
        input.close();
        return nouns;
    }

    private static ST<String, SET<Integer>> buildST(String[] nouns) {
        ST<String, SET<Integer>> st = new ST<>();
        int id = 0;
        // for each noun, split its substrings
        for (String noun : nouns) {
            String[] tokens = noun.split(" ");
            addNoun(tokens, id, st);
            id++;
        }
        return st;
    }

    private static void addNoun(String[] tokens, int id, ST<String, SET<Integer>> st) {
        // for each noun, add its associated synset identifiers to symbol table
        for (String noun : tokens) {
            if (!st.contains(noun)) {
                st.put(noun, new SET<>());
            }
            st.get(noun).add(id);
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() { return st.keys(); }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException("Null value supplied!");
        return st.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("Null value supplied");
        Iterable<Integer> setA = st.get(nounA);
        Iterable<Integer> setB = st.get(nounB);
        return sap.length(setA, setB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in the shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("Null value supplied");

        Iterable<Integer> setA = st.get(nounA);
        Iterable<Integer> setB = st.get(nounB);
        int id = sap.ancestor(setA, setB);
        return nouns[id];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // test client goes here
    }
}
