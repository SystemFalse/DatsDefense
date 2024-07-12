package dats_defense;

import java.util.UUID;

public class Attack {
    UUID blockId;
    Point target;

    public Attack() {
    }

    public Attack(UUID blockId, Point target) {
        this.blockId = blockId;
        this.target = target;
    }

    public UUID getBlockId() {
        return blockId;
    }

    public Point getTarget() {
        return target;
    }
}
