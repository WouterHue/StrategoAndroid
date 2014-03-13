package com.example.GitHub;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.example.Android.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends KeyboardActivity {
    private static final String URL = "http://10.0.2.2:8080/api/verifyuser"; //indien je vanuit emulator verstuurd
   // private static final String URL = "http://84.196.161.180:80/api/verifyuser"; //normaal gezien wnr je vanuit device stuurt
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setupUI(findViewById(R.id.login_activity_layout),this);
        final EditText txtPassword = (EditText)findViewById(R.id.password);
        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            boolean handled;
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    checkCredentials(txtPassword);
                    handled = true;
                }
                return handled;
            }
        });
    }

    public void checkCredentials(View view) {
        EditText txtUsername = (EditText)findViewById(R.id.username);
        EditText txtPassword = (EditText)findViewById(R.id.password);
        String username = txtUsername.getText().toString();
        String password= txtPassword.getText().toString();
        boolean isVerified = false;
        if (!username.isEmpty() && !password.isEmpty()) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            JSONObject data = null;
            try {
                data = new JsonController().excecuteRequest(nameValuePairs, URL, "post");
                isVerified = data.getBoolean("isVerified");
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            if (isVerified) {
                Intent intent = new Intent(this, LobbyActivity.class);
                EditText editText = (EditText) findViewById(R.id.username);
                String usernameParam = editText.getText().toString();
                ((AppContext) getApplicationContext()).setUsername(usernameParam);
                startActivity(intent);
            }
        }
        else {
            if (username.isEmpty()) {
                txtUsername.setError("Username cannot be empty!");
            }
            else txtUsername.setError(null);
            if (password.isEmpty()) {
                txtPassword.setError("Password cannot be empty!");
            }
            else txtPassword.setError(null);

        }

    }

}
