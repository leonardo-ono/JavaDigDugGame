package scene;

import infra.Scene;
import infra.StateManager;
import infra.renderer.Text;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * OLPresents (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class OLPresents extends Scene {
    
    private final String text = "O.L.  PRESENTS";
    private double frame;
    
    public OLPresents(StateManager<Scene> scenes) {
        super(scenes, "ol_presents", null);
    }
    
    @Override
    public void onEnter() {
        frame = 0;
    }

    @Override
    public void update() {
        frame += 0.2;
        if (frame > 40) {
            manager.switchTo("title");
        }
    }

    @Override
    public void draw(Graphics2D g) {
        int endIndex = (int) frame;
        if (endIndex > 14) {
            endIndex = 14;
        }
        Text.draw(g, text.substring(0, endIndex), 9, 16, Color.WHITE);
    }
    
}
