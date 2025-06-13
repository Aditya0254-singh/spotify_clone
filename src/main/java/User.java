public class User {
    private int userId;
    private String username;
    private String password; // in real apps, never store plain passwords

    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
}