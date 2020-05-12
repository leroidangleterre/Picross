package picross;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

/**
 * Swing interface to display the grid as a clean black and white image
 * as it is being solved.
 * Must interface with the Grid class to be updated.
 *
 * @author arthurmanoha
 */
public class GridPanel extends JPanel {

    private Grid theGrid;

    private int preferredWidth = 1000;
    private int preferredHeight = 720;

    private int squareSize = 15;

    public static final int FILLED = 1;
    public static final int EMPTY = 0;

    private Timer timer;
    private int delay = 0;
    private int period = 100; // ms

    public GridPanel(Grid g) {
        theGrid = g;
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, delay, period);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.gray);
        g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);

        Color squareColor;

        // Get a copy of the grid. Must not interfere for ConcurrentModification risk.
        int tab[][] = theGrid.getCopy();
        for (int line = 0; line < tab.length; line++) {
            for (int col = 0; col < tab[0].length; col++) {
                int value = tab[line][col];
                switch (value) {
                case FILLED:
                    squareColor = Color.black;
                    break;
                case EMPTY:
                    squareColor = Color.white;
                    break;
                default:
                    squareColor = Color.red;
                    break;
                }
                // TODO: adapt the coordinates to the actual dimensions of the window
                g.setColor(squareColor);// Speed gained if change done when necessary ? TBD.
                g.fillRect(col * squareSize, line * squareSize, squareSize, squareSize);
            }
        }
    }
}
