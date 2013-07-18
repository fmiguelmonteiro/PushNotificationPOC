package com.google.android.gcm.demo.app;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.GsonBuilder;

import android.os.AsyncTask;

public class POSTRequest extends AsyncTask<String, String, String>{
	private Exception exception;
	private HashMap<String, String> mData = null;// post data

    /**
     * constructor
     */
    public POSTRequest(HashMap<String, String> data) {
        mData = data;
    }
	
	protected String doInBackground(String... params) {
		byte[] result = null;
		String str = "";		
		
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(params[0]);

        try {    
                        
            String json = new GsonBuilder().create().toJson(mData, Map.class);

            httppost.setHeader("Content-type", "application/json");
            httppost.setHeader("Accept", "application/json");
            
            httppost.setEntity(new StringEntity(json, "UTF-8"));
             
            HttpResponse response = httpclient.execute(httppost);
            
        } catch (ClientProtocolException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        } 
        
		return str;
    }	

	@Override
    protected void onPostExecute(String result) {
        // something...
    }
}
