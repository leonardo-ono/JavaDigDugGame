package scene;

import com.sun.glass.events.KeyEvent;
import infra.GameInfo;
import infra.Input;
import infra.Resource;
import infra.Scene;
import infra.ScoreInfo;
import infra.ScoreInfo.HiscoreEntry;
import infra.StateManager;
import infra.Underground;
import infra.renderer.HUD;
import infra.renderer.Text;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Hiscores (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Hiscores extends Scene {
    
    private final BufferedImage titleImage;
    private long nextSceneTime;
    
    public Hiscores(StateManager<Scene> manager) {
        super(manager, "hiscores", null);
        titleImage = Resource.getImage("title");
    }

    @Override
    public void onEnter() {
        GameInfo.reset();
        HUD.setBlink1UP(false);
        Underground.reset(-1);
        HUD.setBlink1UP(false);
        nextSceneTime = System.currentTimeMillis() + 10000;
    }
    
    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= nextSceneTime 
                || Input.isKeyJustPressed(KeyEvent.VK_SPACE)) {
            
            manager.switchTo("title");
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(titleImage, 208, 60, -160, 48, null);
        
        Text.draw(g, "BEST 5", 13, 15, Color.WHITE);
        
        Text.draw(g, "SCORE ROUND NAME", 11, 21, Color.WHITE);
        
        Text.draw(g, "1ST", 5, 23, Color.WHITE);
        Text.draw(g, "2ND", 5, 25, Color.WHITE);
        Text.draw(g, "3RD", 5, 27, Color.WHITE);
        Text.draw(g, "4TH", 5, 29, Color.WHITE);
        Text.draw(g, "5TH", 5, 31, Color.WHITE);
        
        for (int p = 0; p < 5; p++) {
            HiscoreEntry player = ScoreInfo.HISCORES.get(p);
            Text.draw(g, player.toString(), 10, 23 + p * 2, Color.WHITE);
        }
        
        HUD.draw(g);
    }
    
}
