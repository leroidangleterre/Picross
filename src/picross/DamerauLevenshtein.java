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
        System.out.println("DL.distance: TODO");
        return 0;
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

}
