package com.google.android.gcm.demo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
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
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.topicfeed);
        
        String value = getIntent().getExtras().getString("FeedName");
        
        TextView cenas = (TextView)findViewById(R.id.TopicfeedText1);
        
        cenas.setText(value);        

        currentTopics();
    }

	private void currentTopics() {
		final ListView listview = (ListView) findViewById(R.id.TopicFeedlistView1);
    	
    	//get TOPICS!
        String[] values = new String[] { "Test1", "Test2", "test3", "Test1", "Test2", "test3", "Test1", "Test2", "test3", "Test1", "Test2", "test3" };
        ////
                
        
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (int i = 0; i < values.length; ++i) {
        	Map<String, String> list = new HashMap<String, String>(2);
        	list.put("title", values[i]);
        	list.put("description", "description test");
        	data.add(list);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "description"},
                new int[] {android.R.id.text1,
                           android.R.id.text2});
        
        
        listview.setAdapter(adapter);        
	}
}
