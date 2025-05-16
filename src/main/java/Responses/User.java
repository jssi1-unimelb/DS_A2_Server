package Responses;

public class User {
    private String id;
    private String username;
    public String role;


    public User(String id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
