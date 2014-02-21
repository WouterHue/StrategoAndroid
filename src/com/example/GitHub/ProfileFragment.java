package com.example.GitHub;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import com.example.Android.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by wouter on 16/02/14.
 */
public class ProfileFragment extends Fragment {

    private static final String URL_ACHIEVEMENTS= "http://10.0.2.2:8080/api/getachievements";
    private Map<String,List<String>> achievements;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            getUserAchievements();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addProfilePictureWithListener();
        LinearLayout topVerticalLayout = (LinearLayout)getActivity().findViewById(R.id.profile_layout_vertical_top);
        String usernameLoggedInUser = ((AppContext)getActivity().getApplicationContext()).getUsername();
        String usernameForProfile = usernameLoggedInUser;
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        //3different cases for handling arguments:
        //                  -User clicks on "profile" tab --> no argument
        //                  -User clicks on his icon --> argument = logged in user
        //                  -User clicks on friend in friendchat -->argument = friend
        if(getArguments()!=null){
            usernameForProfile = getArguments().getString(LobbyActivity.KEY_USERNAME_PROFILE);
            if(usernameForProfile.equals(usernameLoggedInUser)){
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
        TextView usernameLabel = new TextView(getActivity());
        usernameLabel.setText(usernameForProfile);
        usernameLabel.setBackgroundColor(Color.parseColor("#5E0000"));
        usernameLabel.setTextColor(Color.parseColor("#FDB951"));
        LayoutParams usernameParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        //params.setMargins(0,0,0,20); --> werkt niet vreemd genoeg
        usernameParams.bottomMargin += 20;
        topVerticalLayout.addView(usernameLabel,0,usernameParams);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        achievements = new HashMap<String, List<String>>();
        return inflater.inflate(R.layout.profile, container, false);
    }

    private void addProfilePictureWithListener() {
        LinearLayout topHorizontalLayout = (LinearLayout)getActivity().findViewById(R.id.profile_layout_horizontal_top);
        ImageView profilePicture = new ImageView(getActivity());
        profilePicture.setImageResource(R.drawable.maarschalk); //momenteel nog hardcoded, later vervangen door icon uit database
        topHorizontalLayout.addView(profilePicture, 0);
        LayoutInflater layoutInflater
                = (LayoutInflater)getActivity().getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout popupView = (LinearLayout)layoutInflater.inflate(R.layout.popup_profile_images, null);
        final PopupWindow popUp = new PopupWindow(popupView,700,700);
        /*profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            popUp.showAtLocation(view, Gravity.CENTER,1,1);
            }
        });*/

    }
    //TODO: een specifieke kleur geven afhankelijk van de status van de gebruiker
    public int getSpecificColor() {
        return R.color.green;
    }
    //TODO: specifieke text terugsturen afhankelijk of ingelogde gebruiker bevriend is met gebruiker
    public int getSpecificText() {
        return R.string.friend;
    }




    public void getUserAchievements() throws ExecutionException, InterruptedException, JSONException {
        List<String>idsList = new ArrayList<String>();
        List<String>titlesList = new ArrayList<String>();
        List<String>descriptionsList = new ArrayList<String>();
        List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("username",((AppContext)getActivity().getApplicationContext()).getUsername()));
        JSONObject data = new JsonController().executePostRequest(urlparams, URL_ACHIEVEMENTS);
        JSONArray achievementsArray = data.getJSONArray("achievements");
        if (achievementsArray != null) {
            for (int i=0;i<achievementsArray.length();i++){
                System.out.println(achievementsArray.get(i).toString());
                JSONObject jsonObject = (JSONObject)achievementsArray.get(i);
                idsList.add();
            }
        }
    }
}