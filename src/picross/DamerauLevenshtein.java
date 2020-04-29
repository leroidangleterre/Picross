package picross;

/**
 *
 * @author arthurmanoha
 * This class applies to a table a modified version of the Damerau-Levenshtein
 * distance.
 * The distance may be computed with operations among the usual ones: adding one
 * character, remove one character, swap two contiguous characters.
 *
 */
public class DamerauLevenshtein {

    /**
     * Classic Damerau-Levenshtein distance between two tabs.
     *
     * @param tab1
     * @param tab2
     * @return The classic distance between the two tab, i.e. using all unit
     * operations
     */
    public static int distance(int tab1[], int tab2[]) {
        return distance(tab1, tab2, true, true, true, true);
    }

    private static int distance(int tab1[], int tab2[],
            boolean deletionAuthorized, boolean insertionAuthorized,
            boolean substitutionAuthorized, boolean transpositionAuthorized) {

        int length1 = tab1.length;
        int length2 = tab2.length;

        // Init matrix
        int mat[][] = new int[length1 + 1][];
        for (int i = 0; i < length1 + 1; i++) {
            mat[i] = new int[length2 + 1];
        }

        // First column and first row contain the two tabs.
        for (int i = 0; i < length1; i++) {
            mat[i + 1][0] = tab1[i];
        }
        for (int i = 0; i < length2; i++) {
            mat[0][i + 1] = tab2[i];
        }

        mat[0][0] = 0;

        printMatrix(mat, true);

        int cost;
        for (int i = 0; i < length1; i++) {
            for (int j = 0; j < length2; j++) {
                if (tab1[i] == tab2[j]) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                mat[i + 1][j + 1] = min(mat[i][j + 1] + 1, // Deletion
                        mat[i + 1][j] + 1, // Insertion
                        mat[i][j] + cost); // Substitution
                if (i > 0 && j > 0 && tab1[i] == tab2[j - 1] && tab1[i - 1] == tab2[j]) {
                    mat[i + 1][j + 1] = min(mat[i][j], mat[i - 2][j - 2] + 1); // Transposition
                }
            }
        }

        System.out.println("");
        printMatrix(mat, true);
        System.out.println("");

        return mat[length1][length2];
    }

    /**
     * Modified Damerau-Levenshtein distance: we may only use some operations:
     * - add a zero at the beginning;
     * - add a zero between a zero and a one;
     * - transform a one into a zero
     *
     * @param tab1
     * @param tab2
     * @return
     */
    public static int distanceAddOnly(int tab1[], int tab2[]) {
        System.out.println("DL.distanceAddOnly: TODO, must choose what value we want to return.");
        return 0;
    }

    private static void printMatrix(int[][] matrix, boolean withWords) {
        System.out.println("Matrix:");

        for (int i = 0; i < matrix.length; i++) {
            if (i == 1) {
                for (int j = 0; j <= matrix[i].length; j++) {
                    System.out.print("- ");
                }
                System.out.println("");
            }
            for (int j = 0; j < matrix[i].length; j++) {
                if (j == 1) {
                    System.out.print("| ");
                }
                if (i == 0 && j == 0) {
                    System.out.print("* ");
                } else if (withWords && (i == 0 || j == 0)) {
                    System.out.print((char) (matrix[i][j]) + " ");
                } else {
                    System.out.print(matrix[i][j] + " ");
                }
            }
            System.out.println("");
        }
    }

    /**
     * Convert a string into an array of characters.
     *
     * @param inputString the string to convert
     * @return
     */
    public static int[] stringToTab(String inputString) {
        int[] tab = new int[inputString.length()];
        for (int i = 0; i < inputString.length(); i++) {
            tab[i] = inputString.charAt(i);
        }
        return tab;
    }

    private static int min(int a, int b) {
        return a < b ? a : b;
    }

    private static int min(int a, int b, int c) {
        return min(min(a, b), c);
    }

}
