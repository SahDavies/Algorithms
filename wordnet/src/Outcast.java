import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class Outcast {
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null)
            throw new IllegalArgumentException("Null value supplied!");

        int max = 0;
        int[] distance = new int[nouns.length];

        // find the noun with the maximum distance from other nouns
        for (int i = 0; i < distance.length; i++) {
            // for each noun in nouns, sum its distance to nouns[i]
            for (String noun : nouns) {
                distance[i] += wordnet.distance(nouns[i], noun);
            }
            if (distance[i] > distance[max])    max = i;
        }

        return nouns[max];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
