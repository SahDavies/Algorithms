import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.SeparateChainingHashST;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseballElimination {
    private final int S;
    private final int T;
    private final int[] w, l, r;                 // wins, losses, remaining games
    private final int[][] g;                     // league fixtures (games)
    private final Map<String, Integer> teams;    // a mapping of team names to their integer values.
    private final SeparateChainingHashST<String, SET<String>> cache;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In input = new In(filename);
        final int N = input.readInt();

        // initialise fields
        cache = new SeparateChainingHashST<>(N);
        teams = new LinkedHashMap<>(N);
        w = new int[N];
        l = new int[N];
        r = new int[N];
        g = new int[N][N];
        S = getNumberOfVertices(N);
        T = S + 1;

        // process each line input
        int index = 0;
        while (!input.isEmpty()) {
            teams.put(input.readString(), index);
            w[index] = input.readInt();
            l[index] = input.readInt();
            r[index] = input.readInt();

            for (int j = 0; j < N; j++) {
                g[index][j] = input.readInt();
            }
            index++;        // update index
        }
        input.close();
    }

    // number of teams
    public int numberOfTeams() { return teams.size(); }

    // all teams
    public Iterable<String> teams() { return teams.keySet(); }

    // number of wins for given team
    public int wins(String team) { validate(team);      return w[teams.get(team)]; }

    // number of losses for given team
    public int losses(String team) { validate(team);    return l[teams.get(team)]; }

    // number of remaining games for given team
    public int remaining(String team) { validate(team); return r[teams.get(team)]; }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validate(team1);
        validate(team2);
        return g[teams.get(team1)][teams.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validate(team);
        // if this team's min cut has not been calculated
        if (!cache.contains(team)) cacheMinCutOf(team);
        return !cache.get(team).isEmpty();
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validate(team);
        // if this team's min cut has not been calculated
        if (!cache.contains(team)) cacheMinCutOf(team);

        SET<String> subSet = cache.get(team);
        if (subSet.isEmpty()) return null;
        return subSet;
    }

    private void cacheMinCutOf(String team) {
        SET<String> subSetR = trivialElimination(team);

        if (subSetR.isEmpty())
            subSetR = FordFulkerson(team);

        cache.put(team, subSetR);
    }

    private SET<String> trivialElimination(String team) {
        final int teamX = teams.get(team);
        final Iterable<String> keys = teams.keySet();
        int teamXWins = w[teamX] + r[teamX];

        SET<String> subSetR = new SET<>();
        int i = 0;
        for (String key : keys) {
            if (teamXWins < w[i++]) subSetR.add(key);
        }
        return subSetR;
    }

    private SET<String> FordFulkerson(String team) {
        final int teamX = teams.get(team);
        final Iterable<String> keys = teams.keySet();

        // construct FlowNetwork graph
        FlowNetwork G = constructGraph(teamX, teams.size());

        FordFulkerson maxFlow = new FordFulkerson(G, S, T);
        SET<String> subSetR = new SET<>();

        int i = 0;
        for (String key : keys) {
            if (maxFlow.inCut(i++)) subSetR.add(key);
        }
        return subSetR;
    }

    private FlowNetwork constructGraph(int teamX, int N) {
        int size = getNumberOfVertices(N) + 2;
        FlowNetwork G = new FlowNetwork(size);

        addEdgesFromSource(teamX, N, G);
        addEdgesFromGames(teamX, N, G);
        addEdgesFromTeams(teamX, N, G);
        return G;
    }

    private void addEdgesFromTeams(int teamX, int N, FlowNetwork G) {
        // create and add edges from each team to target, T
        for (int i = 0; i < N; i++) {
            // exclude team x
            if (i == teamX) continue;

            double edgeCapacity = w[teamX] + r[teamX] - w[i];
            FlowEdge e = new FlowEdge(i, T, edgeCapacity);
            G.addEdge(e);
        }
    }

    private void addEdgesFromGames(int teamX, int N, FlowNetwork G) {
        int v = N;      // start index of game vertex
        // create and add edges from each game vertex to opposing teams
        for (int row = 0; row < N; row++) {
            for (int col = row + 1; col < N; col++) {
                // exclude team x
                if (row == teamX) { row = row + 1; continue; }
                if (col == teamX) continue;

                int team1 = row, team2 = col;
                double edgeCapacity = Double.POSITIVE_INFINITY;

                FlowEdge e1 = new FlowEdge(v, team1, edgeCapacity);
                FlowEdge e2 = new FlowEdge(v, team2, edgeCapacity);
                G.addEdge(e1);
                G.addEdge(e2);

                v++;    // update vertex
            }
        }   // end of outer for loop
    }

    private void addEdgesFromSource(int teamX, int N, FlowNetwork G) {
        int v = N;      // start index of game vertex
        // create and add edges from S to each game vertex
        for (int row = 0; row < N; row++) {
            for (int col = row + 1; col < N; col++) {
                // exclude team x
                if (row == teamX) { row = row + 1; continue; }
                if (col == teamX) continue;

                int edgeCapacity = g[row][col];
                FlowEdge e = new FlowEdge(S, v++, edgeCapacity);
                G.addEdge(e);
            }
        }   // end of outer for loop
    }

    // all vertices excluding source and target
    private static int getNumberOfVertices(int numberOfTeams) {
        int numberOfGames = combination(numberOfTeams - 1);
        return numberOfGames + numberOfTeams;
    }

    private static int combination(int n) {
        return n * (n - 1) / 2;
    }

    private void validate(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException("Team " + team + " is not included.");
    }

    public static void main(String[] args) {
        BaseballElimination baseballElimination = new BaseballElimination("teams4.txt");
        certificateTest(baseballElimination);
    }

    private static void iterationOrderTest(BaseballElimination baseballElimination) {
        System.out.println();
        System.out.println("Iteration order :");
        for (String key : baseballElimination.teams.keySet()) {
            System.out.println(key);
        }
    }

    private static void certificateTest(BaseballElimination baseballElimination) {
        var teams = baseballElimination.teams();

        for (String team : teams) {
            var predicate = baseballElimination.isEliminated(team);
            System.out.println(team + ((predicate) ?
                    " is mathematically eliminated." : " is a title contender."));
        }

        System.out.println();
        System.out.println("Certificate of Elimination :");

        for (String team : teams) {
            var certificate = baseballElimination.certificateOfElimination(team);
            System.out.println(team + " -> " + certificate);
        }
    }

    private static void flowNetworkTest(BaseballElimination baseballElimination) {
        FlowNetwork G = baseballElimination.constructGraph(0, 4);
        System.out.println(G);
    }
}
