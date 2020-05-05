package picross;

import java.util.ArrayList;

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

        printMatrix(mat);

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

        System.out.println("");
        printMatrix(mat);
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

    /**
     * Determine whether tab0 can be transformed into tab1.
     * Only certain modifications may be applied.
     *
     * We use the A* algorithm to find a path from tab0 to tab1 using only the
     * available operations.
     *
     * @param hintsTab the tested tab
     * @param testedLine the target tab
     * @return true if we can find a series of modifications that change tab0
     * into tab1, false otherwise.
     *
     */
    public static boolean canConvert(int hintsTab[], int testedLine[]) {

        System.out.print("Converting ");
        for (int i = 0; i < hintsTab.length; i++) {
            System.out.print(hintsTab[i] + " ");
        }
        System.out.print(" to ");
        for (int i = 0; i < testedLine.length; i++) {
            System.out.print(testedLine[i] + " ");
        }
        System.out.println("");

        ArrayList<AstarNode> closedList = new ArrayList<>();
        ArrayList<AstarNode> openList = new ArrayList<>();

//        openList.add(tab0);
        AstarNode initNode = new AstarNode(hintsTab, null, 0);
        openList.add(initNode);

//        System.out.println("init node: \n" + initNode);
        int maxLength = testedLine.length;
        System.out.println("Tested line: " + testedLine.length + ", hint: " + hintsTab.length);

        boolean loop = true;
        boolean result = false;
        AstarNode finalNode = null;
        while (loop) {

            // Extract the first element
            AstarNode currentNode = openList.remove(0);

            if (tabsAreEqual(currentNode.getArray(), testedLine)) {
                // Found the solution
                result = true;
                loop = false;
                finalNode = currentNode;
//                System.out.println(currentNode + " is solution !!! ");
//                System.out.print("tested line: ");
//                for (int i = 0; i < testedLine.length; i++) {
//                    System.out.print(testedLine[i] + " ");
//                }
//                System.out.println("");
                return true;
            } else {
//                System.out.println(currentNode + " is not solution");
            }

            ArrayList<int[]> neighbors = getNeighbors(currentNode.getArray(), maxLength);
//            openList.addAll(neighbors);
            for (int[] neighbor : neighbors) {
                if (!contains(openList, neighbor)) {
                    AstarNode neighborNode = new AstarNode(neighbor, currentNode, currentNode.getCost());
                    System.out.println(neighborNode);
                    openList.add(neighborNode);
                }
            }
            closedList.add(currentNode);
//            System.out.println("adding " + currentNode + " to closed list.");

            if (openList.isEmpty()) {
                // No other tab to evaluate: no solution
                result = false;
                loop = false;
            }
        }

//        // VERIFICATION
//        if (result == true) {
//            // Print the line of succession
//            AstarNode currentNode = finalNode;
//            System.out.println("Line of succession:");
//            while (currentNode != null) {
//                System.out.println(currentNode);
//                currentNode = currentNode.getParent();
//            }
//        }
        return result;
    }

    /**
     * Compare two arrays
     *
     * @param tab0
     * @param tab1
     * @return
     */
    public static boolean tabsAreEqual(int tab0[], int tab1[]) {

//        System.out.print("comparing ");
//        for (int i = 0; i < tab0.length; i++) {
//            System.out.print(tab0[i] + " ");
//        }
//        System.out.print(" and ");
//        for (int i = 0; i < tab1.length; i++) {
//            System.out.print(tab1[i] + " ");
//        }
//        System.out.println("");
        if (tab0.length != tab1.length) {
//            System.out.println("NOT EQUAL");
            return false;
        }
        for (int i = 0; i < tab0.length; i++) {
            if (tab0[i] != tab1[i]) {
//                System.out.println("NOT EQUAL");
                return false;
            }
        }
//        System.out.println("EQUAL");
        return true;
    }

    /**
     * Find all the arrays we can get by performing an atomic operation on an
     * array, while maintaining a length no greater than maxLength.
     *
     */
    public static ArrayList<int[]> getNeighbors(int tab[], int maxLength) {

        ArrayList<int[]> neighbors = new ArrayList<>();
        int length = tab.length;

        int[] clone;

        // First operation: add a zero at the beginning or at the end and shift everything one step to the right.
        // Only possible if it does not eject a one from the end of the array.
//        if (tab.length < maxLength /* && tab[tab.length - 1] == 0*/) {
//            // At start
//            clone = new int[length + 1];
//            clone[0] = 0;
//            for (int i = 0; i < length; i++) {
//                clone[i + 1] = tab[i];
//            }
//            neighbors.add(clone);
//        }
//        if (tab.length < maxLength) {
//            // At end
//            clone = new int[length + 1];
//            clone[length] = 0;
//            for (int i = 0; i < length; i++) {
//                clone[i] = tab[i];
//            }
//            neighbors.add(clone);
//        }
//        // Second operation: Insert a zero between a zero and a one (i.e. convert "01" into "001"
//        if (tab.length < maxLength) {
//            for (int i = 0; i < length - 1; i++) {
//                // At each position where a 0 is followed by a 1, we create a new modified clone.
//                if (tab[i] == 0 && tab[i + 1] == 1) {
//                    clone = new int[length + 1];
//                    for (int j = 0; j < i; j++) {
//                        clone[j] = tab[j];
//                    }
//                    // Insert the 0
//                    clone[i] = 0;
//                    // Append the rest
//                    for (int j = i + 1; j < length; j++) {
//                        clone[j] = tab[j - 1];
//                    }
//                    neighbors.add(clone);
//                }
//            }
//        }
        // Third operation: every 1 can be changed to a 0.
        for (int i = 0; i < length; i++) {
            if (tab[i] == 1) {
                clone = tab.clone();
                clone[i] = 0;
                neighbors.add(clone);
//                System.out.println("changed 1 into 0; neighbor list size: " + neighbors.size());
            }
        }
        return neighbors;
    }

    /**
     * Returns true when the given list contains one node associated with
     * the
     * given array.
     *
     * @param openList
     * @param neighbor
     * @return
     */
    private static boolean contains(ArrayList<AstarNode> openList, int[] neighbor) {

        for (AstarNode node : openList) {
            if (tabsAreEqual(neighbor, node.getArray())) {
                return true;
            }
        }
        return false;
    }
}
