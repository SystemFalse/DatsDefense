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
}
