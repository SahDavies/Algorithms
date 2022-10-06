import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * @author Dima Pasieka
 */
public class Percolation {
    private int gridLength;
    private boolean[] state; // A site-indexed array that stores the state of each site (either it's open or blocked)
    private int numberOfOpenSites;
    private int topVirtualSite; // Index of the top virtual site (has value 0)
    private int bottomVirtualSite;  // Index of the top virtual site (has value (gridLength * gridLength) + 1)

    private WeightedQuickUnionUF percolationUF;

    // Weighted quick union-find data structure
    // to calculate fullness (without bottom virtual site)
    private WeightedQuickUnionUF fullSiteUF;

    // Create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Grid must have at least one row and column");
        }

        gridLength = n;
        int gridSize = (n * n) + 2; // with two virtual sites
        state = new boolean[gridSize];
        numberOfOpenSites = 0;

        // initialise virtual sites and save their state
        topVirtualSite = 0;
        bottomVirtualSite = (gridLength * gridLength) + 1;
        state[topVirtualSite] = true;
        state[bottomVirtualSite] = false;

        percolationUF = new WeightedQuickUnionUF(gridSize);
        fullSiteUF = new WeightedQuickUnionUF(gridSize);

        // connect top and bottom rows to virtual sites
        for (int col = 1; col <= gridLength; col++) {
            // connect first row to topVirtualIndex
            int firstRow = 1;
            int firstRowSites = getSite(firstRow, col);
            percolationUF.union(topVirtualSite, firstRowSites);
            fullSiteUF.union(topVirtualSite, firstRowSites);

            // connect last row to bottomVirtualIndex
            int lastRow = gridLength;
            int bottomRowSites = getSite(lastRow, col);
            percolationUF.union(bottomVirtualSite, bottomRowSites);
        }
    }

    // Open site (row, col) if it is not open already
    public void open(int row, int col)
    {
        int site = getSite(row, col);
        if (state[site]) {
            return;
        }

        state[site] = true;
        numberOfOpenSites++;

        // connect with left neighbor
        if (col > 1 && isOpen(row, col - 1)) {
            int siteOnTheLeft = getSite(row, col - 1);
            percolationUF.union(site, siteOnTheLeft);
            fullSiteUF.union(site, siteOnTheLeft);
        }

        // connect with right neighbor
        if (col < gridLength && isOpen(row, col + 1)) {
            int siteOnTheRight = getSite(row, col + 1);
            percolationUF.union(site, siteOnTheRight);
            fullSiteUF.union(site, siteOnTheRight);
        }

        // connect with top neighbor
        if (row > 1 && isOpen(row - 1, col)) {
            int siteOnTop = getSite(row - 1, col);
            percolationUF.union(site, siteOnTop);
            fullSiteUF.union(site, siteOnTop);
        }

        // connect with bottom neighbor
        if (row < gridLength && isOpen(row + 1, col)) {
            int siteAtTheBottom = getSite(row + 1, col);
            percolationUF.union(site, siteAtTheBottom);
            fullSiteUF.union(site, siteAtTheBottom);
        }
    }

    // If site (row, col) open
    public boolean isOpen(int row, int col)
    {
        int site = getSite(row, col);

        return state[site];
    }

    // If site (row, col) full
    public boolean isFull(int row, int col)
    {
        int site = getSite(row, col);

        return (isOpen(row, col) && fullSiteUF.connected(topVirtualSite, site));
    }

    // Number of open sites
    public int numberOfOpenSites()
    {
        return numberOfOpenSites;
    }

    // If the system percolate
    public boolean percolates()
    {
        // if grid with one site - check if it"s open
        if (gridLength == 1) {
            int site = getSite(1, 1);
            return state[site];
        }

        return percolationUF.connected(topVirtualSite, bottomVirtualSite);
    }

    // Returns a site which serves as an array index
    private int getSite(int row, int col)
    {
        validateBounds(row, col);

        return ((row - 1) * gridLength) + col;
    }

    // Check if row and column values are in range of grid size
    private void validateBounds(int row, int col)
    {
        if (row > gridLength || row < 1) {
            throw new IndexOutOfBoundsException("Row index is out of bounds");
        }

        if (col > gridLength || col < 1) {
            throw new IndexOutOfBoundsException("Column index is out of bounds");
        }
    }

    // Test client (optional)
    public static void main(String[] args)
    {
        Percolation percolation = new Percolation(2);

        StdOut.println("percolates = " + percolation.percolates());

        StdOut.println("isOpen(1, 2) = " + percolation.isOpen(1, 2));
        StdOut.println("isFull(1, 2) = " + percolation.isFull(1, 2));
        StdOut.println("open(1, 2)");
        percolation.open(1, 2);
        StdOut.println("isOpen(1, 2) = " + percolation.isOpen(1, 2));
        StdOut.println("isFull(1, 2) = " + percolation.isFull(1, 2));
        StdOut.println("numberOfOpenSites() = " + percolation.numberOfOpenSites());
        StdOut.println("percolates() = " + percolation.percolates());

        StdOut.println("isOpen(2, 1) = " + percolation.isOpen(2, 1));
        StdOut.println("isFull(2, 1) = " + percolation.isFull(2, 1));
        StdOut.println("open(2, 1)");
        percolation.open(2, 1);
        StdOut.println("isOpen(2, 1) = " + percolation.isOpen(2, 1));
        StdOut.println("isFull(2, 1) = " + percolation.isFull(2, 1));
        StdOut.println("numberOfOpenSites() = " + percolation.numberOfOpenSites());
        StdOut.println("percolates() = " + percolation.percolates());

        StdOut.println("isOpen(1, 1) = " + percolation.isOpen(1, 1));
        StdOut.println("isFull(1, 1) = " + percolation.isFull(1, 1));
        StdOut.println("open(1, 1)");
        percolation.open(1, 1);
        StdOut.println("isOpen(1, 1) = " + percolation.isOpen(1, 1));
        StdOut.println("isFull(1, 1) = " + percolation.isFull(1, 1));
        StdOut.println("numberOfOpenSites() = " + percolation.numberOfOpenSites());
        StdOut.println("percolates() = " + percolation.percolates());
    }
}
