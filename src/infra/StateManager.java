package infra;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * StateManager class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class StateManager<T> {
    
    private final Map<String, State<T>> states = new HashMap<>();
    private State<T> currentState;
    private State<T> nextState;

    public StateManager() {
    }

    public State<T> getCurrentState() {
        return currentState;
    }
    
    public void addState(State state) {
        states.put(state.getName(), state);
    }
    
    public void removeState(String stateName) {
        states.remove(stateName);
    }

    public void initAll() {
        states.keySet().forEach(key -> states.get(key).init());
    }
    
    public void switchTo(String stateName) { 
        if (currentState == null) {
            currentState = states.get(stateName);
            currentState.onEnter();
        }
        else {
            nextState = states.get(stateName);
        }
    }
    
    public void update() {
        if (nextState != null) {
            currentState = nextState;
            nextState = null;
            currentState.onEnter();
        }
        if (currentState != null) {
            currentState.update();
        }
    }

    public void draw(Graphics2D g) {
        if (currentState != null) {
            currentState.draw(g);
        }
    }
    
}
