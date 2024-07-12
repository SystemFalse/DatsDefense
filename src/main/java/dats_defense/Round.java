package dats_defense;

import java.util.Date;

public class Round {
    int duration;
    Date endAt;
    String name;
    int repeat;
    Date startAt;
    String status;

    public int getDuration() {
        return duration;
    }

    public Date getEndAt() {
        return endAt;
    }

    public String getName() {
        return name;
    }

    public int getRepeat() {
        return repeat;
    }

    public Date getStartAt() {
        return startAt;
    }

    public String getStatus() {
        return status;
    }
}
