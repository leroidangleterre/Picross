package picross;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author arthurmanoha
 * This class applies to a table a modified version of the Damerau-Levenshtein
 * distance.
 * The distance may be computed with operations among the usual ones: adding one
 * character, remove one character, swap two contiguous characters.
 *
 *
 * Conversion of arrays using A* algorithm:
 * start with the hint and apply the available operation until yout reach the
 * target.
 * The operation is:
 * - add a zero before a one that does not follow another one
 * The target is reached when each 'one' of the current line has a matching
 * 'one' in the hint line.
 */
public class DamerauLevenshtein {

    /**
     * Classic Damerau-Levenshtein distance between two tabs.
     *
     * @param tab1
     * @param tab2
     * @return The classic distance between of tab1 to tab2, i.e. how far away
     * tab1 is to the reference tab2.
     * In other words, which modifications we must apply to tab2 in order to
     * reach tab1.
     */
    public static int distance(int tab1[], int tab2[]) {
        boolean deletion = true;
        boolean insertion = true;
        boolean substitution = true;
        boolean transposition = true;
        return distance(tab1, tab2, deletion, insertion, substitution, transposition);
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

        // First column and first row contain values counting from 1.
        for (int i = 0; i < length1; i++) {
            mat[i + 1][0] = i + 1;
        }
        for (int i = 0; i < length2; i++) {
            mat[0][i + 1] = i + 1;
        }

        mat[0][0] = 0;

//        printMatrix(mat);
        int costForDeletion = deletionAuthorized ? 1 : 1000;
        int costForInsertion = insertionAuthorized ? 1 : 1000;
        int costForSubstitution = substitutionAuthorized ? 1 : 1000;
        int costForTransposition = transpositionAuthorized ? 1 : 1000;
        for (int i = 0; i < length1; i++) {
            for (int j = 0; j < length2; j++) {
                int cost;
                if (tab1[i] == tab2[j]) {
                    cost = 0;
                } else {
                    cost = costForSubstitution;
                }
                mat[i + 1][j + 1] = min(
                        mat[i][j + 1] + costForDeletion, // Deletion
                        mat[i + 1][j] + costForInsertion, // Insertion
                        mat[i][j] + cost // Substitution
                );
                if (i > 0 && j > 0
                        && tab1[i] == tab2[j - 1]
                        && tab1[i - 1] == tab2[j]) {
                    mat[i + 1][j + 1]
                            = min(mat[i + 1][j + 1], mat[i - 1][j - 1] + costForTransposition); // Transposition
                }
            }
        }

//        System.out.println("");
//        printMatrix(mat);
//        System.out.println("");
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

    private static void printMatrix(int[][] matrix) {
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
