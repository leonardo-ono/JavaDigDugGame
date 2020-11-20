package scene;

import infra.Audio;
import infra.GameInfo;
import infra.Input;
import infra.Resource;
import infra.Scene;
import infra.StateManager;
import infra.renderer.HUD;
import infra.Underground;
import infra.renderer.Text;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Title (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Title extends Scene {
    
    private final BufferedImage backgroundImage;
    private final BufferedImage titleImage;
    private final BufferedImage namcoImage;

    public Title(StateManager<Scene> scenes) {
        super(scenes, "title", null);
        backgroundImage = Resource.getImage("background");
        titleImage = Resource.getImage("title");
        namcoImage = Resource.getImage("namco_0");
    }
    
    @Override
    public void onEnter() {
        GameInfo.reset();
        HUD.setBlink1UP(false);
        Underground.reset(-1);
        Audio.enable();
    }

    @Override
    public void update() {
        if (Input.isKeyJustPressed(KeyEvent.VK_SPACE)) {
            GameInfo.reset();

            Audio.playSound("credit");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
            }

            manager.switchTo("stage");
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(backgroundImage, 0, 0, null);
        g.drawImage(Underground.getIMAGE(), 0, 0, null);
        g.drawImage(titleImage, 49, 47, null);
        
        Text.draw(g, "(C) O.L. 2020", 10, 28, Color.RED);
        Text.draw(g, "1982 ORIGINAL GAME BY", 5, 30, Color.RED);
        g.drawImage(namcoImage, 100, 256, null);
        
        if ((int) (System.nanoTime() * 0.000000002) % 2 == 0) {
            Text.draw(g, "PRESS SPACE TO START", 6, 15, Color.RED);
        }
        
        HUD.draw(g);
    }
    
}
