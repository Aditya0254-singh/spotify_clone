import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class SpotifyService {
    public static List<Song> searchSongs(String query, String accessToken) throws Exception {
        List<Song> songs = new ArrayList<>();
        String urlStr = "https://api.spotify.com/v1/search?q=" + URLEncoder.encode(query, "UTF-8") + "&type=track&limit=10";
        URL url = new URL(urlStr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        JSONArray items = json.getJSONObject("tracks").getJSONArray("items");

        for (int i = 0; i < items.length(); i++) {
            JSONObject track = items.getJSONObject(i);
            String id = track.getString("id");
            String name = track.getString("name");
            String artist = track.getJSONArray("artists").getJSONObject(0).getString("name");
            String previewUrl = track.optString("preview_url", null); // may be null sometimes
            songs.add(new Song(id, name, artist, previewUrl));
        }

        return songs;
    }
}
