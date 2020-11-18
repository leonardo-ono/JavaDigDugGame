package main;

import infra.Display;
import static infra.Settings.VIEWPORT_HEIGHT;
import static infra.Settings.VIEWPORT_WIDTH;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Main class.
 * 
 * Game entry point.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Display view = new Display();
            view.setPreferredSize(
                    new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
            
            JFrame frame = new JFrame();
            frame.setTitle("Java 2D Dig Dug Game");
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }   
    
}
