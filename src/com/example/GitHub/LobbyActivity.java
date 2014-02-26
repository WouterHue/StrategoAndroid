package com.example.GitHub;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.Android.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by wouter on 14/02/14.
 */
public class LobbyActivity extends Activity {
    private String username;
    public final static String KEY_USERNAME_PROFILE = "com.example.Android.USERNAME_FRIEND";
    private static final String URL_FRIENDSLIST = "http://10.0.2.2:8080/api/getfriends?";
    private List<String> friendList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_activity);
        username = ((AppContext)getApplicationContext()).getUsername();
        friendList = new ArrayList<String>();
        setTitle(username);
        addActionbarWithTabs();
    }

    private void addActionbarWithTabs() {
        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle("Rank... of ...");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.maarschalk);
        //sepcify that tabs should be displayed in the action bar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Tab profileTab = actionBar.newTab();
        profileTab.setText("Profile");
        profileTab.setTabListener(new TabListener<ProfileFragment>(this, "profileTag", ProfileFragment.class));
        Tab lobbyTab = actionBar.newTab();
        lobbyTab.setText("Lobby");
        lobbyTab.setTabListener(new TabListener<LobbyFragment>(this,"lobby",LobbyFragment.class));
        Tab gamesTab = actionBar.newTab();
        gamesTab.setText("Games");
        //"Games" Is the fragment tag
        gamesTab.setTabListener(new TabListener<GamesMenuFragment>(this, "games", GamesMenuFragment.class));
        Tab statsTab = actionBar.newTab();
        statsTab.setText("Stats");
        statsTab.setTabListener(new TabListener<StatsFragment>(this, "stats", StatsFragment.class));
        actionBar.addTab(lobbyTab);
        actionBar.addTab(profileTab);
        actionBar.addTab(statsTab);
        actionBar.addTab(gamesTab);
        actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.background_gradient));

    }

    //Do something when menu button is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            navigateToProfilePage(username);

        }
        return super.onOptionsItemSelected(item);
    }

    public void goToProfile(View view) {
        String usernameFriend = ((TextView)view).getText().toString();
        navigateToProfilePage(usernameFriend);
    }

    private void navigateToProfilePage(String username){
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentManager fragmentManager = getFragmentManager();
        Fragment currentFragment;
        Fragment lobbyFragment = fragmentManager.findFragmentByTag("lobby");
        Fragment statsFragment  = fragmentManager.findFragmentByTag("stats");
        Fragment gamesFragment  = fragmentManager.findFragmentByTag("games");
        Fragment profileTabFragment = fragmentManager.findFragmentByTag("profileTag");
        if(profileTabFragment == null){
            profileTabFragment = new ProfileFragment();
        }
        //Do nothing when you click on your name while the profilefragment is opened.
        if(!profileTabFragment.isVisible()){

            if(lobbyFragment.isVisible()){
                currentFragment = lobbyFragment;
            }
            else if(statsFragment.isVisible()){
                currentFragment = statsFragment;
            }
            else currentFragment = gamesFragment;

            Bundle bundle = new Bundle();
            bundle.putString(KEY_USERNAME_PROFILE, username);
            profileFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(currentFragment.getId(), profileFragment,"profile").addToBackStack("profile").commit();
        }
        else if(!username.equals(((AppContext)getApplicationContext()).getUsername())){
            currentFragment = profileFragment;
            Bundle bundle = new Bundle();
            bundle.putString(KEY_USERNAME_PROFILE, username);
            profileFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(currentFragment.getId(), profileFragment,"profile").addToBackStack("profile").commit();
        }

    }

    public void getFriendsListFromJson() throws ExecutionException, InterruptedException, JSONException {
        List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("username",((AppContext)getApplicationContext()).getUsername()));
        JSONObject data = new JsonController().excecuteRequest(urlparams, URL_FRIENDSLIST,"get");
        JSONArray friendsArray  = data.getJSONArray("friends");
        if (friendsArray != null) {
            for (int i=0;i<friendsArray.length();i++){
                friendList.add(friendsArray.get(i).toString());
            }
        }
    }

    public void navigateToNewGame(View view) {
        Intent intent = new Intent(this,GameActivity.class);
        startActivity(intent);
    }
}