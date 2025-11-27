package com.mindcrafts.posapp.client.models;


public class LoginRequest {
    private String username;
    private String password;
    private String clientType;

    // Default constructor (required for serialization/deserialization)
    public LoginRequest() {
    }

    // Parameterized constructor
    public LoginRequest(String username, String password, String clientType) {
        this.username = username;
        this.password = password;
        this.clientType = clientType;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

}

