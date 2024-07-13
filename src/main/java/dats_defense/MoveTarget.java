package dats_defense;

public class MoveTarget implements Comparable<MoveTarget> {
    int attackingBases;
    int attackingZombies;

    public MoveTarget(int attackingBases) {
        this.attackingBases = attackingBases;
        this.attackingZombies = 0;
    }

    public int getAttackingBases() {
        return attackingBases;
    }

    public int isAttackingZombies() {
        return attackingZombies;
    }

    @Override
    public int compareTo(MoveTarget o) {
        return Integer.compare(attackingBases + attackingZombies, o.attackingBases + o.attackingZombies);
    }
}
