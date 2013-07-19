package com.google.android.gcm.demo.app;

import android.content.Context;
import android.net.ConnectivityManager;

public class InternetConnection {

	
	public boolean checkInternetConnection(Context activityContext){
		ConnectivityManager conMgr = (ConnectivityManager) activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;

            /* New Handler to start the Menu-Activity
             * and close this Splash-Screen after some seconds.*/
        } else {
              return false;           
        }
    }
		
	
}
