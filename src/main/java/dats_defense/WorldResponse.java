package dats_defense;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorldResponse {
    String realmName;
    List<ZPot> zpots;

    public String getRealmName() {
        return realmName;
    }

    public List<ZPot> getZpots() {
        return zpots;
    }

    public static class Deserializer implements JsonDeserializer<WorldResponse> {
        @Override
        public WorldResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            WorldResponse response = new WorldResponse();
            response.realmName = json.getAsJsonObject().get("realmName").getAsString();
            JsonArray zplots = json.getAsJsonObject().get("zpots").getAsJsonArray();
            response.zpots = new ArrayList<>(zplots.size());
            for (JsonElement zplot : zplots) {
                response.zpots.add(context.deserialize(zplot, ZPot.class));
            }
            return response;
        }
    }
}
