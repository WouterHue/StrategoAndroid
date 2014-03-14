package com.example.GitHub;

import android.app.Application;
import com.example.GitHub.Model.Friend;

import java.util.List;

/**
 * Created by wouter on 18/02/14.
 */
//This class is used for declaring global variables
public  class AppContext extends Application {
    private String username;

    private List<Friend> friendList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }
}
