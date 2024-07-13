package dats_defense;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DatsDefense {
    public static final URL COMMAND_URL;
    public static final URL PARTICIPATE_URL;
    public static final URL UNITS_URL;
    public static final URL WORLD_URL;
    public static final URL ZOMDIDEF_URL;

    public static final Gson GSON;

    static {
        try {
            COMMAND_URL = URI.create("https://games.datsteam.dev/play/zombidef/command").toURL();
            PARTICIPATE_URL = URI.create("https://games.datsteam.dev/play/zombidef/participate").toURL();
            UNITS_URL = URI.create("https://games.datsteam.dev/play/zombidef/units").toURL();
            WORLD_URL = URI.create("https://games.datsteam.dev/play/zombidef/world").toURL();
            ZOMDIDEF_URL = URI.create("https://games.datsteam.dev/rounds/zombidef").toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        GSON = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(CommandResponse.class, new CommandResponse.Deserializer())
                .registerTypeAdapter(UnitsResponse.class, new UnitsResponse.Deserializer())
                .registerTypeAdapter(WorldResponse.class, new WorldResponse.Deserializer())
                .registerTypeAdapter(ZombiedefResponse.class, new ZombiedefResponse.Deserializer())
                .create();
    }

    public static void main(String[] args) throws Exception {
//        ZombiedefResponse zombiedef = getZombiedef();
//        System.out.println("Zombiedef: " + GSON.toJson(zombiedef));

//        putParticipate();
        System.out.println("Units: " + GSON.toJson(getUnits()));
        System.out.println("World: " + GSON.toJson(getWorld()));
    }

    public static CommandResponse postCommand(CommandRequest request) throws IOException {
        HttpURLConnection con = (HttpURLConnection) COMMAND_URL.openConnection();
        con.setRequestMethod("POST");
        con.addRequestProperty("X-Auth-Token", "6690bdb9ca7b86690bdb9ca7bc");
        con.addRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.getOutputStream().write(GSON.toJson(request).getBytes(StandardCharsets.UTF_8));
        checkCode(con);
        return GSON.fromJson(new InputStreamReader(con.getInputStream()), CommandResponse.class);
    }

    public static void putParticipate() throws IOException {
        HttpURLConnection con = (HttpURLConnection) PARTICIPATE_URL.openConnection();
        con.setRequestMethod("PUT");
        con.addRequestProperty("X-Auth-Token", "6690bdb9ca7b86690bdb9ca7bc");
        con.addRequestProperty("Content-Type", "application/json");
        checkCode(con);
    }

    public static WorldResponse getWorld() throws IOException {
        HttpURLConnection con = (HttpURLConnection) WORLD_URL.openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("X-Auth-Token", "6690bdb9ca7b86690bdb9ca7bc");
        checkCode(con);
        return GSON.fromJson(new InputStreamReader(con.getInputStream()), WorldResponse.class);
    }

    public static UnitsResponse getUnits() throws IOException {
        HttpURLConnection con = (HttpURLConnection) UNITS_URL.openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("X-Auth-Token", "6690bdb9ca7b86690bdb9ca7bc");
        checkCode(con);
        return GSON.fromJson(new InputStreamReader(con.getInputStream()), UnitsResponse.class);
    }

    public static ZombiedefResponse getZombiedef() throws IOException {
        HttpURLConnection con = (HttpURLConnection) ZOMDIDEF_URL.openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("X-Auth-Token", "6690bdb9ca7b86690bdb9ca7bc");
        checkCode(con);
        return GSON.fromJson(new InputStreamReader(con.getInputStream()), ZombiedefResponse.class);
    }

    private static void checkCode(HttpURLConnection con) throws IOException {
        int code = con.getResponseCode();
        if (code != 200) {
            Error error = GSON.fromJson(new InputStreamReader(con.getErrorStream()), Error.class);
            throw new IOException(code + ": " + error.getError());
        }
    }
}
