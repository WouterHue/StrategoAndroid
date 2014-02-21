package com.example.GitHub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.Android.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends Activity {
    private static final String URL = "http://10.0.2.2:8080/rest/verifyuser"; //indien je vanuit emulator verstuurd
    //private static final String URL = "http://192.168.0.206/verifyUser"; normaal gezien wnr je vanuit device stuurt
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void checkCredentials(View view) throws ExecutionException, InterruptedException {
        //wait until the asynctask has finished its doInBackground method.

        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password= ((EditText)findViewById(R.id.password)).getText().toString();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        JSONObject data = new JsonController().executePostRequest(nameValuePairs,URL);
        boolean isVerified = false;
        try {
            isVerified = data.getBoolean("isVerified");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(isVerified){
            Intent intent = new Intent(this,LobbyActivity.class);
            EditText editText = (EditText)findViewById(R.id.username);
            String usernameParam = editText.getText().toString();
            ((AppContext)getApplicationContext()).setUsername(usernameParam);
            startActivity(intent);
        }
    }
}
