package dats_defense;

import java.util.HashSet;

public class AttackTarget {
    public enum Target {
        ENEMY_HEAD, ZOMBIE_LINER, ENEMY, ZOMBIE_JUGGERNAUT, ZOMBIE_BOMBER, ZOMBIE_CHAOS_KNIGHT, ZOMBIE_FAST, ZOMBIE_NORMAL
    }

    Target target;
    int health;
    HashSet<Base> attackingBases;

    public AttackTarget(EnemyBlock enemy, Base attackingBase) {
        target = enemy.isHead ? Target.ENEMY_HEAD : Target.ENEMY;
        health = enemy.health;
        attackingBases = new HashSet<>();
        attackingBases.add(attackingBase);
    }

    public AttackTarget(Zombie zombie, Base attackingBase) {
        switch (zombie.type) {
            case normal -> target = Target.ZOMBIE_NORMAL;
            case fast -> target = Target.ZOMBIE_FAST;
            case bomber -> target = Target.ZOMBIE_BOMBER;
            case chaos_knight -> target = Target.ZOMBIE_CHAOS_KNIGHT;
            case liner -> target = Target.ZOMBIE_LINER;
            case juggernaut -> target = Target.ZOMBIE_JUGGERNAUT;
        }
        health = zombie.health;
        attackingBases = new HashSet<>();
        attackingBases.add(attackingBase);
    }

    public Target getTarget() {
        return target;
    }

    public HashSet<Base> getAttackingBases() {
        return attackingBases;
    }
}
