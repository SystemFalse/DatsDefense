package dats_defense;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UnitsResponse {
    List<Base> base;
    List<EnemyBlock> enemyBlocks;
    Player player;
    String realmName;
    int turn;
    int turnEndsInMs;
    List<Zombie> zombies;

    public List<Base> getBase() {
        return base;
    }

    public List<EnemyBlock> getEnemyBlocks() {
        return enemyBlocks;
    }

    public Player getPlayer() {
        return player;
    }

    public String getRealmName() {
        return realmName;
    }

    public int getTurn() {
        return turn;
    }

    public int getTurnEndsInMs() {
        return turnEndsInMs;
    }

    public List<Zombie> getZombies() {
        return zombies;
    }

    public static class Deserializer implements JsonDeserializer<UnitsResponse> {
        @Override
        public UnitsResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            UnitsResponse response = new UnitsResponse();
            if (json.getAsJsonObject().has("base") && !json.getAsJsonObject().get("base").isJsonNull()) {
                JsonArray base = json.getAsJsonObject().getAsJsonArray("base");
                response.base = new ArrayList<>(base.size());
                for (JsonElement e : base) {
                    response.base.add(context.deserialize(e, Base.class));
                }
            } else {
                response.base = List.of();
            }
            if (json.getAsJsonObject().has("enemyBlocks") && !json.getAsJsonObject().get("enemyBlocks").isJsonNull()) {
                JsonArray enemyBlocks = json.getAsJsonObject().getAsJsonArray("enemyBlocks");
                response.enemyBlocks = new ArrayList<>(enemyBlocks.size());
                for (JsonElement e : enemyBlocks) {
                    response.enemyBlocks.add(context.deserialize(e, EnemyBlock.class));
                }
            } else {
                response.enemyBlocks = List.of();
            }
            if (json.getAsJsonObject().has("player") && !json.getAsJsonObject().get("player").isJsonNull()) {
                response.player = context.deserialize(json.getAsJsonObject().get("player"), Player.class);
            } else {
                response.player = new Player();
            }
            response.realmName = json.getAsJsonObject().get("realmName").getAsString();
            response.turn = json.getAsJsonObject().get("turn").getAsInt();
            response.turnEndsInMs = json.getAsJsonObject().get("turnEndsInMs").getAsInt();
            if (json.getAsJsonObject().has("zombies") && !json.getAsJsonObject().get("zombies").isJsonNull()) {
                JsonArray zombies = json.getAsJsonObject().getAsJsonArray("zombies");
                response.zombies = new ArrayList<>(zombies.size());
                for (JsonElement e : zombies) {
                    response.zombies.add(context.deserialize(e, Zombie.class));
                }
            } else {
                response.zombies = List.of();
            }
            return response;
        }
    }
}
