package dats_defense;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZombiedefResponse {
    String gameName;
    Date now;
    List<Round> rounds;

    public String getGameName() {
        return gameName;
    }

    public Date getNow() {
        return now;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public static class Deserializer implements JsonDeserializer<ZombiedefResponse> {
        @Override
        public ZombiedefResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ZombiedefResponse response = new ZombiedefResponse();
            response.gameName = json.getAsJsonObject().get("gameName").getAsString();
            response.now = context.deserialize(json.getAsJsonObject().get("now"), Date.class);
            JsonArray rounds = json.getAsJsonObject().get("rounds").getAsJsonArray();
            response.rounds = new ArrayList<>(rounds.size());
            for (JsonElement round : rounds) {
                response.rounds.add(context.deserialize(round, Round.class));
            }
            return response;
        }
    }
}
