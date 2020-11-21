package scene;

import com.sun.glass.events.KeyEvent;
import infra.Audio;
import infra.GameInfo;
import infra.Input;
import infra.Resource;
import infra.Scene;
import infra.StateManager;
import infra.renderer.HUD;
import infra.renderer.Text;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * HiscoreTop (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class HiscoreTop extends Scene {
    
    private long nextSceneTime;
    
    public HiscoreTop(StateManager<Scene> manager) {
        super(manager, "hiscore_top", null);
    }

    @Override
    public void onEnter() {
        Audio.enable();
        Audio.playSound("high_score");
        HUD.setBlink1UP(false);
        nextSceneTime = System.currentTimeMillis() + 10500;
    }
    
    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= nextSceneTime) {
            Audio.stopSound("high_score");
            manager.switchTo("hiscore_enter_name");
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Text.draw(g, "CONGRATULATIONS !!", 7, 6, Color.RED);
        
        Text.draw(g, "HIGHEST SCORE", 9, 9, Color.YELLOW);
        
        Text.draw(g, "GO FOR THE WORLD RECORD", 5, 22, Color.WHITE);
        Text.draw(g, "NOW !", 13, 24, Color.WHITE);
        
        AffineTransform ot = g.getTransform();
        
        g.translate(0, -8);
        g.scale(4, 4);
        
        if ((int) (System.nanoTime() * 0.0000000025) % 2 == 0) {
            Text.draw(g, GameInfo.getScoreStr(), 1, 4, Color.GREEN);
        }
        
        g.setTransform(ot);
        HUD.draw(g);
    }
    
}
