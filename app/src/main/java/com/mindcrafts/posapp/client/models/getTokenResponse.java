package com.mindcrafts.posapp.client.models;



public class getTokenResponse {

    private boolean isSuccess;
    private String message;
    private String token;

    // Empty constructor
    public getTokenResponse() {
    }

    // Parameterized constructor
    public getTokenResponse(boolean isSuccess, String message, String token) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.token = token;
    }

    // Getter and Setter for isSuccess
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter and Setter for token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

