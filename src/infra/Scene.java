package infra;

import java.awt.Graphics2D;

/**
 * Scene class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Scene extends State<Scene> {

    public Scene(StateManager<Scene> manager, String name, Scene owner) {
        super(manager, name, owner);
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g) {
    }
    
}
