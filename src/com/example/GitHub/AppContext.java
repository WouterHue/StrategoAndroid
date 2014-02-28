package com.example.GitHub;

import android.app.Application;

/**
 * Created by wouter on 18/02/14.
 */
//This class is used for declaring global variables
public  class AppContext extends Application {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
