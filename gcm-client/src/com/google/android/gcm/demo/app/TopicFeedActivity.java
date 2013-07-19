package com.google.android.gcm.demo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class TopicFeedActivity extends Activity {
	class Result {
        private String title;
        private String description;
       
        public Result(String name, String address) {
        	title = name;
        	description = address;
        }
	}
	
	class GetMessagesResult{		
		List<Message> GetMessagesResult;
	}
	
	class Message
    {
        public String Id;           
        public String Title;   
        public Date Date;
        public String Text;
        public String Url;
    }
	
	class UnsubscribeTopicResult {    	
    	int UnsubscribeTopicResult;
    }
	
	public static final String PROPERTY_REG_ID = "registration_id";
	
	/* Menu Code */
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.topicfeedmenu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case R.id.helpTopicFeed:
		    	Intent helpIntent = new Intent(TopicFeedActivity.this, TopicFeedHelpActivity.class);
	        	startActivity(helpIntent);
		    return true;
		    case R.id.removeTopicFeed:
			    removeTopic();
			return true;
		    default:
		    return super.onOptionsItemSelected(item);
		}
		
	}
	
	private void removeTopic() {
		final String topic = getIntent().getExtras().getString("FeedName");
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				TopicFeedActivity.this);
 
			// set title
			alertDialogBuilder.setTitle("Are you sure you want to remove this topic?");
 
			// set dialog message
			alertDialogBuilder
				.setMessage(topic)
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						Context context = getApplicationContext();
						final SharedPreferences prefs = getGCMPreferences(context);
				        String registrationId = prefs.getString(PROPERTY_REG_ID, "");  
						int removeTopicResult = deleteTopicFromServer(registrationId, topic);
						
						AlertDialog.Builder alertDialogBuilderConfirm = new AlertDialog.Builder(
								TopicFeedActivity.this);
						
						if(removeTopicResult == 0){
							alertDialogBuilderConfirm.setMessage("Topic successfully removed!");
						}else{				
							alertDialogBuilderConfirm.setMessage("Oops something went wrong!");
						}
						
						alertDialogBuilderConfirm
						.setMessage("Topic Removed!")
						.setCancelable(false)
						.setNegativeButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
								Intent mIntent = new Intent(TopicFeedActivity.this, TopicPageActivity.class);
					        	startActivity(mIntent);
							}							
						});
						// create alert dialog
						AlertDialog alertDialogConfirm = alertDialogBuilderConfirm.create();
		 
						// show it
						alertDialogConfirm.show();
					}
				  })
				  .setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
	}

	/*******/
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.topicfeed);
        
        String topic = getIntent().getExtras().getString("FeedName");
        
        TextView cenas = (TextView)findViewById(R.id.TopicfeedText1);
        
        cenas.setText(topic);        

        currentTopicFeed(topic);
    }

	private void currentTopicFeed(String topic) {
		final ListView listview = (ListView) findViewById(R.id.TopicFeedlistView1);
        
        GetMessagesResult messagelist = new GetMessagesResult();
                
        HashMap<String, String> param = new HashMap<String, String>();        
        param.put("searchTerm", topic);
        POSTRequest asyncHttpPost = new POSTRequest(param);
        try {
	        String str_result = asyncHttpPost.execute("http://10.0.2.2/PushNotificationService/PushNotificationService.svc/GetMessages").get();
	        Gson gson = new GsonBuilder()
	        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssz")
	        .create();
			messagelist = gson.fromJson(str_result, GetMessagesResult.class);        
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();        	
        for (Message mess : messagelist.GetMessagesResult){
        	Map<String, String> list = new HashMap<String, String>(2);
        	list.put("line1", mess.Title);
        	list.put("line2", mess.Text);
        	list.put("url", mess.Url);
        	
        	long diff = (new java.util.Date()).getTime() - mess.Date.getTime();
        	long diffSeconds = diff / 1000 % 60;  
        	long diffMinutes = diff / (60 * 1000) % 60;        
            long diffHours = diff / (60 * 60 * 1000);  
            
            list.put("line3", diffHours + " hours, " + diffMinutes + " minutes and " + diffSeconds + " seconds ago");
        	
        	data.add(list);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, data,
        	    R.layout.multi_lines,
        	    new String[] { "line1","line2", "line3" },
        	    new int[] {R.id.line_a, R.id.line_b, R.id.line_c});
        
        listview.setOnItemClickListener(new OnItemClickListener()
        {
		        public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
		        {      
		        	WebView wView = new WebView(TopicFeedActivity.this);
		        	wView.getSettings().setJavaScriptEnabled(true);
		        	Map<String, String> item = (Map<String, String>)arg0.getItemAtPosition(position);
		              	
		        	if(item.get("url") !=  null && item.get("url") != ""){
		        		wView.loadUrl(item.get("url"));
		        	}
		        	else{
		        		//display in short period of time
		        		Toast.makeText(getApplicationContext(), "This feed has no URL!", Toast.LENGTH_SHORT).show();
		        	}
		      
                }
         });
        listview.setAdapter(adapter);        
	}
	
	private int deleteTopicFromServer(String regid, String topic) {
		        
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("regId", regid);
        data.put("topic", topic);
        POSTRequest asyncHttpPost = new POSTRequest(data);
        try {
			String str_result = asyncHttpPost.execute("http://10.0.2.2:58145/PushNotificationService.svc/UnsubscribeTopic").get();
			Gson gson = new Gson(); 
			UnsubscribeTopicResult i = gson.fromJson(str_result, UnsubscribeTopicResult.class);
			
			return i.UnsubscribeTopicResult;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;    
		
	}

	/**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(TopicPageActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
