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

        int colHintStartIndex = nbLines - findMaxColHints();
        int lineHintStartIndex = findMaxLineHints();

        // Quadrant NE
        for (int hintIndex = colHintStartIndex; hintIndex < nbLines; hintIndex++) {
            for (int j = 0; j < lineHintStartIndex; j++) {
                System.out.print("   "); // Place the column indicators above the grid
            }
            System.out.print(" "); // Take into account the vertical separator after the line indications
            for (int col = 0; col < nbColumns; col++) {
                int hint = this.getColHint(col, hintIndex);
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
        for (int line = 0; line < nbLines; line++) {
            // Display the hints
            for (int hintIndex = nbColumns - findMaxLineHints(); hintIndex < nbColumns; hintIndex++) {
                int hint = lineHints[line][hintIndex];// this.getLineHint(line, hintIndex);
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
     * To be used only after moveHintsToEndOfTables().
     *
     * @return
     */
    private int findMaxColHints() {
        int max = 0;

        for (int col = 0; col < nbColumns; col++) {

            int nbHints = nbLines;
            int i = 0;
            while (i < nbLines && colHints[col][i] == 0) {
                // this position does not contain a hint, so this is one fewer possible hint.
                nbHints--;
                i++;
            }
            if (nbHints > max) {
                max = nbHints;
            }
        }
        return max;
    }

    /**
     * To be used only after moveHintsToEndOfTables().
     *
     * @return
     */
    private int findMaxLineHints() {
        int max = 0;

        for (int line = 0; line < nbLines; line++) {

            int nbHints = nbColumns;
            int i = 0;
            while (i < nbColumns && lineHints[line][i] == 0) {
                // this position does not contain a hint, so this is one fewer possible hint.
                nbHints--;
                i++;
            }
            if (nbHints > max) {
                max = nbHints;
            }
        }
        return max;
    }

    /**
     * Get the n-th hint for the given column.
     *
     * @param col
     * @param rank
     * @return
     */
    private int getColHint(int col, int rank) {
        if (colHints[col].length < rank) {
            return -1;
        }
        return colHints[col][rank];
    }

//    /**
//     * Get the n-th hint for the given line.
//     *
//     * @param line
//     * @param rank
//     * @return
//     */
//    private int getLineHint(int line, int rank) {
//        if (lineHints[line].length <= rank) {
//            return -1;
//        }
//        // Do not take into account the leading zeroes.
//        int rankForZero = 0;
//        while (lineHints[line][rankForZero] == 0) {
//            rankForZero++;
//            rank++;
//        }
//        try {
//            System.out.println(lineHints[line][rank]);
//            return lineHints[line][rank];
//        } catch (ArrayIndexOutOfBoundsException e) {
//            return -1;
//        }
//    }
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

//    /**
//     * Move the hints closer to the gird, just for a better display
//     *
//     */
//    private void moveHintsToEndOfTables() {
//
//        for (int hintTab[] : colHints) {
//            moveValuesToEndOfTab(hintTab);
//        }
//        for (int hintTab[] : lineHints) {
//            moveValuesToEndOfTab(hintTab);
//
//        }
//    }
    private boolean tabIsOnlyZeroes(int[] tab) {
        for (int i = 0; i < tab.length; i++) {
            if (tab[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private void waitForKeypressed() {
//        STEP++;
//        COUNT++;
//        if (STEP >= NB_STEPS_BEFORE_DISPLAY) {
        try {
            System.out.println("press key");
            System.in.read();
        } catch (IOException e) {
            System.out.println("grid wait error...");
        }
//            STEP = 0;
//            MUST_DISPLAY = true;
//    }
    }

    /**
     * Backtrack solving.
     *
     */
    public void solve() {
        System.out.println("solve();");
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

        STEP++;
        COUNT++;
        if (STEP >= NB_STEPS_BEFORE_DISPLAY) {
            STEP = 0;
            MUST_DISPLAY = true;

            if (MUST_DISPLAY) {
                System.out.println("solve(" + squareIndex + ", " + COUNT + ");");
                System.out.println("printing grid");
                this.printGrid(line, col);
                MUST_DISPLAY = false;
                waitForKeypressed();
            }
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

        int nbBlocksInLine = getBlocksInLine(currentLine).length;
        int nbBlocksInCol = getBlocksInColumn(currentCol).length;
        int nbHintsInLine = trimArray(lineHints[currentLine]).length;
        int nbHintsInCol = trimArray(colHints[currentCol]).length;

        if (grid[currentLine][currentCol] == FILLED) {
            // Check that we did not create more line-groups than necessary
            if (nbBlocksInLine > nbHintsInLine) {
                // Too many blocks for this line
                return false;
            }
            if (nbBlocksInCol > nbHintsInCol) {
                // Too many blocks for this column
                return false;
            }

            // Check that the current group is not too long.
            // Test line
            int currentSize = getBlocksInLine(currentLine)[nbBlocksInLine - 1];// Last currently changing block
            int hint = lineHints[currentLine][nbBlocksInLine - 1]; // Corresponding hint
            if (currentSize > hint) {
                // Block is too long for hint.
                return false;
            }
            // Test column
            try {
                currentSize = getBlocksInColumn(currentCol)[nbBlocksInCol - 1];// Last currently changing block
                hint = colHints[currentCol][nbBlocksInCol - 1]; // Corresponding hint
            } catch (ArrayIndexOutOfBoundsException e) {
                printGrid(currentLine, currentCol);
                return false;
            }
            if (currentSize > hint) {
                // Block is too long for hint.
                return false;
            }
        } else {
            // Check that the current block is not too short.
            int currentSize;
            int hint;

            // Test line
            if (nbBlocksInLine >= 1) {
                currentSize = getBlocksInLine(currentLine)[nbBlocksInLine - 1];// Last currently changing block
                hint = lineHints[currentLine][nbBlocksInLine - 1]; // Corresponding hint
                if (currentSize < hint) {
                    // Block is too short for hint.
                    return false;
                }
            }
            // Test column
            if (nbBlocksInCol >= 1) {
                currentSize = getBlocksInColumn(currentCol)[nbBlocksInCol - 1];// Last currently changing block
                hint = colHints[currentCol][nbBlocksInCol - 1]; // Corresponding hint
                if (currentSize < hint) {
                    // Block is too short for hint.
                    return false;
                }
            }
        }

        if (currentCol == nbColumns - 1) {

            // Check an empty line
            if (lineIsEmpty(currentLine)) {
                if (lineHints[currentLine][0] != 0) {
                    // Empty line with at least one non-zero hint is an error.
                    return false;
                }
            }

            // Check that there is the right amount of blocks
            int nbHints = trimArray(lineHints[currentLine]).length;
            int nbBlocks = getBlocksInLine(currentLine).length;
            if (nbBlocks != nbHints) {
                return false;
            }

            for (int i = 0; i < nbBlocks; i++) {
                if (getBlocksInLine(currentLine)[i] != lineHints[currentLine][i]) {
                    // This block does not have the required length.
                    return false;
                }
            }
        }

        if (currentLine == nbLines - 1) {
            // Col is complete, must verify the hints.

            if (colIsEmpty(currentCol)) {
                if (colHints[currentCol][0] != 0) {
                    // Empty col with at least one non-zero hint is an error.
                    return false;
                }
            }

            for (int i = 0; i < nbBlocksInCol; i++) {
                if (getBlocksInColumn(currentCol)[i] != colHints[currentCol][i]) {
                    // This block does not have the required length.
                    return false;
                }
            }
        }

        // If we reach this point, then no mistake has been found.
        return true;
    }

    private int[] getBlocksInLine(int line) {
        return getBlocksInLineOrCols(line, true);
    }

    private int[] getBlocksInColumn(int col) {
        return getBlocksInLineOrCols(col, false);
    }

    /**
     * List the sizes of the blocks in a given line.
     *
     * @param line the number of the line or column
     * @param checkLines true if we want lines, false for columns
     * @return the list of the sizes of the blocks formed by the filled squares
     * in the given line.
     */
    private int[] getBlocksInLineOrCols(int line, boolean checkLines) {
        int tab[];
        if (checkLines) {
            tab = new int[nbColumns];
        } else {
            tab = new int[nbLines];
        }
        int currentBlockIndex = 0;
        int currentBlockSize = 0;
        int maxIndex = (checkLines ? nbColumns : nbLines);
        for (int index = 0; index < maxIndex; index++) {
            // Scan the line or column.

            int testedValue = checkLines ? grid[line][index] : grid[index][line];

            if (testedValue == FILLED) {
                currentBlockSize++;
            } else if (currentBlockSize > 0) {
                // Reached the end of a block.
                tab[currentBlockIndex] = currentBlockSize;
                currentBlockSize = 0;
                currentBlockIndex++;
            }
            // Else, Scanned another empty square between blocks.
        }
        if (grid[line][nbColumns - 1] == FILLED) {
            // The last block terminates not with a EMPTY, but with the end of the array.
            tab[currentBlockIndex] = currentBlockSize;
        }
        return trimArray(tab);
    }

//    /**
//     * Test if a given line is incomplete, correct or incorrect.
//     *
//     * @param lineIndex
//     * @return true if the line is either complete or
//     * incomplete-but-without-mistakes, false if at least one mistake exists.
//     */
//    private boolean lineIsCorrect(int lineIndex, boolean lineIsComplete) {
//
//        // The current state of the grid portion we are examining
//        int currentGridExtract[] = extractLineOrCol(lineIndex, true);
//        int blockLengths[] = findBlockLengths(currentGridExtract);
//
//        int hints[] = lineHints[lineIndex];
//
//        boolean result = isCompatible(blockLengths, hints, lineIsComplete);
//        lineChecks[lineIndex] = result;
//        return result;
//    }
//
//    private boolean colIsCorrect(int colIndex, boolean colIsComplete) {
//        // The current state of the grid portion we are examining
//        int currentGridExtract[] = extractLineOrCol(colIndex, false);
//        int blockLengths[] = findBlockLengths(currentGridExtract);
//
//        int hints[] = colHints[colIndex];
//
//        boolean result = isCompatible(blockLengths, hints, colIsComplete);
//        colChecks[colIndex] = result;
//        return result;
//
//    }
//    /**
//     *
//     * @param index The index of the line or column that we want to extract
//     * @param processLines true when we want to process a line, false for a
//     * column
//     * @return an array representing the line or column from the grid.
//     */
//    private int[] extractLineOrCol(int index, boolean processLines) {
//
//        int array[];
//
//        if (processLines) {
//            array = new int[nbColumns];
//            for (int col = 0; col < nbColumns; col++) {
//                array[col] = grid[index][col];
//            }
//        } else {
//            array = new int[nbLines];
//            for (int line = 0; line < nbLines; line++) {
//                array[line] = grid[line][index];
//            }
//        }
//        return array;
//    }
//    private int[] findBlockLengths(int[] currentGridExtract) {
//        int result[] = new int[currentGridExtract.length];
//        int currentBlockSize = 0;
//        int iResult = 0;
//        for (int i = 0; i < currentGridExtract.length; i++) {
//            if (currentGridExtract[i] == FILLED) {
//                currentBlockSize++;
//            } else {
//                if (currentBlockSize > 0) {
//                    // End of a block
//                    result[iResult] = currentBlockSize;
//                    currentBlockSize = 0;
//                    iResult++;
//                }
//            }
//        }
//        if (currentGridExtract[currentGridExtract.length - 1] == FILLED) {
//            // The last block is not followed by a zero but by the end of the line.
//            result[iResult] = currentBlockSize;
//        }
//        return trimArray(result);
//    }
//    /**
//     * Check that the block length respect the hints.
//     *
//     * @param blockLengths
//     * @param hints
//     * @param mustBeExact when true, all hints must be found in the blocks.
//     * @return
//     */
//    private boolean isCompatible(int[] blockLengths, int[] hints, boolean mustBeExact) {
//
//        // There should not be more groups than there are hints.
//        if (blockLengths.length > hints.length) {
//            // Too many groups, error
//            return false;
//        }
//
//        if (blockLengths.length == 0) {
//            // Nothing in the line, correct unless we need it to be complete
//            if (mustBeExact && hints.length > 0) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//
////        System.out.println("    checking with " + blockLengths.length + " blocks and " + hints.length + " hints.");
////        System.out.println("First block: " + blockLengths[0]);
////        System.out.println("First hint: " + hints[0]);
//        // If there is at least one block, then the last block's size is constrained by the 'mustBeExact' parameter
//        // Any block before that last one must always have precisely the right length.
//        if (blockLengths.length > 0) {
//            for (int i = 0; i < Math.min(hints.length - 1, blockLengths.length - 1); i++) {
////                System.out.println("    i = " + i);
////                System.out.println("blockLengths[" + i + "] = " + blockLengths[i]);
////                System.out.println("hints[" + i + "] = " + hints[i]);
//                if (blockLengths[i] != hints[i]) { // Every block must be precisely the right length.
//                    return false;
//                }
//            }
//
//            // Last block:
//            int lastBlockIndex = blockLengths.length - 1;
//            if (mustBeExact) {
//                if (blockLengths[lastBlockIndex] != hints[lastBlockIndex]) {
//                    // Any different length is a grid error.
//                    return false;
//                }
//            } else {
//                if (blockLengths[lastBlockIndex] > hints[lastBlockIndex]) {
//                    // Only a block too long constitutes an error.
//                    return false;
//                }
//            }
//        }
//
//        // No block or last block is incorrect.
//        return true;
//
//    }
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

    private boolean lineIsEmpty(int line) {
        int col = 0;
        while (col < nbColumns && grid[line][col] == EMPTY) {
            col++;
        }
        if (col == nbColumns) {
            // Reach the end of the line.
            return true;
        } else {
            // Stopped on a non-empty square before the end of the line.
            return false;
        }
    }

    private boolean colIsEmpty(int col) {
        int line = 0;
        while (line < nbLines && grid[line][col] == EMPTY) {
            line++;
        }
        if (line == nbLines) {
            // Reach the end of the column.
            return true;
        } else {
            // Stopped on a non-empty square before the end of the column.
            return false;
        }
    }
}
