import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;


public class SeamCarver {
    private boolean isTranspose = false;
    private int rows, cols;
    private int[][] picture;
    private double[][] energyTable;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Invalid constructor argument");
        this.picture = getData(picture);
        int m = picture.width();
        int n = picture.height();
        energyTable = new double[n][m];
        rows = n;
        cols = m;

        // populate energy table
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < m; x++)
                energyTable[y][x] = energyCalculator(x, y, picture);
        }
    }

    private static int[][] getData(Picture picture) {
        int m = picture.width();
        int n = picture.height();
        int[][] pix = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                pix[i][j] = picture.getRGB(j, i);
            }
        }
        return pix;
    }

    // current picture
    public Picture picture() {
        int cols = (isTranspose) ? this.rows : this.cols;
        int rows = (isTranspose) ? this.cols : this.rows;

        Picture pix = new Picture(cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int rgb = (isTranspose) ? picture[j][i] : picture[i][j];
                pix.setRGB(j, i, rgb);
            }
        }
        return pix;
    }

    // width of current picture
    public int width() {
        return (isTranspose) ? rows: cols;
    }

    // height of current picture
    public int height() {
        return (isTranspose) ? cols: rows;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateX(x);
        validateY(y);
        if (isTranspose) return energyTable[x][y];
        return energyTable[y][x];
    }

    private void validateY(int y) {
        if (y < 0 || y >= this.height())
            throw new IllegalArgumentException("Value is outside the prescribed height range");
    }

    private void validateX(int x) {
        if (x < 0 || x >= this.width())
            throw new IllegalArgumentException("Value is outside the prescribed width range.");
    }

    // the dual-energy gradient function
    private static double energyCalculator(int x, int y, Picture pix) {
        final double edgeEnergy = 1000.0;
        int width = pix.width();
        int height = pix.height();

        if (x == 0 || x == width - 1) return edgeEnergy;
        if (y == 0 || y == height - 1) return edgeEnergy;

        double dx = change(x,1, y, 0, pix);
        double dy = change(x, 0, y, 1, pix);
        double sum = dx + dy;
        return Math.sqrt(sum);
    }

    // calculates the sum of the central difference
    private static double change(int x, int offsetX, int y, int offsetY, Picture pix) {
        int dr, dg, db;
        double sum;

        int color1 = pix.getRGB(x + offsetX, y + offsetY);
        int color2 = pix.getRGB(x - offsetX, y + offsetY);
        int r1 = (color1 >> 16) & 0xFF, r2 = (color2 >> 16) & 0xFF;
        int g1 = (color1 >>  8) & 0xFF, g2 = (color2 >>  8) & 0xFF;
        int b1 = (color1 >>  0) & 0xFF, b2 = (color2 >>  0) & 0xFF;

        // the central difference of r, g, and b
        dr = r1 - r2;
        dg = g1 - g2;
        db = b1 - b2;

        sum = Math.pow(dr, 2) + Math.pow(dg, 2) + Math.pow(db, 2);
        return sum;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!isTranspose) { transpose(); }
        return findSeam();
    }

    // returns an array of indices representing the vertical seam
    public int[] findVerticalSeam() {
        if (isTranspose) { transpose(); }
        return findSeam();
    }

    // returns an array of indices representing the seam
    private int[] findSeam() {
        final int degree = 3;   // the degree of each vertex
        final int column = cols;
        final int row = rows;
        final int grid = column * row;

        if (row == 1) return new int[]{0};    // because row is 1, there is no seam to remove

        Queue<Integer> q = new Queue<>();   // stores vertex to be processed in correct order
        double[] distTo = new double[grid]; // stores the distance of each vertex from their source
        int[] edgeTo = new int[grid];       // a path from a vertex to its source vertex

        // initialisation
        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
        // enqueue the source vertices
        for (int i = 0; i < column; i++) {
            q.enqueue(i);
            distTo[i] = 1000.0;
            edgeTo[i] = i;
        }

        // multiple source shortest-path algorithm
        while (!q.isEmpty()) {
            int[] offset_J = {-1, 0, 1};
            int offset_I = 1;
            int v = q.dequeue();

            // relax edge: v -> w
            for (int i = 0; i < degree; i++) {
                int _J = (v % column) + offset_J[i];
                int _I = (v / column) + offset_I;
                boolean inRange = (_J >= 0 && _J < column) && (_I < row);
                if (!inRange) continue;
                int w = (_I*column) + _J;
                relax(v, w, distTo, edgeTo, q);
            }
        }

        int v = minEnergySeamIndex(distTo, grid, column);
        int[] seam = getSeam(column, row, edgeTo, v);
        return seam;
    }

    private static int[] getSeam(int width, int height, int[] edgeTo, int v) {
        int i = height -1;
        int[] seam = new int[height];

        while (v != edgeTo[v]) {
            seam[i] = v % width;
            v = edgeTo[v];  // update
            i--;    // update
        }
        seam[0] = v;
        return seam;
    }

    // returns the index of the pixel with minimum energy value
    private static int minEnergySeamIndex(double[] weight, int grid, int width) {
        int min = grid - width;
        for (int i = min; i < grid; i++) {
            if (weight[i] < weight[min])    min = i;
        }
        return min;
    }

    private void relax(
            int v, int w,
            double[] distTo,
            int[] edgeTo,
            Queue<Integer> q
    ) {
        int width = cols;
        int height = rows;
        int x = w % width;
        int y = w / width;

        if (distTo[w] > distTo[v] + energyTable[y][x]) {
            distTo[w] = distTo[v] + energyTable[y][x];
            edgeTo[w] = v;
            // do not add cells in the last row to queue
            if (y < (height-1)) q.enqueue(w);
        }
    }

    // auxiliary matrix transpose
    private void transpose() {
        final int m = rows;
        final int n = cols;

        double[][] aux = new double[n][m];
        int[][] aux2 = new int[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                aux[j][i] = energyTable[i][j];
                aux2[j][i] = picture[i][j];
            }
        }
        // interchange rows and cols
        rows = n;
        cols = m;
        isTranspose = !isTranspose;
        energyTable = aux;
        picture = aux2;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException("Null value supplied.");
        if (this.height() <= 1)
            throw new IllegalArgumentException("Image height is too small");
        if (seam.length != this.width())
            throw new IllegalArgumentException("Invalid argument supplied.");
        for (int y : seam) { validateY(y); }
        for (int i = 0; i < (seam.length - 1); i++) {
            int difference = seam[i] - seam[i+1];
            if (Math.abs(difference) > 1)
                throw new IllegalArgumentException("The difference between consecutive seam value is more than 1");
        }

        if (!isTranspose) { transpose(); }
        resizePicture(seam);
        resizeEnergyTable(seam);
    }

    private void resizePicture(int[] seam) {
        // shift the elements of each row in the array to the left
        // starting from the index specified by seam[i]
        for (int i = 0; i < rows; i++) {
            int hold = picture[i][cols-1];
            for (int j = cols-1; j >= seam[i]; j--) {
                int temp = picture[i][j];
                picture[i][j] = hold;
                hold = temp;
            }
        }
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException("Null value supplied.");
        if (this.width() <= 1)
            throw new IllegalArgumentException("Image width cannot be further reduced.");
        if (seam.length != this.height())
            throw new IllegalArgumentException("Invalid argument supplied.");
        for (int x : seam) { validateX(x); }
        for (int i = 0; i < (seam.length - 1); i++) {
            int difference = seam[i] - seam[i+1];
            if (Math.abs(difference) > 1)
                throw new IllegalArgumentException("The difference between consecutive seam value is more than 1");
        }

        if (isTranspose) { transpose(); }
        resizePicture(seam);
        resizeEnergyTable(seam);
    }

    private void resizeEnergyTable(int[] seam) {
        // shift the elements of each row in the array to the left
        // starting from the index specified by seam[i]
        for (int i = 0; i < rows; i++) {
            double hold = energyTable[i][cols-1];
            for (int j = cols-1; j >= seam[i]; j--) {
                double temp = energyTable[i][j];
                energyTable[i][j] = hold;
                hold = temp;
            }
        }
        cols = cols-1; // update state
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture input = new Picture("C:\\Users\\HP\\Documents\\Cousera\\Algorithms Part II\\Test files\\seam\\chameleon.png");
        SeamCarver sc = new SeamCarver(input);
        for (int i = 0; i < 175; i++) {
            int[] seamV = sc.findVerticalSeam();
            sc.removeVerticalSeam(seamV);
        }
        for (int i = 0; i < 75; i++) {
            int[] seamH = sc.findHorizontalSeam();
            sc.removeHorizontalSeam(seamH);
        }
        input.show();
        Picture output = sc.picture();
        output.show();
    }
}
