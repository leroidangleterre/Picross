/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package picross;

import javax.swing.JFrame;

/**
 *
 * @author arthurmanoha
 */
public class Window extends JFrame {

    private Grid grid;
    private GridPanel panel;

    public Window(Grid g) {

        setTitle("Picross Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        panel = new GridPanel(g);
        setContentPane(panel);

        setVisible(true);
        invalidate();

        grid = g;
    }

}
