package scene;

import infra.Audio;
import infra.Direction;
import static infra.Direction.IDLE;
import static infra.Direction.LEFT;
import infra.GameInfo;
import infra.Input;
import infra.Resource;
import infra.Scene;
import infra.StateManager;
import infra.renderer.HUD;
import infra.Underground;
import infra.renderer.AnimationPlayer;
import infra.renderer.Text;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Title (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Title extends Scene {
    
    private final BufferedImage backgroundImage;
    
    private final TitleAnimation titleBackground;
    private final TitleAnimation title;
    private final TitleAnimation digdug;
    private final TitleAnimation fygar;
    private final TitleAnimation fygarFire;
    private final TitleAnimation namco;
    private final TitleAnimation pooka;
    
    public Title(StateManager<Scene> scenes) {
        super(scenes, "title", null);
        backgroundImage = Resource.getImage("background");
        
        titleBackground = Resource.getTitleAnimation("title_background");
        title = Resource.getTitleAnimation("title");
        digdug = Resource.getTitleAnimation("digdug");
        fygar = Resource.getTitleAnimation("fygar");
        fygarFire = Resource.getTitleAnimation("fygar_fire");
        namco = Resource.getTitleAnimation("namco");
        pooka = Resource.getTitleAnimation("pooka");
    }
    
    @Override
    public void onEnter() {
        GameInfo.reset();
        HUD.setBlink1UP(false);
        Underground.reset(-1);
        Audio.enable();

        titleBackground.reset();
        title.reset();
        digdug.reset();
        fygar.reset();
        fygarFire.reset();
        namco.reset();
        pooka.reset();
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
            return;
        }
        
        if (namco.frameState.frame >= 700) {
            manager.switchTo("hiscores");
        }
        
        titleBackground.update();
        title.update();
        digdug.update();
        fygar.update();
        fygarFire.update();
        namco.update();
        pooka.update();
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(backgroundImage, 0, 0, null);
        g.drawImage(Underground.getIMAGE(), 0, 0, null);

        titleBackground.draw(g);
        title.draw(g);
        digdug.draw(g);
        fygar.draw(g);
        fygarFire.draw(g);
        namco.draw(g);
        pooka.draw(g);
        
        Text.draw(g, "(C) 2020 O.L.", 10, 28, Color.RED);
        
        if (namco.frameState.frame >= 558) {
            Text.draw(g, "1982 ORIGINAL GAME BY", 5, 30, Color.RED);
        }
        
        if ((int) (System.nanoTime() * 0.000000002) % 2 == 0) {
            Text.draw(g, "PRESS SPACE TO START", 6, 16, Color.RED);
        }
        
        HUD.draw(g);
    }

    public static class FrameState {
        public int frame;
        public Boolean digging;
        public Direction direction;
        public Point position;
        public String animation;
        public String sound;
        public Boolean restart;
    }

    public static class TitleAnimation {
        String id;
        FrameState[] frameStates;
        FrameState frameState;
        AnimationPlayer animationPlayer;

        public TitleAnimation(String id, FrameState[] frameStates
                , AnimationPlayer animationPlayer) {
            
            this.id = id;
            this.frameStates = frameStates;
            this.animationPlayer = animationPlayer;
            
            frameState = new FrameState();
            frameState.position = new Point();
        }

        void update() {
            FrameState currentFrameState = frameStates[frameState.frame];
            if (currentFrameState != null) {
                frameState.digging = currentFrameState.digging != null 
                        ? currentFrameState.digging : frameState.digging;
                
                frameState.direction = currentFrameState.direction != null 
                        ? currentFrameState.direction : frameState.direction;
                
                if (currentFrameState.position != null ) {
                    frameState.position.setLocation(currentFrameState.position);
                }
                
                frameState.animation = currentFrameState.animation != null 
                        ? currentFrameState.animation : frameState.animation;

                frameState.restart = currentFrameState.restart != null 
                        ? currentFrameState.restart : frameState.restart;
                
                frameState.sound = currentFrameState.sound != null 
                        ? currentFrameState.sound : frameState.sound;
            }
            frameState.frame++;
            if (frameState.frame > frameStates.length - 1) {
                frameState.frame = frameStates.length - 1;
            }
            if (frameState.direction != null 
                    && (currentFrameState == null 
                        || currentFrameState.position == null)) {
                
                frameState.position.x += frameState.direction.getDx();
                frameState.position.y += frameState.direction.getDy();
            }
            if (frameState.animation != null) {
                animationPlayer.play(frameState.animation, frameState.restart);
                frameState.animation = null;
            }
            if (frameState.sound != null) {
                Audio.playSound(frameState.sound);
                frameState.sound = null;
            }
            if (frameState.digging != null && frameState.digging) {
                Underground.dig(
                        frameState.position.x, frameState.position.y, false);
            }
            if (frameState.direction == IDLE) {
                animationPlayer.setDirection(LEFT);
            }
            else {
                animationPlayer.setDirection(frameState.direction);
            }
            animationPlayer.update();
        }
        
        void draw(Graphics2D g) {
            animationPlayer.draw(
                    g, frameState.position.x, frameState.position.y);
        }
        
        void reset() {
            frameState.frame = 0;
            frameState.digging = null;
            frameState.direction = null;
            frameState.position.setLocation(0, 0);
            frameState.animation = null;
            frameState.restart = null;
            frameState.sound = null;
        }
        
    }
    
}
