package com.example.breezehome;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class MainActivity extends Activity {

	WifiManager wifi;
	WifiInfo wifiInfo;
	TextView status;
	WifiConfiguration wifiConf;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        status = (TextView) findViewById(R.id.textViewStatus);
        
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifi.getConnectionInfo();
        status.append(wifiInfo.getSSID());
        
        
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void authenticate(View view) {
    	
    	// Set up the connection configuration.
    	wifiConf = (WifiConfiguration) new WifiConfiguration();
    	wifiConf.SSID = "\"testAP\"";
    	wifiConf.preSharedKey = "\"password\"";
    	wifiConf.hiddenSSID = false;
    	int netID = wifi.addNetwork(wifiConf);
    	boolean connectSuccess = wifi.enableNetwork(netID, true);
    	
    	// If we are connected to the correct AP we can now go over to the mobile site.
    	if (connectSuccess) {
    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
    		startActivity(browserIntent);
    	}
    }
    
}
