package picross;

/**
 *
 * @author arthurmanoha
 */
public class Picross {

    public static void main(String[] args) {

//        Grid g = new Grid("grid0.txt");
//
//        g.printGrid();
//
//        g.solve();
        int tab1[] = DamerauLevenshtein.stringToTab("conjuguaison");
        int tab2[] = DamerauLevenshtein.stringToTab("conjugaisons");

        int distance = DamerauLevenshtein.distance(tab1, tab2);
        System.out.println("distance: " + distance);
    }

}
