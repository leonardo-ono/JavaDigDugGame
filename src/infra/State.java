package infra;

import java.awt.Graphics2D;

/**
 * State class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public abstract class State<T> {

    protected final StateManager<T> manager;
    protected final String name;
    protected final T owner;

    public State(StateManager<T> manager, String name, T owner) {
        this.manager = manager;
        this.name = name;
        this.owner = owner;
    }
    
    public void init() {
    }

    public StateManager<T> getManager() {
        return manager;
    }
    
    public String getName() {
        return name;
    }

    public T getOwner() {
        return owner;
    }
    
    public void onEnter() {
        // implement your code here
    }
    
    public void update() {
        // implement your code here
    }

    public void draw(Graphics2D g) {
        // implement your code here
    }
    
}
