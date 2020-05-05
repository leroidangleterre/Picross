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

    public static char notProcessedCharacter = '.';
    public static char filledCharacter = 0x0870; // Nice square but empty
    public static char emptyCharacter = 'X';

    private static final int NOT_PROCESSED = 0;
    private static final int FILLED = 1;
    private static final int EMPTY = 2;

    int nbLines;
    int nbColumns;
    int grid[][];
    int lineHints[][];
    int colHints[][];

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
                    lineHints = new int[nbLines][]; // NB: see if we can do nbLines/2
                    colHints = new int[nbColumns][];
                    for (int i = 0; i < nbLines; i++) {
                        lineHints[i] = new int[nbColumns];
                    }
                    for (int j = 0; j < nbColumns; j++) {
                        colHints[j] = new int[nbLines];
                    }
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
            moveHintsToEndOfTables();

            // Create an empty grid.
            grid = new int[nbLines][];
            for (int line = 0; line < nbLines; line++) {
                grid[line] = new int[nbColumns];
                for (int col = 0; col < nbColumns; col++) {
                    grid[line][col] = NOT_PROCESSED;
                }
            }

            System.out.println("Filling grid for test purposes only");
            grid[2][2] = FILLED;
            grid[2][4] = FILLED;
            grid[2][5] = FILLED;
            grid[2][6] = FILLED;
            grid[0][2] = FILLED;
            grid[9][2] = FILLED;

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
     */
    public void printGrid() {

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
                case NOT_PROCESSED:
                    System.out.print(" " + notProcessedCharacter + " ");
                    break;
                case EMPTY:
                    System.out.print(" " + emptyCharacter + " ");
                    break;
                case FILLED:
                    System.out.print(" " + filledCharacter + " ");
                    break;
                default:
                    System.out.print(" ? ");
                    break;
                }
            }
            System.out.println("|");
        }

        for (int i = 0; i < lineHintStartIndex; i++) {
            System.out.print("   ");
        }
        System.out.print("  ");
        for (int i = 0; i < nbColumns; i++) {
            System.out.print("-  ");
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

    /**
     * Get the n-th hint for the given line.
     *
     * @param line
     * @param rank
     * @return
     */
    private int getLineHint(int line, int rank) {
        if (lineHints[line].length <= rank) {
            return -1;
        }
        // Do not take into account the leading zeroes.
        int rankForZero = 0;
        while (lineHints[line][rankForZero] == 0) {
            System.out.println("lineHints[" + line + "][" + rankForZero + "] == 0");
            rankForZero++;
            rank++;
        }
        try {
            System.out.print("returning lineHints[" + line + "][" + rank + "] which is ");
            System.out.println(lineHints[line][rank]);
            return lineHints[line][rank];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException: " + e);
            return -1;
        }
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

    /**
     * Move the hints closer to the gird, just for a better display
     *
     */
    private void moveHintsToEndOfTables() {

        for (int hintTab[] : colHints) {
            moveValuesToEndOfTab(hintTab);
        }
        for (int hintTab[] : lineHints) {
            moveValuesToEndOfTab(hintTab);

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

    /**
     * Backtrack solving.
     *
     */
    public void solve() {
        System.out.println("solve();");
        this.solve(0, true);
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
    private boolean solve(int squareIndex, boolean forward) {

        int line = squareIndex / nbColumns;
        int col = squareIndex - line * nbColumns;

        System.out.println("Solving line " + line + ", col " + col);
        this.printGrid();
        try {
            System.in.read();
        } catch (IOException e) {
            System.out.println("grid wait error...");
        }

        if (squareIndex == 0 && !forward) {
            // Limit case: going backward to the first square means no solution
            return false;
        } else if (squareIndex == nbLines * nbColumns) {
            // Limit case: reaching the end of the grid means that a solution was found.
            return true;
        } else {
            // General case
            switch (grid[line][col]) {
            case NOT_PROCESSED:
                grid[line][col] = FILLED;
                if (isCorrect()) {
                    return solve(squareIndex + 1, true); // Go to the next square
                } else {
                    return solve(squareIndex, forward); // Try another value for the current square
                }
            case FILLED:
                grid[line][col] = EMPTY;
                if (isCorrect()) {
                    return solve(squareIndex + 1, true); // Go to the next square
                } else {
                    return solve(squareIndex, forward); // Try another value for the current square
                }
            case EMPTY:
                grid[line][col] = NOT_PROCESSED;
                return solve(squareIndex - 1, false); // BACKTRACK
            }
        }
        // Should not reach here... TODO write this function with a single return
        return false;
    }

    /**
     * Test the correctness of the grid, even when it is not complete.
     *
     * @return false if an error is visible, true otherwise.
     */
    private boolean isCorrect() {
        System.out.println("Testing correctness of lines");
        for (int line = 0; line < nbLines; line++) {
            if (!lineIsCorrect(line)) {
                // Any error in any line makes the grid incorrect.
                return false;
            }
        }
        for (int col = 0; col < nbColumns; col++) {
            if (!colIsCorrect(col)) {
                // Any error in any column makes the grid incorrect.
                return false;
            }
        }

        // If no line or column is incorrect, then the grid is correct.
        return true;
    }

    /**
     * Check that the grid is completely solved.
     *
     * @return true when all hints are verified.
     */
    private boolean isSolved() {
        return false;
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
     * incomplete-but-without-mistakes, false if at elast one mistake exists.
     */
    private boolean lineIsCorrect(int lineIndex) {

        System.out.println("    Testing correctness of line " + lineIndex);

        // The hints converted as an array
        int tabHints[] = makeTabFromHints(lineIndex, true);

        // The current state of the grid portion we are examining
        int currentGridExtract[] = extractLineOrCol(lineIndex, true);

        boolean possible = DamerauLevenshtein.canConvert(tabHints, currentGridExtract);

        System.out.println((possible ? "P" : "Imp") + "ossible to convert line " + lineIndex);
        return possible;
    }

    private boolean colIsCorrect(int col) {
        return true;
    }

    /**
     * Create a tab from the hints of the grid.
     * Example: if the hints are 1 3 2, then the tab is
     * {1, 0, 1, 1, 1, 0, 1, 1, 0,..., 0} (a block of 1, a zero, a block of 3, a
     * zero, ...)
     *
     * @param line the index of the line or column
     * @param processLine when true, the function processes a line of the grid;
     * when false, a column.
     * @return
     */
    private int[] makeTabFromHints(int line, boolean processLine) {
        int tab[];
        int indexInTab = 0;
        int hintsToConvert[];
        if (processLine) {
            hintsToConvert = lineHints[line];
            tab = new int[nbColumns];
        } else {
            hintsToConvert = colHints[line];
            tab = new int[nbLines];
        }
        for (int hint : hintsToConvert) {
            if (hint > 0) {
                // Add several consecutive ones so that they form a block correcponding to the hint.
                for (int i = 0; i < hint; i++) {
                    tab[indexInTab] = 1;
                    indexInTab++;
                }
                tab[indexInTab] = 0; // End of the block;
                indexInTab++;
            }
        }
        return tab;
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
                array[line] = grid[index][line];
            }
        }
        return array;
    }
}
