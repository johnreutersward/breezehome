package com.example.breezehome;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.SupplicantState;

public class MainActivity extends Activity {

	private WifiManager wifi;
	private WifiInfo wifiInfo;
	private TextView status;
	private TextView help;
	private WifiConfiguration wifiConf;
	private IntentFilter intentFilter;
	private String breezehomeSSID;
	private String breezehomePass;
	private String breezehomeUrl;
	private String currentSSID;
	private boolean disconnectOccurred;
	public NsdManager myNsdManager;
	public NsdManager.DiscoveryListener discoveryListener; 
	
	// Check if breezehome responds to HTTP requests. 
	private class BreezeHomeWebCheck extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... urls) {
			Log.d("DEBUG", "AsyncTask doInBackground running");
			String checkURL = urls[0];
			boolean connectedToBreeze = false;
				do {
					try {
						URL url = new URL(checkURL);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setRequestProperty("User-Agent", "yourAgent");
						connection.setRequestProperty("Connection", "close");
						connection.setConnectTimeout(1000);
						if (connection.getResponseCode() == 200) {
							connectedToBreeze = true;
							return true;
						}
					} catch (MalformedURLException e) {
						
					} catch (IOException e) {
						
					}
				} while (connectedToBreeze == false);
				return false;
	     }
		 
		 protected void onPostExecute(Boolean result) {
			 Log.d("DEBUG", "AsyncTask onPostExecute running");
			 if (result == true) {
				 Log.d("DEBUG", "AsyncTask onPostExecute opening browser");
				 startNsdScan();
				 //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(breezehomeUrl));
				 //startActivity(browserIntent);
			 } 
	     }
	 }
	
	
	// Monitor the wifi state to discover when we have connected to the breezehome access point.
	public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	final String action = intent.getAction();
	    	Log.d("DEBUG", "onReceive: " + action);
	    	if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
	    		SupplicantState stateInfo = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
	    		Log.d("DEBUG", "onReceive [SUPPLICANT_STATE_CHANGED_ACTION] EXTRA_NEW_STATE = " + stateInfo.name());
	    		if (stateInfo.name() == SupplicantState.DISCONNECTED.name()) {
	    			disconnectOccurred = true;
	    		} else if  (stateInfo.name() == SupplicantState.COMPLETED.name()) {
	    			if (disconnectOccurred == true) {
	    				disconnectOccurred = false;
	    				openBrowser(breezehomeUrl);
	    			}
	    		}
	    	} 
	    }
	};
	
	public void initNsdScanner() {
		
		Log.d("NSD", "initNsdScanner()");
		discoveryListener = new NsdManager.DiscoveryListener() {
			
			@Override
			public void onStopDiscoveryFailed(String serviceType, int errorCode) {
				Log.d("NSD", "ERROR: NSD STOP FAILED + [" + errorCode + "]");
				myNsdManager.stopServiceDiscovery(this);
			}
			
			@Override
			public void onStartDiscoveryFailed(String serviceType, int errorCode) {
				Log.d("NSD", "ERROR: NSD START FAILED [" + errorCode + "]");
				myNsdManager.stopServiceDiscovery(this);
				
			}
			
			@Override
			public void onServiceLost(NsdServiceInfo serviceInfo) {
				Log.d("NSD", "NSD service _lost_ to " + serviceInfo.getServiceName() + " on " + serviceInfo.getPort() + " type " + serviceInfo.getServiceType());
				
			}
			
			@Override
			public void onServiceFound(NsdServiceInfo serviceInfo) {
				Log.d("NSD", "NSD service found: " + serviceInfo.getServiceName() + " on " + serviceInfo.getPort() + " type " + serviceInfo.getServiceType());
				
			}
			
			@Override
			public void onDiscoveryStopped(String serviceType) {
				Log.d("NSD", "NSD stopped " + serviceType);
				
			}
			
			@Override
			public void onDiscoveryStarted(String serviceType) {
				Log.d("NSD", "NSD running " + serviceType);
				
			}
		};	
	}
	
	public void startNsdScan() {
		myNsdManager.discoverServices( "_http._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // The following TextViews will be updated in the application life cycle. 
        status = (TextView) findViewById(R.id.textViewStatus);
        help = (TextView) findViewById(R.id.textViewHelp);
        
        
    }
   
    @Override
    protected void onResume() {
        super.onResume();
		help.setText("Hold the device close to a tag to begin");
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        myNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        initNsdScanner();
    	
    	// Unless wifi is enabled there is no point to continue. 
        if (wifi.isWifiEnabled() == false) {
        	wifi.setWifiEnabled(true);
        }
        
        // TODO: Wait until we get relevant info from NFC/RDID.
        breezehomeSSID = "\"breezehome\"";
        breezehomePass = "\"whiterun\"";
        breezehomeUrl = "http://192.168.1.102/media_player/";
        
        // Check if device is already connected to a access point. 
        wifiInfo = wifi.getConnectionInfo();
        if (wifiInfo != null) {
        	currentSSID = wifiInfo.getSSID();
        	status.append(currentSSID);
        	// Check if device is already connected to the specified breezehome access point. 
        	if (currentSSID.equalsIgnoreCase(breezehomeSSID)) {
        		openBrowser(breezehomeUrl);
        	}	
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    private void openBrowser(String localUrl) {
    	help.setText("Connecting to breezehome, please wait");
    	new BreezeHomeWebCheck().execute(localUrl);
    } 
    
    // TODO: This button will be replaced with a non-interactive task started after NFC/RFID scanning is complete.
    public void authenticate(View view) {
    	wifiConf = (WifiConfiguration) new WifiConfiguration();
    	wifiConf.SSID = breezehomeSSID;
    	wifiConf.preSharedKey = breezehomePass;
    	wifiConf.hiddenSSID = false;
    	int netID = wifi.addNetwork(wifiConf);
    	wifi.enableNetwork(netID, true);
    	intentFilter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
    	registerReceiver(broadcastReceiver, intentFilter);
    }
}
