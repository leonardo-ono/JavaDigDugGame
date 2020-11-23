package infra.renderer;

import infra.Direction;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * AnimationPlayer class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class AnimationPlayer {

    public static class Animation {
        public String name;
        public int startFrame;
        public int endFrame;
        public double speed;
        public boolean looping;
        public double currentFrame;
        public boolean finished;
        public boolean paused;

        public Animation(String name, int startFrame
                , int endFrame, double speed, boolean looping) {
            
            this.name = name;
            this.currentFrame = startFrame;
            this.startFrame = startFrame;
            this.endFrame = endFrame;
            this.looping = looping;
            this.speed = speed;
        }
    }

    private final Map<String, Animation> animations = new HashMap<>();
    private final Sprite sprite;
    
    private Animation currentAnimation;
    
    public AnimationPlayer(String resource, int size) {
        sprite = new Sprite(resource, size);
    }
    
    public AnimationPlayer(String resource, int sizeX, int sizeY) {
        sprite = new Sprite(resource, sizeX, sizeY);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public boolean isFinished() {
        return currentAnimation != null ? currentAnimation.finished : true;
    }
    
    public void addAnimation(String animationName, int startFrame
            , int endFrame, double speed, boolean looping) {
        
        animations.put(animationName, new Animation(animationName
                , startFrame, endFrame, speed, looping));
    }
    
    public void play(String animationName, boolean restartFrame) {
        currentAnimation = animations.get(animationName);
        if (restartFrame) {
            currentAnimation.currentFrame = currentAnimation.startFrame;
        }
        currentAnimation.finished = false;
        currentAnimation.paused = false;
    }
    
    public void play(String animationName) {
        play(animationName, false);
    }
    
    public void update() {
        if (currentAnimation != null) {
            if (currentAnimation.paused) {
                return;
            }
            
            currentAnimation.currentFrame += currentAnimation.speed;
            if (currentAnimation.currentFrame >= currentAnimation.endFrame + 1){
                if (currentAnimation.looping) {
                    currentAnimation.currentFrame = currentAnimation.startFrame;
                }
                else {
                    currentAnimation.currentFrame = currentAnimation.endFrame;
                    currentAnimation.finished = true;
                }
            }
            sprite.setFrame((int) currentAnimation.currentFrame);
        }
    }
    
    public void draw(Graphics2D g, int x, int y) {
        sprite.draw(g, x, y);
    }

    public void setDirection(Direction direction) {
        sprite.setDirection(direction);
    }

    public void pause() {
        if (currentAnimation != null) {
            currentAnimation.paused = true;
        }
    }
    
}
