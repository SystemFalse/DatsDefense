package dats_defense;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CommandResponse {
    CommandRequest acceptedCommands;
    List<String> errors;

    public CommandRequest getAcceptedCommands() {
        return acceptedCommands;
    }

    public List<String> getErrors() {
        return errors;
    }

    public static class Deserializer implements JsonDeserializer<CommandResponse> {
        @Override
        public CommandResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            CommandResponse response = new CommandResponse();
            JsonElement acceptedCommands = json.getAsJsonObject().get("acceptedCommands");
            if (!acceptedCommands.isJsonNull()) {
                response.acceptedCommands = context.deserialize(acceptedCommands, CommandRequest.class);
            } else {
                response.acceptedCommands = new CommandRequest();
            }
            if (json.getAsJsonObject().has("errors") && !json.getAsJsonObject().get("errors").isJsonNull()) {
                JsonArray errors = json.getAsJsonObject().getAsJsonArray("errors");
                response.errors = new ArrayList<>();
                for (JsonElement error : errors) {
                    response.errors.add(error.getAsString());
                }
            } else {
                response.errors = new ArrayList<>();
            }
            return response;
        }
    }
}
