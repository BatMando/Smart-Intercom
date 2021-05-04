package com.example.smartintercom.Models;

public class User {
    String id;
    String username;
    String password;
    String LiveFeed;
    String Status;
    String port;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getLiveFeed() {
        return LiveFeed;
    }

    public void setLiveFeed(String liveFeed) {
        LiveFeed = liveFeed;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
