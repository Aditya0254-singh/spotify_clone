import java.util.*;
import java.sql.*;

public class Main {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String accessToken = AccessTokenManager.getAccessToken(); // Assuming you have this class

            // DB Connection
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/spotify_clone", "root", "yourpassword"
            );

            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();

            User user = registerOrLogin(conn, username, password);

            System.out.println("Search a song:");
            String query = scanner.nextLine();

            List<Song> songs = SpotifyService.searchSongs(query, accessToken);

            for (int i = 0; i < songs.size(); i++) {
                System.out.println((i + 1) + ". " + songs.get(i).getName() + " - " + songs.get(i).getArtist());
            }

            System.out.println("Do you want to see your liked songs? (yes/no)");
            String seeLiked = scanner.next();

            if (seeLiked.equalsIgnoreCase("yes")) {
                showLikedSongs(conn, user.getUserId());
            }

            System.out.println("Select song number to play preview:");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the newline after nextInt()

            Song selectedSong = songs.get(choice - 1);

            if (selectedSong.getPreviewUrl() != null) {
                Player.playPreview(selectedSong.getPreviewUrl());
            } else {
                System.out.println("No preview available for this song.");
            }

            System.out.println("Do you want to like this song? (yes/no)");
            String likeChoice = scanner.next();

            if (likeChoice.equalsIgnoreCase("yes")) {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO liked_songs (user_id, song_id, song_name, artist_name, preview_url) VALUES (?, ?, ?, ?, ?)"
                );
                stmt.setInt(1, user.getUserId());
                stmt.setString(2, selectedSong.getId());
                stmt.setString(3, selectedSong.getName());
                stmt.setString(4, selectedSong.getArtist());
                stmt.setString(5, selectedSong.getPreviewUrl());
                stmt.executeUpdate();
                System.out.println("Song liked successfully!");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User registerOrLogin(Connection conn, String username, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?"
        );
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password")
            );
        } else {
            stmt = conn.prepareStatement(
                    "INSERT INTO users (username, password) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return new User(generatedKeys.getInt(1), username, password);
            } else {
                throw new SQLException("User creation failed.");
            }
        }
    }

    private static void showLikedSongs(Connection conn, int userId) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT song_name, artist_name FROM liked_songs WHERE user_id = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Your Liked Songs:");
            int count = 1;
            while (rs.next()) {
                String songName = rs.getString("song_name");
                String artistName = rs.getString("artist_name");
                System.out.println(count++ + ". " + songName + " - " + artistName);
            }

            if (count == 1) {
                System.out.println("No liked songs yet.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
