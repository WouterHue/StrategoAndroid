package com.example.GitHub;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.Android.R;
import com.example.GitHub.Model.Friend;
import com.example.GitHub.alarms.QueueAlarm;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wouter on 10/03/14.
 */
public class SearchOpponentFragment extends Fragment implements View.OnClickListener {
    private int oldPosition = 0;
    private IntentFilter queueIntentFilter;
    private QueueAlarm queueAlarm;
    private String username;
    private List<NameValuePair> urlparams;
    private BroadcastReceiver queueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            searchRandomOpponent(null);
        }
    };
    private static final String URL_ADD_USER_TO_QUEUE= "http://10.0.2.2:8080/api/addUserToQueue";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        username = ((AppContext)getActivity().getApplicationContext()).getUsername();
        urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("username", username));
        queueAlarm = new QueueAlarm();
        queueIntentFilter = new IntentFilter("polQueue");
        return inflater.inflate(R.layout.search_opponent_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnSearchOnlineFriends = (Button)getActivity().findViewById(R.id.btn_play_with_friend);
        btnSearchOnlineFriends.setOnClickListener(this);
        Button btnSearchOpponent= (Button)getActivity().findViewById(R.id.btn_search_random_player);
        btnSearchOpponent.setOnClickListener(this);
    }

    public void searchRandomOpponent(View view) {
        try {
            JSONObject data = new JsonController().excecuteRequest(urlparams,URL_ADD_USER_TO_QUEUE,"post");
            int playerId = data.getInt("playerId");
            if (playerId != -1) {
                startGame(data.getString("color"),playerId, data.getInt("gameId"));
                queueAlarm.CancelAlarm(getActivity());
            } else queueAlarm.setAlarm(getActivity());
        }catch (JSONException e) {
            Toast.makeText(getActivity(),"We're sorry, but we couldn't retrieve the data from the server",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void startGame(String color, int playerId, int gameId) {
        Intent intent = new Intent();
        intent.putExtra("gameId",gameId);
        intent.putExtra("playerId",playerId);
        intent.putExtra("color",color);
        intent.setClass(getActivity(),GameActivity.class);
        startActivity(intent);
    }

    public void showOnlineFriends() {
        Button btnInviteFriend = (Button)getActivity().findViewById(R.id.btn_play_with_friend);
        btnInviteFriend.setVisibility(View.VISIBLE);
        btnInviteFriend.setOnClickListener(this);
        final List<String> onlineFriendslist = new ArrayList<String>();
        for (Friend friend : ((AppContext)getActivity().getApplicationContext()).getFriendList()) {
            if (friend.getStatus().equalsIgnoreCase("online")) {
                onlineFriendslist.add(friend.getUsername());
            }
        }
        final ListView listOnlineFriends = (ListView)getActivity().findViewById(R.id.list_online_friends);
        listOnlineFriends.setVisibility(View.VISIBLE);
        listOnlineFriends.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.online_friends_item_row, onlineFriendslist));
        listOnlineFriends.setTextFilterEnabled(true);
        listOnlineFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listOnlineFriends.getChildAt(oldPosition).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                view.setBackgroundColor(getResources().getColor(R.color.app_background_light));
                oldPosition = i;
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_play_with_friend) {
            showOnlineFriends();
        } else if (view.getId() == R.id.btn_invite_friend) {
            inviteFriend();
        }
        else if (view.getId() == R.id.btn_search_random_player) {
            searchRandomOpponent(view);
        }
    }

    private void inviteFriend() {
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(queueReceiver,queueIntentFilter);
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(queueReceiver);
        super.onPause();
    }
}