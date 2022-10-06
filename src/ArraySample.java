public class ArraySample {
    public static void main(String[] args) {
        int[][] array = new int[5][6];
        int counter = 0;
        // initialise array
        for (int[] row : array) {
            for (int cell = 0; cell < row.length; cell++) {
                row[cell] = cell + counter;
            }
            counter += array[0].length;
        }
        // print array contents
        System.out.printf("%d x %d matrix%n",
                array.length, array[0].length);
        for (int[] row : array) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }

        System.out.println("Transpose of matrix");
        for (int[] row : transpose(array)) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }   // end of main

    // in-place transpose
    public static int[][] transpose(int[][] matrix) {
        if (matrix.length != matrix[0].length )
            return auxTranspose(matrix);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
        return matrix;
    }

    // auxiliary matrix transpose
    public static int[][] auxTranspose(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        int[][] aux = new int[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                aux[j][i] = matrix[i][j];
            }
        }
        return aux;
    }
}
