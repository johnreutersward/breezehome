package com.example.breezehome;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class MainActivity extends Activity {

	WifiManager wifi;
	WifiInfo wifiInfo;
	TextView status;
	WifiConfiguration wifiConf;
	String breezehomeSSID;
	String breezehomePass;
	String breezehomeUrl;
	String currentSSID;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView) findViewById(R.id.textViewStatus);
        
        // Unless wifi is enabled there is no point to continue.
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
        	wifi.setWifiEnabled(true);
        }
        
        // Wait until we get relevant info from NFC/RDID.
        breezehomeSSID = "\"test\"";
        breezehomePass = "\"test\"";
        breezehomeUrl = "http://www.google.se";
        
        // Check if we are connected.
        wifiInfo = wifi.getConnectionInfo();
        if (wifiInfo != null) {
        	currentSSID = wifiInfo.getSSID();
        	status.append(currentSSID);
        	// Check if we are on the breezehome ap.
        	if (currentSSID.equalsIgnoreCase(breezehomeSSID)) {
        		openBrowser(breezehomeUrl);
        	}	
        }
    }
    
    private void openBrowser(String localUrl) {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(localUrl));
		startActivity(browserIntent);
    } 
    
    public void authenticate(View view) {
    	
    	// Set up the connection configuration.
    	wifiConf = (WifiConfiguration) new WifiConfiguration();
    	wifiConf.SSID = breezehomeSSID;
    	wifiConf.preSharedKey = breezehomePass;
    	wifiConf.hiddenSSID = false;
    	int netID = wifi.addNetwork(wifiConf);
    	wifi.enableNetwork(netID, true);
    	
    	// If we are connected to the correct AP we can now go over to the mobile site.
    	
    	openBrowser(breezehomeUrl);
    }
    
}
