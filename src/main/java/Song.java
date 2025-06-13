public class Song {
    private String id;
    private String name;
    private String artist;
    private String previewUrl;

    public Song(String id, String name, String artist, String previewUrl) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.previewUrl = previewUrl;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getArtist() { return artist; }
    public String getPreviewUrl() { return previewUrl; }
}