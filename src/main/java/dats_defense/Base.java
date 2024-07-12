package dats_defense;

import java.util.UUID;

public class Base {
    int attack;
    int health;
    UUID id;
    boolean isHead;
    Point lastAttack;
    int range;
    int x;
    int y;

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public UUID getId() {
        return id;
    }

    public boolean isHead() {
        return isHead;
    }

    public Point getLastAttack() {
        return lastAttack;
    }

    public int getRange() {
        return range;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
