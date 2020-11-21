package scene;

import entity.DigDug;
import entity.Fire;
import entity.Food;
import entity.Harpoon;
import entity.Rock;
import infra.Collidable;
import infra.Enemy;
import infra.Entity;
import infra.GameController;
import infra.GameInfo;
import infra.Resource;
import infra.Scene;
import infra.StateManager;
import infra.renderer.BonusPoints;
import infra.renderer.Flowers;
import infra.renderer.HUD;
import infra.Underground;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stage (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Stage extends Scene {
    
    private final BufferedImage stage;
    private final BufferedImage backgroundImage;
    private final List<Entity> entities = new ArrayList<>();
    private GameController gameController;
    private DigDug digdug;
    private Harpoon harpoon;
    private Food food; 
    
    public Stage(StateManager<Scene> scenes) {
        super(scenes, "stage", null);
        stage = Resource.getImage("stage");
        backgroundImage = Resource.getImage("background");
    }

    public DigDug getDigdug() {
        return digdug;
    }

    @Override
    public void init() {
    }
    
    @Override
    public void onEnter() {
        if (GameInfo.isFirstStagePlay()) {
            GameInfo.addScore(Underground.getDigScore());
            Underground.reset(GameInfo.getStage());
            createAllStageEntities();
        }
        else {
            reviveCurrentStageAliveEntities();
        }
        Flowers.setStage(GameInfo.getStage());
        BonusPoints.hideAll();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    private void createAllStageEntities() {
        entities.clear();
        
        gameController = new GameController(this);
        harpoon = new Harpoon(this);
        digdug = new DigDug(this, harpoon);
        food = new Food(this);
        
        entities.add(food);
        entities.add(harpoon);
        entities.add(digdug);
        entities.add(gameController);
        
        Resource.loadLevel(GameInfo.getStage1_12(), this);
        
        Collections.sort(entities);
        
        // init all entities
        entities.forEach(entity -> entity.init());
        
        System.gc();
    }
    
    private void reviveCurrentStageAliveEntities() {
        gameController.revive();
        digdug.revive();
        for (Entity entity : entities) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                if (GameInfo.getRemainingEnemyIds().contains(enemy.getId())) {
                    enemy.revive();
                }
            }
        }        
    }
    
    public boolean needsToWaitForNextStage() {
        for (Entity entity : entities) {
            // inAction rocks
            if (entity instanceof Rock) {
                Rock rock = (Rock) entity;
                if (rock.isInAction()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void update() {
        entities.forEach(entity -> {
            if (!entity.isDestroyed()) {
                entity.update();
            }
        });
        checkCollisions();
        BonusPoints.update();
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(backgroundImage, 0, 0, null);
        g.drawImage(Underground.getIMAGE(), 0, 0, null);
        // g.drawImage(Underground.getPATH_MASK(), 0, 0, null);
        // g.drawImage(stage, 16, 0, null); // for debug
        Flowers.draw(g);
        entities.forEach(entity -> {
            if (!entity.isDestroyed()) {
                entity.draw(g);
            }
        });
        BonusPoints.draw(g);
        HUD.draw(g);
        
        // debug
        //Text.draw(g, "remaining enemies: " + GameInfo.getRemainingEnemies(), 0, 1, Color.WHITE);
    }
    
    private void checkCollisions() {
        for (Entity e1 : entities) {
            for (Entity e2 : entities) {
                if (e1 != e2 && !e1.isDestroyed() && !e2.isDestroyed()
                    && e1 instanceof Collidable && e2 instanceof Collidable) {
                    
                    Collidable c1 = (Collidable) e1;
                    Collidable c2 = (Collidable) e2;
                    if (c1.collides(c2)) {
                        c1.onCollision(c2);
                        c2.onCollision(c1);
                    }
                }
            }
        }        
    }

    public void destroyAllStageEntities() {
        for (Entity entity : entities) {
            if (entity instanceof Food
                || entity instanceof Fire
                || entity instanceof Harpoon) {
                
                entity.destroy();
            }
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                if (enemy.isAlive() && !enemy.isDestroyed()) {
                    enemy.destroy();
                }
            }
        }        
    }
    
    public void activateAllEnemies() {
        for (Entity entity : entities) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                enemy.getStateManager().switchTo("chasing");
            }
        }        
    }

    public void deactivateAllEnemies() {
        for (Entity entity : entities) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                enemy.getStateManager().switchTo("idle");
            }
        }        
    }

    public void retreatLastLeftEnemy() {
        for (Entity entity : entities) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                if (enemy.isAlive() && !enemy.isDestroyed()) {
                    enemy.retreat();
                    return;
                }
            }
        }        
    }
    
    private final Rectangle colliderA = new Rectangle();
    private final Rectangle colliderB = new Rectangle();
    
    public boolean isBlockedByRock(int x, int y) {
        colliderA.setBounds(x - 8, y - 8, 16, 16);
        
        for (Entity entity : entities) {
            if (entity instanceof Rock) {
                Rock rock = (Rock) entity;
                colliderB.setBounds((int) (rock.getOriginalX() - 8)
                        , (int) (rock.getY() - 8), 16, 16);
                
                if (colliderA.intersects(colliderB) && !rock.isDestroyed()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean checkUnstableRockAbove(int x, int y) {
        for (Entity entity : entities) {
            if (entity instanceof Rock) {
                Rock rock = (Rock) entity;
                String rockCurrentState 
                        = rock.getStateManager().getCurrentState().getName();
                if ((int) rock.getOriginalX() == x && (int) rock.getY() < y
                    && (rockCurrentState.equals("unstable")
                    || rockCurrentState.equals("falling"))) {
                    
                    return true;
                }
            }
        }
        return false;
    }

    public void spawnFood() {
        food.spawn(120, 152, GameInfo.getStage());
    }

}
