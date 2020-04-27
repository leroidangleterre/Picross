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

    public static char emptyCharacter = '.';
    public static char filledCharacter = 'O';

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
                int hint = this.getLineHint(line, hintIndex);
                if (hint == 0) {
                    System.out.print("   "); // Spaceholder for empty hint
                } else {
                    System.out.print(String.format("%2d ", hint));
                }
            }

            System.out.print("|");

            // Display the grid values
            for (int col = 0; col < nbColumns; col++) {
                if (grid[line][col] == 0) {
                    System.out.print(" " + emptyCharacter + " ");
                } else {
                    System.out.print(" " + filledCharacter + " ");
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
        if (lineHints[line].length < rank) {
            return -1;
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

}
