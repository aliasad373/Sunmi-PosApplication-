package com.mindcrafts.posapp.client.models;


import java.util.List;

public class User {
    private int id;
    private String username;
    private String name;
    private String user_type;
    private List<List<String>> roles;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public List<List<String>> getRoles() {
        return roles;
    }

    public void setRoles(List<List<String>> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", user_type='" + user_type + '\'' +
                ", roles=" + roles +
                '}';
    }
}

