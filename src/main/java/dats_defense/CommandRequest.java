package dats_defense;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CommandRequest {
    List<Attack> attack;
    List<Point> build;
    Point moveBase;

    public CommandRequest() {
        attack = new ArrayList<>();
        build = new ArrayList<>();
    }

    public List<Attack> getAttack() {
        return attack;
    }

    public List<Point> getBuild() {
        return build;
    }

    public Point getMoveBase() {
        return moveBase;
    }

    public static class Deserializer implements JsonDeserializer<CommandRequest> {
        @Override
        public CommandRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            CommandRequest request = new CommandRequest();
            if (json.getAsJsonObject().has("attack") && !json.getAsJsonObject().get("attack").isJsonNull()) {
                JsonArray attack = json.getAsJsonObject().getAsJsonArray("attack");
                request.attack = new ArrayList<>(attack.size());
                for (JsonElement e : attack) {
                    request.attack.add(context.deserialize(e, Attack.class));
                }
            } else {
                request.attack = List.of();
            }
            if (json.getAsJsonObject().has("build") && !json.getAsJsonObject().get("build").isJsonNull()) {
                JsonArray build = json.getAsJsonObject().getAsJsonArray("build");
                request.build = new ArrayList<>(build.size());
                for (JsonElement e : build) {
                    request.build.add(context.deserialize(e, Point.class));
                }
            } else {
                request.build = List.of();
            }
            if (json.getAsJsonObject().has("moveBase") && !json.getAsJsonObject().get("moveBase").isJsonNull()) {
                request.moveBase = context.deserialize(json.getAsJsonObject().get("moveBase"), Point.class);
            } else {
                request.moveBase = null;
            }
            return request;
        }
    }
}
