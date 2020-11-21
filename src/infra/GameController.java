package infra;

import entity.DigDug;
import infra.renderer.Text;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import scene.Stage;

/**
 * GameController class.
 * 
 * Responsible to managing the current state of game.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class GameController extends Entity<Stage> {
    
    private final StateManager<GameController> stateManager; 
    
    private static final Audio.SoundPlayer MUSIC;
    private static final Audio.SoundPlayer SOUND_ENEMY_MOVING;
    private int silenceFrames;
            
    static {
        MUSIC = new Audio.SoundPlayer();
        MUSIC.initialize();
        SOUND_ENEMY_MOVING = new Audio.SoundPlayer();
        SOUND_ENEMY_MOVING.initialize();
        SOUND_ENEMY_MOVING.play(Resource.getSound(
            "enemy_moving"), "enemy_moving", true, true);
}
    
    public GameController(Stage stage) {
        super(stage);
        stateManager = new StateManager<>();
        
        
        zorder = 1000;
    }

    public StateManager<GameController> getStateManager() {
        return stateManager;
    }

    @Override
    public void init() {
        stateManager.addState(new Ready(stateManager, this));
        stateManager.addState(new Playing(stateManager, this));
        stateManager.addState(new OneEnemyLeft(stateManager, this));
        stateManager.addState(new LevelCleared(stateManager, this));
        stateManager.addState(new DigDugDead(stateManager, this));
        stateManager.addState(new GameOver(stateManager, this));
        stateManager.switchTo("ready");
    }

    @Override
    public void update() {
        stateManager.update();
        checkNextLifeOrGameOver();
    }
    
    private void updateEnemyMovementSound() {
        DigDug digdug = scene.getDigdug();
        if (MUSIC.isPlaying() || !digdug.isAlive() || digdug.isDestroyed()) {
            SOUND_ENEMY_MOVING.pause();
            silenceFrames = 0;
        }
        else {
            silenceFrames++;
            if (silenceFrames > 30) {
                SOUND_ENEMY_MOVING.resume();
            }
        }
    }
    
    private void checkNextLifeOrGameOver() {
        DigDug digdug = scene.getDigdug();
        String currentStateName = stateManager.getCurrentState().getName(); 
        if (digdug.isDestroyed() 
                && !currentStateName.equals("level_cleared")
                && !currentStateName.equals("game_over")
                && !currentStateName.equals("digdug_dead")) {
            
            stateManager.switchTo("digdug_dead");
        }
        
        // debug
        //if (Input.isKeyJustPressed(KeyEvent.VK_K)) {
        //    stateManager.switchTo("level_cleared");
        //    BonusPoints.show(9999, 112, 144, BonusPoints.ENEMY_KILLED);
            //GameInfo.addScore(9500);
            //GameInfo.nextLevel();
            //GameInfo.nextLevel();
            //stateManager.switchTo("game_over");
        //}
        
        //if (Input.isKeyJustPressed(KeyEvent.VK_S)) {
        //    GameInfo.addScore(9950);
        //}
    }
    
    @Override
    public void draw(Graphics2D g) {
        stateManager.draw(g);
    }

    /**
     * Ready (State) class.
     */
    private class Ready extends State<GameController> {
        
        private long playTime;
        
        public Ready(StateManager stateManager, GameController owner) {
            super(stateManager, "ready", owner);
        }

        @Override
        public void onEnter() {
            Audio.disable();
            if (GameInfo.isFirstPlay()) {
                MUSIC.play(Resource.getSound(
                        "game_start"), "game_start", false);

                playTime = System.currentTimeMillis() + 8000;
            }
            else {
                playTime = System.currentTimeMillis() + 3000;
            }
            SOUND_ENEMY_MOVING.pause();
        }

        @Override
        public void update() {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= playTime) {
                owner.getStateManager().switchTo("playing");
            }
        }

        @Override
        public void draw(Graphics2D g) {
            Text.draw(g, "PLAYER 1", 11, 16, Color.WHITE);
            Text.draw(g, "READY", 12, 21, Color.WHITE);
        }
        
    }
    
    private class Playing extends State<GameController> {
        
        public Playing(StateManager stateManager, GameController owner) {
            super(stateManager, "playing", owner);
        }

        @Override
        public void onEnter() {
            Audio.enable();
            DigDug digdug = scene.getDigdug();
            MUSIC.play(Resource.getSound("digdug_walking")
                    , "digdug_walking", true, true);
            digdug.setMusic(MUSIC);
            digdug.getStateManager().switchTo("walking");
            SOUND_ENEMY_MOVING.pause();
            scene.activateAllEnemies();
        }

        @Override
        public void update() {
            DigDug digdug = scene.getDigdug();
            if (digdug.isAlive() && GameInfo.getRemainingEnemies() == 0
                    && !scene.needsToWaitForNextStage()) {
                
                owner.getStateManager().switchTo("level_cleared");
            }
            else if (digdug.isAlive() && GameInfo.getRemainingEnemies() == 1) { 
                owner.getStateManager().switchTo("one_enemy_left");
            }
            updateEnemyMovementSound();
        }
        
    }

    /**
     * OneEnemyLeft (State) class.
     */
    private class OneEnemyLeft extends State<GameController> {
        
        private long audioEnableTime;
        
        public OneEnemyLeft(StateManager stateManager, GameController owner) {
            super(stateManager, "one_enemy_left", owner);
        }

        @Override
        public void onEnter() {
            Audio.disable();
            DigDug digdug = scene.getDigdug();
            digdug.setMusic(null);
            scene.retreatLastLeftEnemy();
            MUSIC.play(Resource.getSound(
                    "enemy_bye_bye"), "enemy_bye_bye", false);
            SOUND_ENEMY_MOVING.pause();
            audioEnableTime = System.currentTimeMillis() + 2000;
        }

        @Override
        public void update() {
            long currentTime = System.currentTimeMillis();
            DigDug digdug = scene.getDigdug();
            
            if (Audio.isDisabled() && currentTime >= audioEnableTime) {
                Audio.enable();
                MUSIC.play(Resource.getSound("digdug_walking_fast")
                        , "digdug_walking_fast", true, true);
                digdug.setMusic(MUSIC);
            }
            
            if (digdug.isAlive() && GameInfo.getRemainingEnemies() == 0
                && !scene.needsToWaitForNextStage()) {
                
                owner.getStateManager().switchTo("level_cleared");
            }
            
            updateEnemyMovementSound();
        }
        
    }

    private class LevelCleared extends State<GameController> {
        
        private long nextLevelTime;
        
        public LevelCleared(StateManager stateManager, GameController owner) {
            super(stateManager, "level_cleared", owner);
        }

        @Override
        public void onEnter() {
            Audio.disable();
            DigDug digdug = scene.getDigdug();
            digdug.getStateManager().switchTo("idle");
            digdug.setMusic(null);
            MUSIC.play(Resource.getSound(
                    "stage_clear"), "stage_clear", false);
            SOUND_ENEMY_MOVING.pause();
            nextLevelTime = System.currentTimeMillis() + 4000;
        }

        @Override
        public void update() {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextLevelTime) {
                GameInfo.nextLevel();
                scene.getManager().switchTo("stage");
            }
        }
        
    }
    
    private class DigDugDead extends State<GameController> {
        
        private long checkConditionTime;

        public DigDugDead(StateManager stateManager, GameController owner) {
            super(stateManager, "digdug_dead", owner);
        }

        @Override
        public void onEnter() {
            MUSIC.stop();
            SOUND_ENEMY_MOVING.pause();
            DigDug digdug = scene.getDigdug();
            digdug.setMusic(null);
            checkConditionTime = System.currentTimeMillis() + 1000;
        }

        @Override
        public void update() {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= checkConditionTime
                    && !scene.needsToWaitForNextStage()) {
                
                boolean gameOver = GameInfo.nextLife();
                if (gameOver) {
                    stateManager.switchTo("game_over");
                }
                else {
                    // digdug dies at the same time as level is cleared
                    if (GameInfo.getRemainingEnemies() == 0) {
                        stateManager.switchTo("level_cleared");
                    }
                    else {
                        Audio.disable();
                        scene.getManager().switchTo("stage");
                    }
                }
            }
        }
        
    }
    
    private class GameOver extends State<GameController> {
        
        private long titleTime;

        public GameOver(StateManager stateManager, GameController owner) {
            super(stateManager, "game_over", owner);
        }

        @Override
        public void onEnter() {
            Audio.disable();
            DigDug digdug = scene.getDigdug();
            digdug.setMusic(null);
            MUSIC.play(Resource.getSound(
                    "game_over"), "game_over", false);
            SOUND_ENEMY_MOVING.pause();
            titleTime = System.currentTimeMillis() + 4000;
        }

        @Override
        public void update() {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= titleTime) {
                // hiscore
                if (GameInfo.getScore() > ScoreInfo.HISCORES.get(0).score) {
                    owner.getScene().getManager().switchTo("hiscore_top");
                }
                else if (GameInfo.getScore() > ScoreInfo.HISCORES.get(4).score) {
                    owner.getScene().getManager().switchTo("hiscore_enter_name");
                }
                else {
                    owner.getScene().getManager().switchTo("title");
                }
            }
        }

        @Override
        public void draw(Graphics2D g) {
            Text.draw(g, "GAME OVER", 11, 21, Color.WHITE);
        }
        
    }

    public void revive() {
        stateManager.switchTo("ready");
    }
    
}
