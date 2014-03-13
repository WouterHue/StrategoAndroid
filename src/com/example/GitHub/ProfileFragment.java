package com.example.GitHub;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import com.example.Android.R;
import com.example.GitHub.Adapters.AchievementAdapter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by wouter on 16/02/14.
 */
public class ProfileFragment extends Fragment implements OnClickListener {

    private static final String URL_ACHIEVEMENTS= "http://10.0.2.2:8080/api/getachievements?";
    private String[] descriptions;
    String[]titles;
    ImageView profilePic;
    GridView gridView;
    List<ImageView>avatars;
    String profileUsername;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setProfilePicture();
        LinearLayout topVerticalLayout = (LinearLayout)getActivity().findViewById(R.id.profile_layout_vertical_top);
        String usernameLoggedInUser = ((AppContext)getActivity().getApplicationContext()).getUsername();
        profileUsername = usernameLoggedInUser;
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //3different cases for handling arguments:
        //                  -User clicks on "profile" tab --> no argument
        //                  -User clicks on his icon --> argument = logged in user
        //                  -User clicks on friend in friendchat -->argument = friend
        if(getArguments()!=null){
            profileUsername = getArguments().getString(LobbyActivity.KEY_USERNAME_PROFILE);
            if(profileUsername.equals(usernameLoggedInUser)){
                //Do something when visting your own profile
            }
            else {
                //Do something when visiting someone else profile
                TextView txtOnline = new TextView(getActivity());
                txtOnline.setBackgroundColor(Color.GREEN);
                txtOnline.setText(R.string.online);
                txtOnline.setLayoutParams(params);
                txtOnline.setPadding(20,10,0,10);
                topVerticalLayout.addView(txtOnline);
                Button btnFriend = new Button(getActivity());
                btnFriend.setText(getSpecificText());
                // btnFriend.setBackgroundColor(Color.parseColor("#C4433A"));
                btnFriend.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Drawable img = getResources().getDrawable(R.drawable.ic_action_done);
                img.setBounds(0,0,60,60);
                btnFriend.setCompoundDrawables(img, null, null, null);
                btnFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //String friend =
                    }
                });
                topVerticalLayout.addView(btnFriend);

            }
        }
        addAchievementsToGridview();
        TextView usernameLabel = new TextView(getActivity());
        usernameLabel.setText(profileUsername);
        usernameLabel.setBackgroundColor(Color.parseColor("#5E0000"));
        usernameLabel.setTextColor(Color.parseColor("#FDB951"));
        LayoutParams usernameParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        usernameParams.bottomMargin += 20;
        topVerticalLayout.addView(usernameLabel,0,usernameParams);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        avatars = new ArrayList<ImageView>();
        return inflater.inflate(R.layout.profile, container, false);
    }

    private void setProfilePicture() {
        profilePic = (ImageView)getActivity().findViewById(R.id.profile_pic);
        profilePic.setImageResource(R.drawable.maarschalk); //momenteel nog hardcoded, later vervangen door icon uit database
        profilePic.setOnClickListener(this);
    }
    //TODO: een specifieke kleur geven afhankelijk van de status van de gebruiker
    public int getSpecificColor() {
        return Color.parseColor("#008000");
    }
    //TODO: specifieke text terugsturen afhankelijk of ingelogde gebruiker bevriend is met gebruiker
    public int getSpecificText() {
        return R.string.friend;
    }

    public void addAchievementsToGridview(){
        //List<Integer>idsList = new ArrayList<Integer>();
        List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("username",profileUsername));
        JSONObject data = null;
        try {
            data = new JsonController().excecuteRequest(urlparams, URL_ACHIEVEMENTS,"get");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray achievementsArray = null;
        try {
            achievementsArray = data.getJSONArray("achievements");
            if (achievementsArray != null) {
                titles = new String[achievementsArray.length()];
                descriptions = new String[achievementsArray.length()];
                for (int i=0;i<achievementsArray.length();i++){
                    JSONObject jsonObject = (JSONObject)achievementsArray.get(i);
                    //idsList.add(jsonObject.getInt("id"));
                    titles[i]= jsonObject.getString("title");
                    descriptions[i] = jsonObject.getString("description");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gridView = (GridView)getActivity().findViewById(R.id.profile_layout_grid_bottom);
        gridView.setAdapter(new AchievementAdapter(getActivity(),titles));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        descriptions[position], Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showPopUpWindow(View view){
        LayoutInflater layoutInflater
                = (LayoutInflater)getActivity().getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout popupView = (LinearLayout)layoutInflater.inflate(R.layout.popup_profile_images, null);
        final PopupWindow popUp = new PopupWindow(popupView);
        TableLayout tblAvatars = (TableLayout)popupView.findViewById(R.id.tbl_avatars);
        for(int i = 0; i<tblAvatars.getChildCount();i++){
            TableRow tableRow = (TableRow)tblAvatars.getChildAt(i);
            for (int j = 0 ; j<tableRow.getChildCount();j++){
                ImageView avatar = (ImageView)tableRow.getChildAt(j);
                avatar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(view.getBackground()==null){
                            view.setBackgroundResource(R.drawable.avatar_image_border);
                        }

                        else view.setBackground(null);

                    }
                });
                avatars.add(avatar);
            }
        }
        popUp.showAtLocation(view, Gravity.CENTER,1,1);
        ImageButton closeButton = (ImageButton) popupView.findViewById(R.id.close_button);
        ImageButton acceptButton = (ImageButton)popupView.findViewById(R.id.accept_button);
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp.dismiss();
            }
        });
        acceptButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ImageView avatar : avatars){
                    if(avatar.getBackground()!=null){
                        profilePic.setImageDrawable(avatar.getDrawable());
                        getActivity().getActionBar().setIcon(avatar.getDrawable());
                        popUp.dismiss();
                    }
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profile_pic : showPopUpWindow(view);
        }
    }
}