package com.example.GitHub.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.Android.R;
import com.example.GitHub.Model.Friend;

import java.util.List;

/**
 * Created by wouter on 3/03/14.
 */
public class FriendsAdapter extends ArrayAdapter<Friend> implements AdapterView.OnItemClickListener {
    private Context context;
    private int layoutResourceId;
    private List<Friend>friendsList;
    private Status status;
    public FriendsAdapter(Context context, int layoutResourceId, List<Friend> friendsList) {
        super(context, layoutResourceId, friendsList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.friendsList = friendsList;
    }

    //return certain view for every row in friendslist
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout row = (LinearLayout)convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = (LinearLayout)inflater.inflate(layoutResourceId, parent, false);
        }

        ImageView imgStatus = (ImageView) row.getChildAt(0);
        String statusFriend = friendsList.get(position).getStatus();
        status = Status.valueOf(statusFriend.toUpperCase());
        if (friendsList.get(position).isFriends()) {
            if(status == Status.ONLINE){
                imgStatus.setImageResource(R.drawable.online_icon);
            }
            else{
                imgStatus.setImageResource(R.drawable.offline_icon);
            }
        }
        else imgStatus.setImageResource(R.drawable.not_accepted_icon);

        TextView txtFriend = (TextView) row.getChildAt(1);
        String friend = friendsList.get(position).getUsername();
        txtFriend.setText(friend);
        return row;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

   public enum Status{
       ONLINE,OFFLINE;

   }
}
