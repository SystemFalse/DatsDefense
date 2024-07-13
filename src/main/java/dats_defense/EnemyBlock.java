package dats_defense;

public class EnemyBlock {
    int attack;
    int health;
    boolean isHead;
    Point lastAttack;
    String name;
    int x;
    int y;

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public boolean isHead() {
        return isHead;
    }

    public Point getLastAttack() {
        return lastAttack;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
