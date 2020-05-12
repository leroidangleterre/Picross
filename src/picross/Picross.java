package picross;

import javax.swing.JFrame;

/**
 *
 * @author arthurmanoha
 */
public class Picross {

    public static void main(String[] args) {

//        Grid g = new Grid("whale.txt");
        Grid g = new Grid("tank.txt");

        g.printGrid(0, 0);

        Window window = new Window(g);

        g.solve();
    }

}
