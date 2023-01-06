import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

// Test client for percolation

public class PercolationStats {
    private final int trials;
    private final double[] threshold; // holds the percentage of open site for each trials

    public PercolationStats(int n, int trials) {
        // validate input
        if (n < 1)
            throw new IllegalArgumentException("Grid must have at least one site");

        if (trials < 1)
            throw new IllegalArgumentException("Percolation must be done at least once");

        //initialise fields
        this.trials = trials;
        threshold = new double[trials];

        //calculate the percentage of open sites required for percolation to occur for each trials
        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);

            while (!percolation.percolates()) {
                int row = StdRandom.uniform(0, n);
                int col = StdRandom.uniform(0, n);
                percolation.open(row, col);
            }
            threshold[i] = (double) percolation.numberOfOpenSites() / (n * n);
        }
    }   // End of constructor

    //test client
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        Stopwatch timer = new Stopwatch();
        PercolationStats stat = new PercolationStats(n, trials);

        StdOut.println("mean = " + stat.mean());
        StdOut.println("standard deviation = " + stat.stddev());
        StdOut.println("confidence interval = [" + stat.confidenceLo() + "," + stat.confidenceHi() + "]");

        double time = timer.elapsedTime();
        StdOut.printf("\nElapsed time : (%.2f seconds)\n", time);
    }

    // percolation mean
    public double mean() {
        return StdStats.mean(threshold);
    }

    // percolation Standard deviation
    public double stddev() {
        return StdStats.stddev(threshold);
    }

    // Low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (stddev() / Math.sqrt(trials));
    }

    // High endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (stddev() / Math.sqrt(trials));
    }
}

