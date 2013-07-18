package com.google.android.gcm.demo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AddTopicActivity extends Activity {
	
	 /* Menu Code */
	
		public boolean onCreateOptionsMenu(Menu menu) {
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.addtopicmenu, menu);
		    return true;
		}
		
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			    case R.id.addTopicHelp:
			    	Intent helpIntent = new Intent(AddTopicActivity.this, AddTopicHelpActivity.class);
		        	startActivity(helpIntent);
			    return true;
			    default:
			    return super.onOptionsItemSelected(item);
			}
		}
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        setContentView(R.layout.addtopic);
	        
	        TextView cenas = (TextView)findViewById(R.id.textViewAddTopic);
	        
	        cenas.setText("New Topic");        

	        popularTopics();
	    }

	private void popularTopics() {
		final ListView listview = (ListView) findViewById(R.id.listViewAddTopic);
    	
    	//get TOPICS!
        String[] values = new String[] { "PopularTopic1", "PopularTopic2", "PopularTopic3" };
        ////
        
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
          list.add(values[i]);
        }
        
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        
        listview.setAdapter(adapter);        
        
        listview.setOnItemClickListener(new OnItemClickListener()
        {
		        public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
		        {      
		        	EditText textBox = (EditText)findViewById(R.id.editTextAddTopic);
		        	textBox.setText(listview.getItemAtPosition(position).toString());  
                }
         });
	}

}
