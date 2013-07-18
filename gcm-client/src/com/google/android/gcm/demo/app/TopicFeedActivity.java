package com.google.android.gcm.demo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TopicFeedActivity extends Activity {
	class Result {
        private String title;
        private String description;
       
        public Result(String name, String address) {
        	title = name;
        	description = address;
        }
	}
	
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
        
        String value = getIntent().getExtras().getString("FeedName");
        
        TextView cenas = (TextView)findViewById(R.id.TopicfeedText1);
        
        cenas.setText(value);        

        currentTopicFeed();
    }

	private void currentTopicFeed() {
		final ListView listview = (ListView) findViewById(R.id.TopicFeedlistView1);
    	
    	//get TOPICS!
        String[] values = new String[] { "Test1", "Test2", "test3", "Test1", "Test2", "test3", "Test1", "Test2", "test3", "Test1", "Test2", "test3" };
        ////
                
        
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (int i = 0; i < values.length; ++i) {
        	Map<String, String> list = new HashMap<String, String>(2);
        	list.put("title", values[i]);
        	list.put("description", "description test");
        	list.put("url", "http://www.sapo.pt");
        	data.add(list);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "description"},
                new int[] {android.R.id.text1,
                           android.R.id.text2});
        
        listview.setOnItemClickListener(new OnItemClickListener()
        {
		        public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
		        {      
		        	WebView wView = new WebView(TopicFeedActivity.this);
		        	wView.getSettings().setJavaScriptEnabled(true);
		        	Map<String, String> item = (Map<String, String>)arg0.getItemAtPosition(position);
		        	if(item.get("url") != null && item.get("url") != ""){
		        		wView.loadUrl(item.get("url"));
		        	}
		      
                }
         });
        listview.setAdapter(adapter);        
	}
}
