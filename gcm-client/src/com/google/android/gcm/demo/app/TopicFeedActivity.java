package com.google.android.gcm.demo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.Date;

import org.apache.http.HttpResponse;

import com.google.android.gcm.demo.app.DemoActivity.result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
    }
	
	/* Menu Code */
	
	/*public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.topicfeedmenu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case R.id.about:
		    //startActivity(new Intent(this, About.class));
		    return true;
		    case R.id.help:
		    //startActivity(new Intent(this, Help.class));
		    return true;
		    case R.id.remove:
			    removeTopic();
			return true;
		    default:
		    return super.onOptionsItemSelected(item);
		}
		
	}*/
	
	private void removeTopic() {
		String topic = getIntent().getExtras().getString("FeedName");
		
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
						// if this button is clicked, close
						// current activity
						//MainActivity.this.finish();
						AlertDialog.Builder alertDialogBuilderConfirm = new AlertDialog.Builder(
								TopicFeedActivity.this);
						alertDialogBuilderConfirm
						.setMessage("Topic Removed!")
						.setCancelable(false)
						.setNegativeButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
								Intent mIntent = new Intent(TopicFeedActivity.this, DemoActivity.class);
								mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
						// if this button is clicked, just close
						// the dialog box and do nothing
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
       
        currentTopics(topic);
    }      

	private void currentTopics(String topic) {
		final ListView listview = (ListView) findViewById(R.id.TopicFeedlistView1);
        
        GetMessagesResult messagelist = new GetMessagesResult();
                
        HashMap<String, String> param = new HashMap<String, String>();        
        param.put("searchTerm", topic);
        POSTRequest asyncHttpPost = new POSTRequest(param);
        try {
	        String str_result = asyncHttpPost.execute("http://10.0.2.2:58145/PushNotificationService.svc/GetMessages").get();
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
        
        
        listview.setAdapter(adapter);        
	}
}
