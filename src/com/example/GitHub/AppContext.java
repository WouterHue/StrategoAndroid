package com.example.GitHub;

import android.app.Application;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by wouter on 18/02/14.
 */
//This class is used for declaring global variables
public class AppContext extends Application {
    String username;

    public float convertPixelsToDp(float px){
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
