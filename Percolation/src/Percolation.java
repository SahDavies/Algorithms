import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int size;
    private final boolean[] isOpen; // A site-indexed array that stores the isOpen of each site (either it's open or blocked)
    private int numberOfOpenSites;
    private final int entryPoint; // Index of the source site (has value 0)
    private final int exitPoint;  // Index of the exit site (has value (size * size) + 1)

    private final WeightedQuickUnionUF percolationUF;

    // Weighted quick union-find data structure
    // to calculate fullness

    // Create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Grid must have at least one row and column");
        }

        final int gridSize = (n * n) + 2; // with two virtual sites
        size = n;
        isOpen = new boolean[gridSize];
        numberOfOpenSites = 0;

        // initialise virtual sites and save their isOpen
        entryPoint = 0;
        exitPoint = gridSize - 1;
        isOpen[entryPoint] = true;

        percolationUF = new WeightedQuickUnionUF(gridSize);
    }

    // Open site (row, col) if it is not open already
    public void open(int row, int col) {
        int site = (row * size + col) + 1;
        if (isOpen[site])   return;

        isOpen[site] = true;
        numberOfOpenSites++;
        // connect sites in first row to entry point
        if (row == 0)
            percolationUF.union(entryPoint, site);
        // connect sites in last row to exit point
        if (row == size-1)
            percolationUF.union(exitPoint, site);

        final int[] shift_I = {-1, 0, 0, 1};
        final int[] shift_J = {0, -1, 1, 0};

        for (int i= 0; i < 4; i++) {
            int _row = row + shift_I[i];
            int _col = col + shift_J[i];
            int neighbour = (_row * size + _col) + 1;
            boolean inRange = (_row < size && _row >= 0) && (_col < size && _col >= 0);

            if (inRange && isOpen[neighbour])
                percolationUF.union(site, neighbour);
        }
    }

    // If site (row, col) open
    public boolean isOpen(int row, int col) {
        int site = (row * size + col) + 1;
        return isOpen[site];
    }

    // If site (row, col) full
    public boolean isFull(int row, int col) {
        int site = (row * size + col) + 1;
        return (isOpen[site] &&
                percolationUF.find(entryPoint) == percolationUF.find(site));
    }

    // Number of open sites
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    // If the system percolate
    public boolean percolates() {
        // if grid with one site - check if it's open
        if (size == 1) return isOpen[1];

        return percolationUF.find(entryPoint) ==
                percolationUF.find(exitPoint);
    }
}
