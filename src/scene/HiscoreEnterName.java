package scene;

import infra.Audio;
import infra.GameInfo;
import infra.Input;
import infra.Scene;
import infra.ScoreInfo;
import infra.ScoreInfo.HiscoreEntry;
import infra.StateManager;
import infra.renderer.HUD;
import infra.renderer.Text;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * HiscoreEnterName (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class HiscoreEnterName extends Scene implements KeyListener {
    
    private final char[] playerInitials = new char[3];
    private int cursorIndex;
    private int scorePosition;
    
    public HiscoreEnterName(StateManager<Scene> manager) {
        super(manager, "hiscore_enter_name", null);
        Input.setListener(this);
    }

    @Override
    public void onEnter() {
        scorePosition = ScoreInfo.addHiscore(
                "", GameInfo.getScore(), GameInfo.getStage());
        
        cursorIndex = 0;
        playerInitials[0] = ' ';
        playerInitials[1] = ' ';
        playerInitials[2] = ' ';
        
        HUD.setBlink1UP(false);
        
        Audio.enable();
        Audio.playSound("name_entry", true);
    }
    
    @Override
    public void update() {
        ScoreInfo.HISCORES.get(scorePosition).name = 
            "" + playerInitials[0] + playerInitials[1] + playerInitials[2];
        
        if (cursorIndex == 3 && Input.isKeyPressed(KeyEvent.VK_ENTER)) {
            Audio.stopSound("name_entry");
            manager.switchTo("hiscores");
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Text.draw(g, "ENTER YOUR INITIALS !", 6, 12, Color.YELLOW);
        Text.draw(g, "SCORE  ROUND  NAME", 7, 15, Color.YELLOW);

        String l = GameInfo.getScoreStr() + "  " + GameInfo.getStageStr();
        Text.draw(g, l, 6, 17, Color.WHITE);
        
        Text.draw(g, "" 
                + playerInitials[0] + playerInitials[1] + playerInitials[2]
                , 22, 17, Color.RED);

        if ((int) (System.nanoTime() * 0.0000000025) % 2 == 0) {
            Text.draw(g, "_", 22 + cursorIndex, 17, Color.RED);
        }
        
        Text.draw(g, "SCORE ROUND NAME", 11, 21, Color.WHITE);
        
        Text.draw(g, "1ST", 5, 23, getPositionColor(0));
        Text.draw(g, "2ND", 5, 25, getPositionColor(1));
        Text.draw(g, "3RD", 5, 27, getPositionColor(2));
        Text.draw(g, "4TH", 5, 29, getPositionColor(3));
        Text.draw(g, "5TH", 5, 31, getPositionColor(4));
        
        for (int p = 0; p < 5; p++) {
            HiscoreEntry player = ScoreInfo.HISCORES.get(p);
            Text.draw(g, player.toString()
                    , 10, 23 + p * 2, getPositionColor(p));
        }
        
        HUD.draw(g);
    }
    
    private Color getPositionColor(int position) {
        return position == scorePosition ? Color.YELLOW : Color.WHITE;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (cursorIndex > 0 && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            playerInitials[--cursorIndex] = ' ';
        }
        else if (Character.isAlphabetic(e.getKeyChar()) && cursorIndex < 3) {
            playerInitials[cursorIndex++] = 
                Character.toUpperCase(e.getKeyChar());
        }        
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
}
