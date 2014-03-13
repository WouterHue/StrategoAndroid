package com.example.GitHub.Model;

/**
 * Created by wouter on 4/03/14.
 */
public class Friend implements Comparable<Friend> {
    private int id;
    private String email;
    private String username;
    private String status;
    private boolean isFriends;

    public Friend(int id, String username, String email, String status,boolean isFriends) {
        this.status = status;
        this.username = username;
        this.email = email;
        this.id = id;
        this.isFriends = isFriends;
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

    public String getStatus() {
        return status;
    }

    public boolean isFriends() {
        return isFriends;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public int compareTo(Friend friend) {
        return friend.getStatus().compareTo(this.getStatus());
    }
}
