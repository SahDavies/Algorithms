import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.ST;


public class DeluxeWordNet {
    private Digraph G;                    // Directed Acyclic Graph
    private ST<String, SET<Integer>> st;    // a collection of nouns along with the synsets they each appear in
    private String[] nouns;                 // Array holding the entire data to be processed


    // constructor takes the name of the two input files
    public DeluxeWordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("Null value supplied!");

        populateDigraph(hypernyms);        // builds G from hypernym file

        if (!isRooted(G))
            throw new IllegalArgumentException("Graph supplied is not a rooted digraph.");

        cacheSynsetData(synsets);
        buildST();
    }

    private boolean isRooted(Digraph dag) {
        for (int v = 0; v < G.V(); v++) {
            if (G.outdegree(v) == 0) return true;
        }
        return false;
    }

    private void populateDigraph(String hypernyms) {
        In input = new In(hypernyms);
        String[] lines = input.readAllLines();
        G = new Digraph(lines.length);

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
    }

    private void cacheSynsetData(String synsets) {
        nouns = new String[G.V()];
        In input = new In(synsets);

        // populate nouns array with the second value in the CSV file that holds the nouns
        for (int i = 0; input.hasNextLine(); i++) {
            String[] token = input.readLine().split(",");
            nouns[i] = token[1];
        }
    }

    private void buildST() {
        st = new ST<>();
        int id = 0;
        // for each noun, split its substrings
        for (String noun : nouns) {
            String[] tokens = noun.split(" ");
            addNoun(tokens, id);
            id++;
        }
    }

    private void addNoun(String[] tokens, int id) {
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

        AncestralPath sap = new AncestralPath(G);
        SET<Integer> setA = st.get(nounA);
        SET<Integer> setB = st.get(nounB);
        return sap.length(setA, setB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("Null value supplied");

        AncestralPath sap = new AncestralPath(G);
        SET<Integer> setA = st.get(nounA);
        SET<Integer> setB = st.get(nounB);
        int result = sap.ancestor(setA, setB);
        return nouns[result];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // test client goes here
    }
}
