package dats_defense;

import java.util.UUID;

public class Zombie {
    public enum Direction {
        up, down, left, right
    }
    public enum ZombieType {
        normal, fast, bomber, liner, juggernaut, chaos_knight
    }

    int attack;
    Direction direction;
    int health;
    UUID id;
    int speed;
    ZombieType type;
    int waitTurns;
    int x;
    int y;

    public int getAttack() {
        return attack;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getHealth() {
        return health;
    }

    public UUID getId() {
        return id;
    }

    public int getSpeed() {
        return speed;
    }

    public ZombieType getType() {
        return type;
    }

    public int getWaitTurns() {
        return waitTurns;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
