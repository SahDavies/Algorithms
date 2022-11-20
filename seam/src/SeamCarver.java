import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;


public class SeamCarver {
    private boolean isTranspose = false;
    private int width;
    private int[] picture;
    private double[][] energyTable;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Invalid constructor argument");
        this.picture = getData(picture);
        int m = picture.width();
        int n = picture.height();
        energyTable = new double[n][m];
        width = m;

        // populate energy table
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < m; x++)
                energyTable[y][x] = energyCalculator(x, y, picture);
        }
    }

    private static int[] getData(Picture picture) {
        int m = picture.width();
        int n = picture.height();
        int grid = m * n;
        int[] pix = new int[grid];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int cell = (i * m) + j;
                pix[cell] = picture.getRGB(j, i);
            }
        }
        return pix;
    }

    // current picture
    public Picture picture() {
        int grid = picture.length;
        int m = width;
        int n = grid / m;
        Picture pix = new Picture(m, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int cell = (i * m) + j;
                int rgb = picture[cell];
                pix.setRGB(j, i, rgb);
            }
        }
        return pix;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return picture.length / width;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateX(x);
        validateY(y);
        if (isTranspose) return energyTable[x][y];
        return energyTable[y][x];
    }

    private void validateY(int y) {
        int height = picture.length / width;
        if (y < 0 || y >= height)
            throw new IllegalArgumentException("Value is outside the prescribed height range");
    }

    private void validateX(int x) {
        if (x < 0 || x >= width)
            throw new IllegalArgumentException("Value is outside the prescribed width range.");
    }

    // the dual-energy gradient function
    private static double energyCalculator(int x, int y, Picture pix) {
        final double edgeEnergy = 1000.0;
        int width = pix.width();
        int height = pix.height();

        if (x == 0 || x == width - 1) return edgeEnergy;
        if (y == 0 || y == height - 1) return edgeEnergy;

        double dx = changeInX(x, y, pix);
        double dy = changeInY(x, y, pix);
        double sum = dx + dy;
        return Math.sqrt(sum);
    }

    // calculates the sum of the central difference of the column
    private static double changeInX(int x, int y, Picture pix) {
        int dr, dg, db;
        double sum;

        int color1 = pix.getRGB(x + 1, y);
        int color2 = pix.getRGB(x - 1, y);
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

    // calculates the sum of the central difference of the row
    private static double changeInY(int x, int y, Picture pix) {
        int dr, dg, db;
        double sum;

        int color1 = pix.getRGB(x, y + 1);
        int color2 = pix.getRGB(x, y - 1);
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
        if (!isTranspose) {
            energyTable = transpose(energyTable);
            isTranspose = true;
        }
        return findSeam();
    }

    // returns an array of indices representing the vertical seam
    public int[] findVerticalSeam() {
        if (isTranspose) {
            energyTable = transpose(energyTable);
            isTranspose = false;
        }
        return findSeam();
    }

    // returns an array of indices representing the seam
    private int[] findSeam() {
        final int degree = 3;   // the degree of each vertex
        final int column = energyTable[0].length;
        final int row = energyTable.length;
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
            int v = q.dequeue();

            // ignore if vertex is in the first or last column
            boolean isOnFirstColumn = v % column == 0;
            boolean isOnLastColumn = v % column == (column - 1);
            if (!isOnFirstColumn || !isOnLastColumn) continue;

            // relax edge: v -> w
            for (int i = 0; i < degree; i++) {
                int[] shift = {-1, 0, 1};
                int w = v + shift[i];
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
        int width = energyTable[0].length;
        int height = energyTable.length;
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
    private static double[][] transpose(double[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        if (m == n) return inPlaceTranspose(matrix);

        double[][] aux = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                aux[j][i] = matrix[i][j];
            }
        }
        return aux;
    }

    private static double[][] inPlaceTranspose(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                double temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
        return matrix;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        int height = picture.length / width;
        if (seam == null)
            throw new IllegalArgumentException("Null value supplied.");
        if (height <= 1)
            throw new IllegalArgumentException("Image height is too small");
        if (seam.length != width)
            throw new IllegalArgumentException("Invalid argument supplied.");
        for (int y : seam) { validateY(y); }
        for (int i = 0; i < (seam.length - 1); i++) {
            int difference = seam[i] - seam[i+1];
            if (Math.abs(difference) > 1)
                throw new IllegalArgumentException("The difference between consecutive seam value is more than 1");
        }

        if (!isTranspose) {
            energyTable = transpose(energyTable);
            isTranspose = true;
        }
        resizePictureRow(seam);
        resizeEnergyTable(seam);
    }

    private void resizePictureRow(int[] seam) {
        // note that temp array is smaller than picture array
        int grid = picture.length;
        int[] temp = new int[grid - seam.length];
        int cols = width;
        int rows = (grid / cols) - 1;

        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                int cell = (i * cols) + j;
                int cell_Shift = cell;
                if (i == seam[j]) { cell_Shift = (i + 1) * cols + j; }
                temp[cell] = picture[cell_Shift];
            }
        }
        picture = temp;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        int height = picture.length / width;
        if (seam == null)
            throw new IllegalArgumentException("Null value supplied.");
        if (width <= 1)
            throw new IllegalArgumentException("Image width cannot be further reduced.");
        if (seam.length != height)
            throw new IllegalArgumentException("Invalid argument supplied.");
        for (int x : seam) { validateX(x); }
        for (int i = 0; i < (seam.length - 1); i++) {
            int difference = seam[i] - seam[i+1];
            if (Math.abs(difference) > 1)
                throw new IllegalArgumentException("The difference between consecutive seam value is more than 1");
        }

        if (isTranspose) {
            energyTable = transpose(energyTable);
            isTranspose = false;
        }
        resizePictureColumn(seam);
        resizeEnergyTable(seam);
    }

    private void resizeEnergyTable(int[] seam) {
        int row = energyTable.length;
        int col = energyTable[0].length;
        double[][] temp = new double[row][col - 1];

        for (int i = 0; i < row; i++) {
            int shift = 0;
            for (int j = 0; j < temp[0].length; j++) {
                // condition for shifting cells to the left
                if (j == seam[i])   shift++;
                temp[i][j] = energyTable[i][j + shift];
            }
        }
        energyTable = temp;
    }

    private void resizePictureColumn(int[] seam) {
        int grid = picture.length;
        int[] temp = new int[grid - seam.length];   // temp array is smaller than picture array by seam.length
        int cols = width - 1;
        int rows = (grid / width);
        int shift = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cell = (i * cols) + j;
                if (j == seam[i])   shift++;
                temp[cell] = picture[cell + shift];
            }
        }
        picture = temp;
        width--;    // update width
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture input = new Picture("C:\\Users\\Sir_Davies\\Documents\\Cousera\\Algorithms Part II\\Test Files\\seam\\HJocean.png");
        SeamCarver sc = new SeamCarver(input);
        for (int i = 0; i < 75; i++) {
            int[] seamH = sc.findVerticalSeam();
            sc.removeVerticalSeam(seamH);
        }
        input.show();
        Picture output = sc.picture();
        output.show();
    }
}
