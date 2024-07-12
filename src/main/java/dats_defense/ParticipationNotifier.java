package dats_defense;

import java.io.IOException;
import java.util.Date;

public class ParticipationNotifier {
    public static void main(String[] args) throws IOException {
        ZombiedefResponse rounds = DatsDefense.getZombiedef();
        while (!rounds.rounds.isEmpty()) {
            rounds.rounds.removeIf(r -> r.status.equals("ended"));
            boolean participated = false;
            for (int i = 0; i < rounds.rounds.size(); i++) {
                Date now = new Date();
                if (rounds.rounds.get(i).status.equals("active")) {
                    if (rounds.rounds.get(i).startAt.compareTo(now) >= 0 && !participated) {
                        try {
                            DatsDefense.putParticipate();
                            System.out.println("Participated in round " + rounds.rounds.get(i).name);
                            participated = true;
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                }
                if (rounds.rounds.get(i).endAt.compareTo(now) <= 0 && participated) {
                    participated = false;
                }
            }
        }
    }
}
