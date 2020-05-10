/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package picross;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author arthurmanoha
 */
class Grid {

    public static char notProcessedCharacter = ' ';
    public static char filledCharacter = 0x0870; // Nice square but empty
    public static char emptyCharacter = '.';

    public static final int FILLED = 1;
    public static final int EMPTY = 0;

    private static int NB_STEPS_BEFORE_DISPLAY = 100000;
    private static int STEP = 0;
    private static int COUNT = 0;
    private static boolean MUST_DISPLAY = false;

    int nbLines;
    int nbColumns;
    int grid[][];
    int lineHints[][];
    int colHints[][];
    boolean lineChecks[];
    boolean colChecks[];

    public Grid(String filename) {
        try {
            String path = "C:/Users/arthurmanoha/Documents/Programmation/Java/Picross/src/picross/";
            BufferedReader reader = new BufferedReader(new FileReader(path + filename));
            String text;
            boolean isReadingLines = true;
            int lineOrColIndex = 0;
            while ((text = reader.readLine()) != null) {
                // Parse the text
                if (text.contains("lines")) {
                    nbLines = Integer.valueOf(text.split(" ")[0]);
                    nbColumns = Integer.valueOf(text.split(" ")[2]);
                    lineHints = new int[nbLines][];
                    colHints = new int[nbColumns][];
                    for (int i = 0; i < nbLines; i++) {
                        lineHints[i] = new int[nbColumns];
                    }
                    for (int j = 0; j < nbColumns; j++) {
                        colHints[j] = new int[nbLines];
                    }
                    lineChecks = new boolean[nbLines];
                    colChecks = new boolean[nbColumns];
                    // Start reading the lines
                    isReadingLines = true;
                    lineOrColIndex = 0;
                } else if (text.equals("columns")) {
                    // Start reading the columns
                    isReadingLines = false;
                    lineOrColIndex = 0;
                } else {
                    // Prepare to read the next line or column
                    // Read an actual line or column
                    String[] hintTab = text.split(" ");
                    int hintIndex = 0;
                    for (String hint : hintTab) {
                        // Fill the line or col table
                        if (isReadingLines) {
                            lineHints[lineOrColIndex][hintIndex] = Integer.valueOf(hint);
                            hintIndex++;
                        } else {
                            colHints[lineOrColIndex][hintIndex] = Integer.valueOf(hint);
                            hintIndex++;
                        }
                    }
                    lineOrColIndex++;
                }
            }

            // Move the hints to the end of every hint table.
//            moveHintsToEndOfTables();
            // Create an empty grid.
            grid = new int[nbLines][];
            for (int line = 0; line < nbLines; line++) {
                grid[line] = new int[nbColumns];
                for (int col = 0; col < nbColumns; col++) {
                    grid[line][col] = EMPTY;//NOT_PROCESSED;
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: file <" + filename + "> not found");
        } catch (IOException e) {
            System.out.println("Error: IO exception");
        }
    }

    /**
     * Print the grid in four quadrants. Quadrant NW: nothing Quadrant NE: the
     * indices for the columns (displayed on as many lines as the longest list
     * of column indices) Quadrant SW: the indicies for the lines (idem)
     * Quadrant SE: the actual grid.
     *
     * Print a character for filled and empty squares up until (line, col);
     * Print blank after that.
     */
    public void printGrid(int lineMax, int colMax) {

        int maxNbLineHints = getNbMaxHints(true);
        int maxNbColHints = getNbMaxHints(false);

        String separation = "";
        for (int i = 0; i < maxNbLineHints * 3 + nbColumns * 3 + 5; i++) {
            separation += "-";
        }
        System.out.println(separation);

        int lineHintStartIndex = maxNbLineHints;
        // Quadrant NE
        int nbMaxColHints = getNbMaxHints(false);
        for (int hintIndex = 0; hintIndex < nbMaxColHints; hintIndex++) {
            // This part is the empty NW quadrant
            for (int j = 0; j < lineHintStartIndex; j++) {
                System.out.print("   "); // Place the column indicators above the grid
            }
            System.out.print(" "); // Take into account the vertical separator after the line indications

            // This part is the actual NE quadrant
            for (int col = 0; col < nbColumns; col++) {
                int nbColHints = trimArray(colHints[col]).length;
                // Align the hints on the last line.
                int hint = this.getColHint(col, hintIndex + (nbColHints - maxNbColHints));
                if (hint == 0) {
                    System.out.print("   "); // Spaceholder for empty hint
                } else {
                    System.out.print(String.format("%2d ", hint));
                }
            }
            System.out.println("");
        }

        System.out.print("  ");
        for (int i = 0; i < lineHintStartIndex; i++) {
            System.out.print("   ");
        }
        for (int i = 0; i < nbColumns; i++) {
            System.out.print("-  ");
        }
        System.out.println("");

        // Quadrants SW and SE
        int nbMaxLineHints = getNbMaxHints(true);
        for (int line = 0; line < nbLines; line++) {
            int nbLineHints = trimArray(lineHints[line]).length;
            // Display the hints
            for (int hintIndex = 0; hintIndex < nbMaxLineHints; hintIndex++) {
                int hint = this.getLineHint(line, hintIndex + (nbLineHints - maxNbLineHints));
                if (hint == 0) {
                    System.out.print("   "); // Spaceholder for empty hint
                } else {
                    System.out.print(String.format("%2d ", hint));
                }
            }

            System.out.print("|");

            // Display the grid values
            for (int col = 0; col < nbColumns; col++) {
                switch (grid[line][col]) {
                case EMPTY:
                    if (line > lineMax || (line == lineMax && col >= colMax)) {
                        System.out.print(" " + notProcessedCharacter + " ");
                    } else {
                        System.out.print(" " + emptyCharacter + " ");
                    }
                    break;
                case FILLED:
                    System.out.print(" " + filledCharacter + " ");
                    break;
                default:
                    System.out.print(" ? ");
                    break;
                }
            }
            System.out.print("|");
            if (lineChecks[line] == false) {
                System.out.print(" ! ");
            } else {
                System.out.print(" ok");
            }
            System.out.println("");
        }

        for (int i = 0; i < lineHintStartIndex; i++) {
            System.out.print("   ");
        }
        System.out.print("  ");
        for (int i = 0; i < nbColumns; i++) {
            if (colChecks[i] == false) {
                System.out.print("!  ");
            } else {
                System.out.print("ok ");
            }
        }
        System.out.println("");
    }

    /**
     * Get the n-th hint for the given column.
     *
     * @param col
     * @param rank
     * @return 0 if the rank is less than 0 (not inclusive) or if rank
     * is too large.
     */
    private int getColHint(int col, int rank) {
        if (rank < 0 || colHints[col].length < rank) {
            return 0;
        }
        return colHints[col][rank];
    }

    /**
     * Get the n-th hint for the given line.
     *
     * @param line
     * @param rank
     * @return
     */
    private int getLineHint(int line, int rank) {
        if (rank < 0 || lineHints[line].length < rank) {
            return 0;
        }
        return lineHints[line][rank];
    }

    /**
     * Shift all values toward the end of a table.
     * Basically move the trailing zeroes to the beginning:
     * 1 2 3 4 5 0 0 0 -> 0 0 0 1 2 3 4 5
     * 0 1 0 2 0 3 0 0 0 0 0 -> 0 0 0 0 0 0 1 0 2 0
     *
     * @param tab
     */
    private void moveValuesToEndOfTab(int tab[]) {

        if (tabIsOnlyZeroes(tab)) {
            return;
        }

        int indexForZero = 0; // We add a zero at this position in the tab.
        while (tab[tab.length - 1] == 0) {
            // Shift everything one step toward the end.

            for (int i = tab.length - 1; i > 0; i--) {
                tab[i] = tab[i - 1];
            }
            tab[indexForZero] = 0;
        }
    }

    private boolean tabIsOnlyZeroes(int[] tab) {
        for (int i = 0; i < tab.length; i++) {
            if (tab[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private void waitForKeypressed() {
        try {
            System.out.println("press key");
            System.in.read();
        } catch (IOException e) {
            System.out.println("grid wait error...");
        }
    }

    /**
     * Backtrack solving.
     *
     */
    public void solve() {
        System.out.println("solve(0);");
        this.solve(0);
        printGrid(nbLines, nbColumns);
    }

    /**
     * Solve the grid assuming every line before 'line' is already solved
     * and every square on current line but before 'column' is already solved.
     * Calls itself on the next square and going forward if still correct,
     * returns false if incorrect.
     *
     * @param line
     * @param col
     * @param forward
     * @return
     */
    private boolean solve(int squareIndex) {

        int line = squareIndex / nbColumns;
        int col = squareIndex - line * nbColumns;

        if (MUST_DISPLAY) {
            System.out.println("solve(" + squareIndex + ", " + COUNT + ");");
            this.printGrid(line, col);
            waitForKeypressed();
            MUST_DISPLAY = false;
        }

        STEP++;
        COUNT++;
        if (STEP >= NB_STEPS_BEFORE_DISPLAY) {
            STEP = 0;
            MUST_DISPLAY = true;
        }

        if (line >= nbLines) {
            // Found a soution
            System.out.println("line = " + line + ", nbLines = " + nbLines + ", returning true");
            return true;
        }

        // Try to fill the square first
        grid[line][col] = FILLED;
        if (isCorrect(line, col) && solve(squareIndex + 1)) {
            return true;
        }
        if (line == nbLines - 1) {
            this.printGrid(line, col);
        }

        // If the function has not returned yet, then no solution was found
        // with the current square filled. We must try empty.
        grid[line][col] = EMPTY;
        if (isCorrect(line, col) && solve(squareIndex + 1)) {
            return true;
        }

        // No solution was found for this square being either filled or empty,
        // the grid has no solution with the previous combination.
        return false;
    }

    /**
     * Test the correctness of the grid, even when it is not complete.
     * If the current line or column is complete, check that all hints are
     * represented.
     *
     * @return false if an error is visible, true otherwise.
     */
    private boolean isCorrect(int currentLine, int currentCol) {
        boolean isCorrect = true;
        for (int line = 0; line < nbLines; line++) {

            boolean lineIsComplete = (line < currentLine || ((line == currentLine) && (currentCol == nbColumns - 1)));

            if (!lineIsCorrect(line, lineIsComplete)) {
                // Any error in any line makes the grid incorrect.
                isCorrect = false;
            }
        }
        for (int col = 0; col < nbColumns; col++) {

            boolean colIsComplete = (currentLine == nbLines - 1);

            if (!colIsCorrect(col, colIsComplete)) {
                // Any error in any column makes the grid incorrect.
                isCorrect = false;
            }
        }

        // If no line or column is incorrect, then the grid is correct.
        return isCorrect;
    }

    /**
     * List the sizes of the blocks in a given line.
     *
     * @param line
     * @return the liste of the sizes of the blocks formed by the filled squares
     * in the given line.
     */
    private int[] getBlocksInLine(int line) {
        int tab[] = new int[nbColumns];
        int currentBlockIndex = 0;
        int currentBlockSize = 0;
        for (int col = 0; col < nbColumns; col++) {
            // Scan the line.

            if (grid[line][col] == FILLED) {
                currentBlockSize++;
            } else if (currentBlockSize > 0) {
                // Reached the end of a block.
                tab[currentBlockIndex] = currentBlockSize;
                currentBlockSize = 0;
                currentBlockIndex++;
            } else {
                // Scanned another empty square between blocks.
            }
        }
        return tab;
    }

    /**
     * Test if a given line is incomplete, correct or incorrect.
     *
     * @param lineIndex
     * @return true if the line is either complete or
     * incomplete-but-without-mistakes, false if at least one mistake exists.
     */
    private boolean lineIsCorrect(int lineIndex, boolean lineIsComplete) {

        // The current state of the grid portion we are examining
        int currentGridExtract[] = extractLineOrCol(lineIndex, true);
        int blockLengths[] = findBlockLengths(currentGridExtract);

        int hints[] = lineHints[lineIndex];

        boolean result = isCompatible(blockLengths, hints, lineIsComplete);
        lineChecks[lineIndex] = result;
        return result;
    }

    private boolean colIsCorrect(int colIndex, boolean colIsComplete) {
        // The current state of the grid portion we are examining
        int currentGridExtract[] = extractLineOrCol(colIndex, false);
        int blockLengths[] = findBlockLengths(currentGridExtract);

        int hints[] = colHints[colIndex];

        boolean result = isCompatible(blockLengths, hints, colIsComplete);
        colChecks[colIndex] = result;
        return result;

    }

    /**
     *
     * @param index The index of the line or column that we want to extract
     * @param processLines true when we want to process a line, false for a
     * column
     * @return an array representing the line or column from the grid.
     */
    private int[] extractLineOrCol(int index, boolean processLines) {

        int array[];

        if (processLines) {
            array = new int[nbColumns];
            for (int col = 0; col < nbColumns; col++) {
                array[col] = grid[index][col];
            }
        } else {
            array = new int[nbLines];
            for (int line = 0; line < nbLines; line++) {
                array[line] = grid[line][index];
            }
        }
        return array;
    }

    private int[] findBlockLengths(int[] currentGridExtract) {
        int result[] = new int[currentGridExtract.length];
        int currentBlockSize = 0;
        int iResult = 0;
        for (int i = 0; i < currentGridExtract.length; i++) {
            if (currentGridExtract[i] == FILLED) {
                currentBlockSize++;
            } else {
                if (currentBlockSize > 0) {
                    // End of a block
                    result[iResult] = currentBlockSize;
                    currentBlockSize = 0;
                    iResult++;
                }
            }
        }
        if (currentGridExtract[currentGridExtract.length - 1] == FILLED) {
            // The last block is not followed by a zero but by the end of the line.
            result[iResult] = currentBlockSize;
        }
        return trimArray(result);
    }

    /**
     * Check that the block length respect the hints.
     *
     * @param blockLengths
     * @param hints
     * @param mustBeExact when true, all hints must be found in the blocks.
     * @return
     */
    private boolean isCompatible(int[] blockLengths, int[] hints, boolean mustBeExact) {

        // There should not be more groups than there are hints.
        if (blockLengths.length > hints.length) {
            // Too many groups, error
            return false;
        }

        if (blockLengths.length == 0) {
            // Nothing in the line, correct unless we need it to be complete
            if (mustBeExact && hints.length > 0) {
                return false;
            } else {
                return true;
            }
        }

        // If there is at least one block, then the last block's size is constrained by the 'mustBeExact' parameter
        // Any block before that last one must always have precisely the right length.
        if (blockLengths.length > 0) {
            for (int i = 0; i < Math.min(hints.length - 1, blockLengths.length - 1); i++) {
                if (blockLengths[i] != hints[i]) { // Every block must be precisely the right length.
                    return false;
                }
            }

            // Last block:
            int lastBlockIndex = blockLengths.length - 1;
            if (mustBeExact) {
                if (blockLengths[lastBlockIndex] != hints[lastBlockIndex]) {
                    // Any different length is a grid error.
                    return false;
                }
            } else {
                if (blockLengths[lastBlockIndex] > hints[lastBlockIndex]) {
                    // Only a block too long constitutes an error.
                    return false;
                }
            }
        }

        // No block or last block is incorrect.
        return true;

    }

    /**
     * Remove the trailing zeroes of an array.
     *
     * @param array
     * @return
     */
    private int[] trimArray(int[] array) {
        int[] result = {};
        int length = array.length;
        if (length < 1) {
            result = array;
        } else {
            while (length > 0 && array[length - 1] == 0) {
                length--;
            }
            // Now length is the amount of elements before the trailing zeroes.
            result = new int[length];

            // Copy the meaningful values from array into result
            for (int i = 0; i < length; i++) {
                result[i] = array[i];
            }
            // Leave the trailing zeroes behind.
        }

        return result;
    }

    /**
     * Find the largest number of hints for lines or columns.
     *
     * @param checkLines when true, scan the line; when false, scan columns.
     * @return the size of the largest hint set for a given line or column
     */
    private int getNbMaxHints(boolean checkLines) {
        int max = 0;
        int hintsTab[][];
        int maxIndex;
        if (checkLines) {
            hintsTab = lineHints;
            maxIndex = nbLines;
        } else {
            hintsTab = colHints;
            maxIndex = nbColumns;
        }

        for (int i = 0; i < maxIndex; i++) {
            int nbOfNonZeroHints = trimArray(hintsTab[i]).length;
            if (nbOfNonZeroHints > max) {
                max = nbOfNonZeroHints;
            }
        }
        return max;
    }
}
