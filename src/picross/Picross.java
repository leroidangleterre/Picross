package picross;

/**
 *
 * @author arthurmanoha
 */
public class Picross {

    public static void main(String[] args) {

        Grid g = new Grid("grid0.txt");

        g.printGrid();

        g.solve();
//        int hintTab____[] = {1, 0, 1, 1, 1, 0, 1, 0};
//
//        int currentLine[] = {1, 0, 1, 0, 1, 0, 1, 0};
//
//        System.out.println("Test: " + DamerauLevenshtein.canConvert(hintTab____, currentLine) + ";");
    }

}
