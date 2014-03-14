package com.example.GitHub;

import android.app.*;
import android.app.ActionBar.Tab;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.example.Android.R;
import com.example.GitHub.Adapters.FriendsAdapter;
import com.example.GitHub.Model.Friend;
import com.example.GitHub.alarms.FriendListAlarm;
import com.example.GitHub.alarms.QueueAlarm;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wouter on 14/02/14.
 */
public class LobbyActivity extends KeyboardActivity{
    private String username;
    public final static String KEY_USERNAME_PROFILE = "com.example.Android.USERNAME_FRIEND";
    private static final String URL_FRIENDSLIST = "http://10.0.2.2:8080/api/getFriends?";
    private static final String URL_LOGOUT = "http://10.0.2.2:8080/api/logout";
    private static final String URL_ADD_FRIEND = "http://10.0.2.2:8080/api/addFriend";
    private static final String URL_ADD_USER_TO_QUEUE= "http://10.0.2.2:8080/api/addUserToQueue";

    private List<Friend> friendList;
    private List<Friend> invitedFriendsList;
    private ProfileFragment profileFragment;
    private DrawerLayout drawerLayout;
    private LinearLayout lLDrawer;
    private IntentFilter friendListIntentFilter;
    private IntentFilter queueIntentFilter;
    private FriendsAdapter friendsAdapter;
    private FriendsAdapter invitedFriendsAdapter;
    private List<NameValuePair> urlparams;
    private BroadcastReceiver friendListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            friendList.clear();
            invitedFriendsList.clear();
            getFriendsListFromJson();
            Collections.sort(friendList);
            friendsAdapter.notifyDataSetChanged();
            invitedFriendsAdapter.notifyDataSetChanged();

        }
    };/*
    private BroadcastReceiver queueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            searchRandomOpponent(null);
        }
    };*/

    private FriendListAlarm friendListAlarm;
    private QueueAlarm queueAlarm;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_activity);
        setupUI(findViewById(R.id.drawer_layout), this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        username = ((AppContext)getApplicationContext()).getUsername();
        setTitle(username);
        urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("username", username));
        addActionbarWithTabs();
        InitializeDrawerLayout();
        friendListAlarm = new FriendListAlarm();
        queueAlarm = new QueueAlarm();
        profileFragment = new ProfileFragment();
        friendListIntentFilter = new IntentFilter("updateFriendlist");
        queueIntentFilter = new IntentFilter("polQueue");
        friendListAlarm.setAlarm(this);
        registerReceiver(friendListReceiver,friendListIntentFilter);
        //registerReceiver(queueReceiver, queueIntentFilter);
    }

    private void InitializeDrawerLayout() {
        friendList = new ArrayList<Friend>();
        invitedFriendsList = new ArrayList<Friend>();
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        lLDrawer = (LinearLayout)findViewById(R.id.drawer);
        ListView lVFriends = (ListView)findViewById(R.id.drawer_list_top);
        ListView lVInvitedFriends = (ListView)findViewById(R.id.drawer_list_bottom);
        View view = getLayoutInflater().inflate(R.layout.friends_list_add_friend,null);
        lLDrawer.addView(view);
        LinearLayout friendListHeader = (LinearLayout)getLayoutInflater().inflate(R.layout.friends_list_header,null);
        TextView friendListTitle = (TextView) friendListHeader.getChildAt(0);
        friendListTitle.setText("FRIENDS");
        LinearLayout invitedFriendListHeader = (LinearLayout)getLayoutInflater().inflate(R.layout.friends_list_header,null);
        TextView invitedFriendListTitle = (TextView) invitedFriendListHeader.getChildAt(0);
        invitedFriendListTitle.setText("INVITED");
        getFriendsListFromJson();
        friendsAdapter = new FriendsAdapter(this,R.layout.friends_item_row,friendList);
        invitedFriendsAdapter = new FriendsAdapter(this,R.layout.friends_item_row, invitedFriendsList);
        lVFriends.setAdapter(friendsAdapter);
        lVFriends.addHeaderView(friendListHeader);
        lVInvitedFriends.setAdapter(invitedFriendsAdapter);
        lVInvitedFriends.addHeaderView(invitedFriendListHeader);


    }

    @Override
    protected void onResume() {
        registerReceiver(friendListReceiver, friendListIntentFilter);
       // registerReceiver(queueReceiver, queueIntentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(friendListReceiver);
        //unregisterReceiver(queueReceiver);
        super.onPause();
    }

    private void addActionbarWithTabs() {
        ActionBar actionBar = getActionBar();
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
        actionBar.addTab(lobbyTab);
        actionBar.addTab(profileTab);
        actionBar.addTab(gamesTab);
        actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.background_gradient));

    }

    //Show all menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_items,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Do something when menu button is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            navigateToProfilePage(username);

        }
        else if (item.getItemId()== R.id.action_friends){
            openOrCloseFriendsSideBar();
        }
        else if (item.getItemId() == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        JSONObject data = null;
        data = new JsonController().excecuteRequest(urlparams, URL_LOGOUT, "post");
            finish();
    }

    private void openOrCloseFriendsSideBar() {
        if(drawerLayout.isDrawerOpen(lLDrawer)){
            drawerLayout.closeDrawer(lLDrawer);
        }
        else drawerLayout.openDrawer(lLDrawer);

    }

    public void openProfile(View view) {
        String usernameFriend = ((TextView)view).getText().toString();
        navigateToProfilePage(usernameFriend);
    }

    private void navigateToProfilePage(String username){
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }
        if(!profileFragment.isVisible()){
            FragmentManager fragmentManager = getFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_USERNAME_PROFILE, username);
            profileFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, profileFragment, "profile").addToBackStack("profile").commit();
        }
    }

    public void getFriendsListFromJson() {
        JSONObject data = null;
        try {
            data = new JsonController().excecuteRequest(urlparams, URL_FRIENDSLIST,"get");
            JSONArray friendsArray = data.getJSONArray("friends");
            if (friendsArray != null) {
                for (int i=0;i<friendsArray.length();i++){
                    JSONObject jsonObject = (JSONObject)friendsArray.get(i);
                    Friend friend = new Friend(jsonObject.getInt("id"),jsonObject.getString("username"),jsonObject.getString("email"),
                            jsonObject.getString("status"),jsonObject.getBoolean("userAndFriendAreFriends"));
                    if (friend.isFriends()) {
                        friendList.add(friend);
                    } else {
                        invitedFriendsList.add(friend);

                    }
                }
                Collections.sort(friendList);
            }
        } catch (JSONException e) {
            Toast.makeText(this,"We're sorry, but we couldn't retrieve the data from the server",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        ((AppContext)getApplicationContext()).setFriendList(friendList);
    }

    public void navigateToNewGame(View view) {
       SearchOpponentFragment searchOpponentFragment = new SearchOpponentFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,searchOpponentFragment).addToBackStack("opponent").commit();
    }
/*
    public void searchRandomOpponent(View view) {
        try {
            JSONObject data = new JsonController().excecuteRequest(urlparams,URL_ADD_USER_TO_QUEUE,"post");
            int playerId = data.getInt("playerId");
            if (playerId != -1) {
                startGame(data.getString("color"),playerId, data.getInt("gameId"));
                queueAlarm.CancelAlarm(this);
            } else queueAlarm.setAlarm(this);
        }catch (JSONException e) {
            Toast.makeText(this,"We're sorry, but we couldn't retrieve the data from the server",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void startGame(String color, int playerId, int gameId) {
        Intent intent = new Intent();
        intent.putExtra("gameId",gameId);
        intent.putExtra("playerId",playerId);
        intent.putExtra("color",color);
        intent.setClass(this,GameActivity.class);
        startActivity(intent);
    }*/


    public void changeToEditText(final View view) {
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switcher);
        final EditText txtAddFriend = (EditText)findViewById(R.id.txt_add_friend);
        txtAddFriend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addFriend(txtAddFriend);
                    handled = true;
                }
                return handled;
            }
        });
        switcher.showNext();
    }

    public void addFriend(View view) {
        EditText txtAddFriend = (EditText)findViewById(R.id.txt_add_friend);
        if (txtAddFriend.getText().toString().isEmpty()) {
            Toast toastEmpty = Toast.makeText(this,"Field cannot be empty",Toast.LENGTH_SHORT);
            toastEmpty.setGravity(Gravity.CENTER, 0, 0);
            toastEmpty.show();
        }
        else {
            BasicNameValuePair friendValuePair = new BasicNameValuePair("friend",txtAddFriend.getText().toString());
            urlparams.add(friendValuePair);
            JSONObject data = null;
            try {
                data = new JsonController().excecuteRequest(urlparams, URL_ADD_FRIEND,"post");
                if (data != null) {
                    JSONObject jSonFriend = data.getJSONObject("friend");
                    Friend friend = new Friend(jSonFriend.getInt("id"), jSonFriend.getString("username"), jSonFriend.getString("email"),
                            jSonFriend.getString("status"), jSonFriend.getBoolean("userAndFriendAreFriends"));
                    if (friend.isFriends()) {
                        friendList.add(friend);
                        friendsAdapter.notifyDataSetChanged();

                    } else {
                        invitedFriendsList.add(friend);
                        invitedFriendsAdapter.notifyDataSetChanged();
                        addNotification(friend);
                    }
                    Toast toastSend = Toast.makeText(this,"A request has been send",Toast.LENGTH_SHORT);
                    toastSend.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                    toastSend.show();
                } else {
                    Toast toastError = Toast.makeText(this,"Friend does not exist",Toast.LENGTH_SHORT);
                    toastError.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toastError.show();
                }

            }catch (JSONException e) {
                Toast.makeText(this,"We're sorry, but we couldn't retrieve the data from the server",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            urlparams.remove(friendValuePair);
        }
    }


    private void addNotification(Friend friend) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_add_person_notification)
                        .setContentTitle("New friend request")
                        .setContentText(friend.getUsername() + "wants to add you to his friendlsit")
                        .setAutoCancel(true);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, LobbyActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(LoginActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT

                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }



}