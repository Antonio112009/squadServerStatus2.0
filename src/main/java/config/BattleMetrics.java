package config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class BattleMetrics {

    public static JsonElement getServerInfo(String serverId){
        InputStreamReader reader = null;
        try  {
            URL url = null;
            try {
                url = new URL("https://api.battlemetrics.com/servers/" + serverId);
            } catch (MalformedURLException ignored) {}
            try {
                assert url != null;
                reader = new InputStreamReader(url.openStream());

            } catch (IOException ignore) {
            }
        } catch (Exception ignore) {

        }

        if (reader != null) {
            return new JsonParser().parse(reader);
        }
        return null;
    }
}
