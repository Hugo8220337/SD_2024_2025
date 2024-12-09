package ipp.estg.models;


public class User {
    private int id;
    private final String username;
    private final String email;
    private final UserTypes userType;

    public User(int id, String username, String email, UserTypes userType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public UserTypes getUserType() {
        return userType;
    }
}
