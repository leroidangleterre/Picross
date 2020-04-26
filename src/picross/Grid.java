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
                    lineOrColIndex++;
                    // Read an actual line or column
                    String[] hintTab = text.split(" ");
                    int hintIndex = 0;
                    for (String hint : hintTab) {
                        // Fill the line or col table
                        if (isReadingLines) {
                            System.out.println("index: " + lineOrColIndex);
                            lineHints[lineOrColIndex][hintIndex] = Integer.valueOf(hint);
                            hintIndex++;
                        } else {
                            System.out.println("index: " + lineOrColIndex);
                            colHints[lineOrColIndex][hintIndex] = Integer.valueOf(hint);
                            hintIndex++;
                        }
                    }
                }
            }

            // Create an empty grid.
            grid = new int[nbLines][];
            for (int line = 0; line < nbLines; line++) {
                grid[line] = new int[nbColumns];
            }

            this.printGrid();

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
    private void printGrid() {

        int maxNbColHints = findMaxColHints();
        int maxNbLinesHints = findMaxLinesHints();

        // Quadrant NE
        for (int hintIndex = 0; hintIndex < maxNbColHints; hintIndex++) {
            System.out.print(" ");
            for (int j = 0; j < maxNbLinesHints; j++) {
                System.out.print(".  ");
            }
            for (int col = 0; col < nbColumns; col++) {
                System.out.print(String.format("%2d ", this.getColHint(col, hintIndex)));
            }
            System.out.println("");
        }

        System.out.print("  ");
        for (int i = 0; i < maxNbLinesHints; i++) {
            System.out.print("   ");
        }
        for (int i = 0; i < nbColumns; i++) {
            System.out.print("-  ");
        }
        System.out.println("");

        // Quadrants SW and SE
        for (int line = 0; line < nbLines; line++) {
            // Display the hints
            for (int hintIndex = 0; hintIndex < maxNbLinesHints; hintIndex++) {
                System.out.print(String.format("%2d ", this.getLineHint(line, hintIndex)));
            }

            System.out.print("|");

            // Display the grid values
            for (int col = 0; col < nbColumns; col++) {
                if (grid[line][col] == 0) {
                    System.out.print(" ? ");
                } else {
                    System.out.print(" O ");
                }
            }
            System.out.println("");
        }
    }

    private int findMaxColHints() {
        int max = 0;
        for (int col = 0; col < nbColumns; col++) {
            int currentNbHints = colHints[col].length;
            if (currentNbHints > max) {
                max = currentNbHints;
            }
        }
        return max;
    }

    private int findMaxLinesHints() {
        int max = 0;
        for (int line = 0; line < nbLines; line++) {
            int currentNbHints = lineHints[line].length;
            if (currentNbHints > max) {
                max = currentNbHints;
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

}
