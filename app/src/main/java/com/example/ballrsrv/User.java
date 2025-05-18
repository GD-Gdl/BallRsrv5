package com.example.ballrsrv;

public class User {
    private String id;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String userType; // "admin" or "user"
    private String createdAt;
    private String lastLogin;

    // Required for Firebase
    public User() {}

    public User(String id, String email, String password, String fullName, 
                String phoneNumber, String userType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.createdAt = String.valueOf(System.currentTimeMillis());
        this.lastLogin = this.createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public boolean isAdmin() {
        return "admin".equals(userType);
    }
}
