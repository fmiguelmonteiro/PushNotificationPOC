package com.google.android.gcm.demo.app;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gcm.demo.app.TopicFeedActivity.Message;
import com.google.android.gcm.demo.app.TopicPageActivity.GetSubscribedTopicsResult;
import com.google.android.gcm.demo.app.TopicPageActivity.result;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AddTopicActivity extends Activity {
	
	 	public static final String EXTRA_MESSAGE = "message";
	    public static final String PROPERTY_REG_ID = "registration_id";
	    private static final String PROPERTY_APP_VERSION = "appVersion";
	    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME =
	            "onServerExpirationTimeMs";
	    /**
	     * Default lifespan (7 days) of a reservation until it is considered expired.
	     */
	    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
	    /**
	     * You must use your own project ID instead.
	     */
	    String SENDER_ID = "649471969080";

	    /**
	     * Tag used on log messages.
	     */
	    static final String TAG = "GCMPOC";

	    TextView mDisplay;
	    GoogleCloudMessaging gcm;
	    AtomicInteger msgId = new AtomicInteger();
	    Context context;

	    String regid;
	    
	    class GetPopularTopicsResult{		
			List<Topic> GetPopularTopicsResult;
		}
	    
	    class Topic {
	    	 public String Id;
	    	 public String Name;
	    	 public String NumberOfSubscribers;
	    }
	
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
	        
	        TextView title = (TextView)findViewById(R.id.textViewAddTopic);
	        
	        title.setText("New Topic");
	        
	        final Button regbtn = (Button) findViewById(R.id.Add);
	 		final EditText editText = (EditText)findViewById(R.id.editTextAddTopic);		

	 		regbtn.setOnClickListener(new View.OnClickListener() {
	 			@Override
	 			public void onClick(View v) {
	 				searchRequest();
	 				editText.setText("");
	 			}
	 		});		 		
	 		
	 		regbtn.setEnabled(false);
	    	 		
	 		editText.addTextChangedListener(new TextWatcher() {

	 		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	 		    public void onTextChanged(CharSequence s, int start, int before, int count) {}

	 		    public void afterTextChanged(Editable s) {
	 		        if (s == null || s.length() == 0) {
	 		        	regbtn.setEnabled(false);
	 		        }
	 		        else {
	 		        	regbtn.setEnabled(true);
	 		        }
	 		    }
	 		}); 

	        popularTopics();
	    }

	 class result {    	
	    	int RegisterResult;
	    }
	 
	 @Override
	    protected void onDestroy() {
	        super.onDestroy();
	    }
	 
	 private void searchRequest(){
	    	EditText editText = (EditText)findViewById(R.id.editTextAddTopic);

	    	String editTextStr = editText.getText().toString();
	    	context = getApplicationContext();
	        final SharedPreferences prefs = getGCMPreferences(context);
	        String registrationId = prefs.getString(PROPERTY_REG_ID, "");       
	        
	        HashMap<String, String> data = new HashMap<String, String>();
	        data.put("regId", registrationId);
	        data.put("topic", editTextStr);
	        POSTRequest asyncHttpPost = new POSTRequest(data);
	        try {
				String str_result = asyncHttpPost.execute("http://10.0.2.2/PushNotificationService/PushNotificationService.svc/SubscribeTopic").get();
				Gson gson = new Gson(); 
				result i = gson.fromJson(str_result, result.class);
				
				AlertDialog.Builder alertDialogBuilderConfirm = new AlertDialog.Builder(
						AddTopicActivity.this);
				if(i.RegisterResult == 0){
					alertDialogBuilderConfirm.setMessage("Topic successfully added!");
				}else{				
					alertDialogBuilderConfirm.setMessage("Oops something went wrong!");
				}
				
				alertDialogBuilderConfirm.setCancelable(true);
				alertDialogBuilderConfirm.setNeutralButton(android.R.string.ok,
			            new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			            dialog.cancel();
			        }
			    });
				
				// create alert dialog
				AlertDialog alertDialogConfirm = alertDialogBuilderConfirm.create();

				// show it
				alertDialogConfirm.show();
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	        
	        
	    }
	 
	private void popularTopics() {
		final ListView listview = (ListView) findViewById(R.id.listViewAddTopic);
    	
    	//get TOPICS!
        //String[] values = new String[] { "PopularTopic1", "PopularTopic2", "PopularTopic3" };
        ////
        
        //final ArrayList<String> list = new ArrayList<String>();
        //for (int i = 0; i < values.length; ++i) {
        //  list.add(values[i]);
        //} 	
        
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");  
        
        GetPopularTopicsResult topiclist = new GetPopularTopicsResult();
        
        HashMap<String, String> param = new HashMap<String, String>();        
        POSTRequest asyncHttpPost = new POSTRequest(param);
        try {
	        String str_result = asyncHttpPost.execute("http://10.0.2.2/PushNotificationService/PushNotificationService.svc/GetPopularTopics").get();
			Gson gson = new Gson(); 
			topiclist = gson.fromJson(str_result, GetPopularTopicsResult.class);        
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();        	
        for (Topic topic : topiclist.GetPopularTopicsResult){
        	Map<String, String> list = new HashMap<String, String>(2);
        	list.put("name", topic.Name);
        	list.put("numberOfSubscribers", topic.NumberOfSubscribers);
        	data.add(list);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "numberOfSubscribers"},
                new int[] {android.R.id.text1,
                           android.R.id.text2});
        
        listview.setAdapter(adapter);        
        
        listview.setOnItemClickListener(new OnItemClickListener()
        {
		        public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
		        {      
		        	EditText textBox = (EditText)findViewById(R.id.editTextAddTopic);
		        	Map<String, String> item = (Map<String, String>)arg0.getItemAtPosition(position);
		        	textBox.setText(item.get("name"));  
                }
         });
	}
	
	@Override  
	   public boolean onKeyDown(int keyCode, KeyEvent event)  
	   {  
	       if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	   Intent intent = new Intent(this, TopicPageActivity.class);
	    	   startActivity(intent);
	           return true;
	       }

	       return super.onKeyDown(keyCode, event); 
	}
	
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(TopicPageActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

}
