package com.example.breezehome;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/*
 * Opens device browser once we have received HTTP 200 from server.
 * Assumes that the server exists and is online.
 */
public class OpenBrowser extends AsyncTask<Object, Void, Boolean> {
	
	private String url;
	private MainActivity callerActivity;

	protected Boolean doInBackground(Object... params) {
		this.url = (String) params[0];
		this.callerActivity = (MainActivity) params[1];
		Log.d("DEBUG", "OpenBrowser waiting for response from: " + url);
			while (true) {
				try {
					URL _url = new URL(url);
					HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
					connection.setRequestProperty("User-Agent", "android-device");
					connection.setRequestProperty("Connection", "close");
					connection.setConnectTimeout(1000);
					if (connection.getResponseCode() == 200) {
						return true;
					}
				} catch (MalformedURLException e) {
					// Do nothing
				} catch (IOException e) {
					// Do nothing
				}
			}
     }
	 
	 protected void onPostExecute(Boolean response) {
		 if (response) {
			 Log.d("DEBUG", "Got HTTP 200 from server, starting browser intent");
			 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			 callerActivity.startActivity(browserIntent);
		 }
     }

}
