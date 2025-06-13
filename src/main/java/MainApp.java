import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class MainApp extends Application {
    private User loggedInUser;
    private String accessToken;
    private List<Song> currentSongList;  // To avoid fetching again for play/like

    @Override
    public void start(Stage primaryStage) throws Exception {
        accessToken = AccessTokenManager.getAccessToken();


        VBox loginLayout = new VBox(10);
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");

        loginLayout.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                loginButton
        );

        Scene loginScene = new Scene(loginLayout, 300, 200);


        VBox homeLayout = new VBox(10);
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");
        ListView<String> songListView = new ListView<>();
        Button playButton = new Button("Play Preview");
        Button likeButton = new Button("Like Song");

        homeLayout.getChildren().addAll(
                new Label("Search Songs:"), searchField,
                searchButton, songListView,
                playButton, likeButton
        );
        Scene homeScene = new Scene(homeLayout, 400, 400);


        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            try {
                Connection conn = DatabaseManager.getConnection();
                loggedInUser = Main.registerOrLogin(conn, username, password);
                conn.close();
                primaryStage.setScene(homeScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        searchButton.setOnAction(e -> {
            String query = searchField.getText();
            try {
                currentSongList = SpotifyService.searchSongs(query, accessToken);
                songListView.getItems().clear();
                for (Song song : currentSongList) {
                    songListView.getItems().add(song.getName() + " - " + song.getArtist());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        playButton.setOnAction(e -> {
            int index = songListView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && currentSongList != null) {
                try {
                    Song selectedSong = currentSongList.get(index);
                    if (selectedSong.getPreviewUrl() != null) {
                        Player.playPreview(selectedSong.getPreviewUrl());
                    } else {
                        System.out.println("No preview available.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        likeButton.setOnAction(e -> {
            int index = songListView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && currentSongList != null) {
                try {
                    Song selectedSong = currentSongList.get(index);

                    Connection conn = DatabaseManager.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO liked_songs (user_id, song_id, song_name, artist_name, preview_url) VALUES (?, ?, ?, ?, ?)"
                    );
                    stmt.setInt(1, loggedInUser.getUserId());
                    stmt.setString(2, selectedSong.getId());
                    stmt.setString(3, selectedSong.getName());
                    stmt.setString(4, selectedSong.getArtist());
                    stmt.setString(5, selectedSong.getPreviewUrl());
                    stmt.executeUpdate();
                    conn.close();

                    System.out.println("Liked successfully!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        primaryStage.setTitle("Spotify Clone");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


