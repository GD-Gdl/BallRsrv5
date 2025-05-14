package com.example.ballrsrv;

public class User {
    public String identifier;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }
}
