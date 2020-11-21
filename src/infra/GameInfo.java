package infra;

import static infra.ScoreInfo.SCORE_EXTRA_LIFE_1;
import static infra.ScoreInfo.SCORE_EXTRA_LIFE_2;
import java.util.HashSet;
import java.util.Set;

/**
 * GameInfo class.
 * 
 * Informations about the game: lives, current stage, score, hiscore, etc.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class GameInfo {
    
    private static final int NUMBER_OF_LIVES = 3;
    
    private static int stage = 1;
    private static int lives = NUMBER_OF_LIVES;
    private static int score = 10001;
    private static boolean firstPlay = true;
    private static boolean firstStagePlay = true;
    private static boolean stageBonusAvailable = true;
    private static int nextExtraLife;
    
    private static final Set<String> REMAINING_ENEMY_IDS 
            = new HashSet<String>();

    static {
        reset();
    }
    
    public static int getStage() {
        return stage;
    }

    public static int getStage1_12() {
        int s = 0;
        if (stage <= 12) {
            s = stage;
        }
        else {
            s = (stage - 13) % 5 + 8;
        }
        return s;
    }

    public static int getLives() {
        return lives;
    }
    
    public static String getStageStr() {
        String stageStr = "     " + stage;
        return stageStr.substring(stageStr.length() - 5, stageStr.length());
    }

    public static int getScore() {
        return score + Underground.getDigScore();
    }

    public static String getScoreStr() {
        String scoreStr = "" + getScore();
        if (scoreStr.length() < 2) {
            scoreStr = "00" + scoreStr;
            scoreStr = scoreStr.substring(
                    scoreStr.length() - 2, scoreStr.length());
        }
        scoreStr = "      " + scoreStr;
        return scoreStr.substring(scoreStr.length() - 6, scoreStr.length());
    }

    public static String getHiscoreStr() {
        int hiscore = ScoreInfo.HISCORES.get(0).score;
        String hiscoreStr = "" + hiscore;
        if (hiscoreStr.length() < 2) {
            hiscoreStr = "00" + hiscoreStr;
            hiscoreStr = hiscoreStr.substring(
                    hiscoreStr.length() - 2, hiscoreStr.length());
        }
        hiscoreStr = "      " + hiscoreStr;
        return hiscoreStr.substring(
                hiscoreStr.length() - 6, hiscoreStr.length());
    }

    public static boolean isFirstPlay() {
        return firstPlay;
    }

    public static boolean isFirstStagePlay() {
        return firstStagePlay;
    }

    public static boolean isStageBonusAvailable() {
        return stageBonusAvailable;
    }

    public static void consumeStageBonus() {
        stageBonusAvailable = false;
    }

    public static void addScore(int point) {
        score += point;
        checkExtraLife();
    }

    public static Set<String> getRemainingEnemyIds() {
        return REMAINING_ENEMY_IDS;
    }

    public static int getRemainingEnemies() {
        return REMAINING_ENEMY_IDS.size();
    }
    
    public static void addEnemyId(String id) {
        REMAINING_ENEMY_IDS.add(id);
    }
    
    public static void removeEnemyId(String id) {
        REMAINING_ENEMY_IDS.remove(id);
    }

    public static void reset() {
        lives = 3;
        stage = 1;
        score = 0;
        firstPlay = true;
        firstStagePlay = true;
        stageBonusAvailable = true;
        REMAINING_ENEMY_IDS.clear();
        nextExtraLife = SCORE_EXTRA_LIFE_1;
    }
    
    // return "is game over ?"
    public static boolean nextLife() {
        if (lives > 0) {
            firstPlay = false;
            firstStagePlay = false;
            lives--;
            return lives == 0;
        }
        return true;
    }

    public static void nextLevel() {
        stage++;
        firstPlay = false;
        firstStagePlay = true;
        stageBonusAvailable = true;
        REMAINING_ENEMY_IDS.clear();
    }

    public static void checkExtraLife() {
        if (GameInfo.getScore() >= nextExtraLife) {
            nextExtraLife += SCORE_EXTRA_LIFE_2;
            lives++;
            Audio.playSound("credit");
        }
    }
    
}
