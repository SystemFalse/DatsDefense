package dats_defense;

import java.util.Date;

public class Player {
    int enemyBlockKills;
    Date gameEndedAt;
    int gold;
    String name;
    int points;
    int zombieKills;

    public int getEnemyBlockKills() {
        return enemyBlockKills;
    }

    public Date getGameEndedAt() {
        return gameEndedAt;
    }

    public int getGold() {
        return gold;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getZombieKills() {
        return zombieKills;
    }
}
