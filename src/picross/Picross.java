package picross;

/**
 *
 * @author arthurmanoha
 */
public class Picross {

    public static void main(String[] args) {

        Grid g = new Grid("grid1.txt");

        g.printGrid(0, 0);
//
//        int blockPosition[] = {0, 0, 0};
//        int hints[] = {1, 2, 1};
        g.solve();
//        int hintTab____[] = {1, 0, 1, 1, 0, 0, 0, 0};
//
//        int currentLine[] = {0, 0, 1, 0, 0, 1, 1, 0};
//        System.out.println("distance: " + DamerauLevenshtein.distance(hintTab____, currentLine));
//        System.out.println("Test: " + Grid.canConvert(currentLine, hintTab____) + ";");
    }

}
