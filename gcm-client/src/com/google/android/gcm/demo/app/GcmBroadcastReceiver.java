/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gcm.demo.app;

import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Handling of GCM messages.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    static final String TAG = "GCMDemo";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;    
    
    public List<Topics> topicForNotification = new ArrayList<Topics>();
    
    class Topics {
    	String topicName;
    	Integer Quantity;
    	Integer NotificationId;
    }
    
    class PushNotificationServiceResult
    {    	
    	String topic;
    	Integer newMessages;
    	String badge;
    	String sound;    	
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            //sendNotification("Send error: " + intent.getExtras().toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            //sendNotification("Deleted messages on server: " + intent.getExtras().toString());
        } else {
            sendNotification(intent.getExtras());
        }
        setResultCode(Activity.RESULT_OK);
    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(Bundle msg) {
    	Log.v(TAG, "mesage: " + msg);
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);        
        
        Topics topics = new Topics();
        Topics Lasttopic = new Topics();
        
        SharedPreferences prefs = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String value = prefs.getString("topicForNotification", null);

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        //Topics[] topicForNotification = gson.fromJson(value, Topics[].class);
        
        List<Topics> topicForNotification = gson.fromJson(value, new TypeToken<List<Topics>>(){}.getType());
                
        topics.topicName = msg.getString("topic");
        topics.Quantity = Integer.parseInt(msg.getString("newMessages"));
                
        Boolean exists = false;
        
        if(topicForNotification != null){
	        for(Topics t : topicForNotification){        	
	        	if(t.topicName == topics.topicName){        		
	        		t.Quantity = t.Quantity + topics.Quantity;
	        		topics.NotificationId = t.NotificationId;
	        		exists = true;
	        	}
	        	Lasttopic = t;
	        }
        }
        else{
        	topicForNotification = new ArrayList<Topics>();
        }
        
        if(!exists){
        	if(Lasttopic.NotificationId != null)
        	{
        		topics.NotificationId = Lasttopic.NotificationId + 1 ;
        	}else{
        		topics.NotificationId = 1;
        	}       
        	
        	topicForNotification.add(topics);
        }
        
        value = gson.toJson(topicForNotification);
    	prefs = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
    	Editor e = prefs.edit();
    	e.putString("topicForNotification", value);
    	e.commit();
        
        Intent mIntent = new Intent(ctx, TopicFeedActivity.class);
        mIntent.putExtra("FeedName", topics.topicName); 


        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
        		mIntent, 0);
        
        String message = "There are " + topics.Quantity + " new alerts for " + topics.topicName;
        
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
              
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
        .setSmallIcon(R.drawable.ic_stat_gcm)
        .setContentTitle("Push POC")
        .setAutoCancel(true)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(message))
        .setSound(uri)
        .setContentText(message);
        
        

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(topics.NotificationId, mBuilder.build());
    }
}
