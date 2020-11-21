package infra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ScoreInfo class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class ScoreInfo {

    // extra life constants
    public static final int SCORE_EXTRA_LIFE_1 = 20000;
    public static final int SCORE_EXTRA_LIFE_2 = 60000;
    
    public static class HiscoreEntry implements Comparable<HiscoreEntry> {
        
        public String name;
        public int score;
        public int round;
        
        public HiscoreEntry(String name, int score, int round) {
            this.name = name;
            this.score = score;
            this.round = round;
        }

        @Override
        public int compareTo(HiscoreEntry o) {
            return o.score - score;
        }

        @Override
        public String toString() {
            String scoreStr = "      " + score;
            scoreStr = scoreStr.substring(
                    scoreStr.length() - 6, scoreStr.length());

            String roundStr = "      " + round;
            roundStr = roundStr.substring(
                    roundStr.length() - 5, roundStr.length());

            String nameStr = "      " + name;
            nameStr = nameStr.substring(
                    nameStr.length() - 4, nameStr.length());
            
            return scoreStr + " " + roundStr + " " + nameStr;
        }
        
    }

    public static final List<HiscoreEntry> HISCORES = new ArrayList<>();
    public static int newHiscorePlayerIndex;
    
    static {
        // initial hiscores
        addHiscore("N.N", 10000, 1);
        addHiscore("A.A", 9000, 1);
        addHiscore("M.M", 8000, 1);
        addHiscore("C.C", 7000, 1);
        addHiscore("O.O", 6000, 1);
    }
    
    public static int addHiscore(String name, int score, int round) {
        HiscoreEntry player = new HiscoreEntry(name, score, round);
        HISCORES.add(player);
        Collections.sort(HISCORES);
        int position = 0;
        for (int i = 0; i < HISCORES.size(); i++) {
            if (HISCORES.get(i).equals(player)) {
                newHiscorePlayerIndex = i;
                position = i;
                break;
            }
        }
        while (HISCORES.size() > 5) {
            HISCORES.remove(HISCORES.size() - 1);
        }
        return position;
    }
    
    public static boolean isHighscore() {
        return GameInfo.getStage() > HISCORES.get(4).score;
    }
 
    public static String getPlayerStr(int index) {
        String order = "  " + (index + 1);
        order = order.substring(order.length() - 2, order.length());
        HiscoreEntry player = HISCORES.get(index);
        String name = player.name.trim() + "   ";
        name = name.substring(0, 3);
        return order + ") " + name + " " + player.score;
    } 
    
}
