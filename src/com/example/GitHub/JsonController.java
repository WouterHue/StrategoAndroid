package com.example.GitHub;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by wouter on 20/02/14.
 */
public class JsonController extends AsyncTask<String,Void,JSONObject> {

    private List<NameValuePair> urlParams;
    private String url;
    private String type;

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;

        try {
            if(type.equalsIgnoreCase("post")){
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(urlParams));
                response = httpclient.execute(httpPost);
            }
            else {
                String paramString = URLEncodedUtils.format(urlParams,"utf-8");
                url+=paramString;
                HttpGet httpGet = new HttpGet(url);
                response = httpclient.execute(httpGet);

            }
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String thisLine;
            while ((thisLine = reader.readLine())!=null){
                sb.append(thisLine);
            }
            return new JSONObject(sb.toString());

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public JSONObject excecuteRequest(List<NameValuePair> urlParams, String url, String type) throws ExecutionException, InterruptedException {
        this.urlParams = urlParams;
        this.url = url;
        this.type = type;
        return execute().get();

    }
}
