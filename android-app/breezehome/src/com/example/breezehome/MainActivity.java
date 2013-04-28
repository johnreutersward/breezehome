package com.example.breezehome;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.SupplicantState;

public class MainActivity extends Activity {

	private WifiManager wifi;
	private WifiInfo wifiInfo;
	private TextView help;
	private ListView nsdList;
	private WifiConfiguration wifiConf;
	private IntentFilter intentFilter;
	private String breezehomeSSID;
	private String breezehomePass;
	private String breezehomeUrl;
	private String currentSSID;
	private boolean disconnectOccurred;
	public NsdManager myNsdManager;
	public NsdManager.DiscoveryListener discoveryListener; 
	public NsdManager.ResolveListener resolveListener;
	public NsdServiceInfo myNsdService;
	public ArrayList<NsdServiceInfo> resultList;
	public ArrayAdapter adapter;
	
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
	    				//startNsdScan();
	    				new OpenBrowser().execute(breezehomeUrl, MainActivity.this);
	    			}
	    		}
	    	} 
	    }
	};
	
	
	// NSD Scanner
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
			public void onServiceLost(final NsdServiceInfo serviceInfo) {
				Log.d("NSD", "NSD service _lost_ to " + serviceInfo.getServiceName() + " on " + serviceInfo.getPort() + " type " + serviceInfo.getServiceType());
				MainActivity.this.runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                	removeNsdServiceFromUI(serviceInfo);

	                }
	            });
				
			}
			
			@Override
			public void onServiceFound(NsdServiceInfo serviceInfo) {
				Log.d("NSD", "NSD service found: " + serviceInfo.getServiceName() + " on " + serviceInfo.getPort() + " type " + serviceInfo.getServiceType());
				myNsdManager.resolveService(serviceInfo, resolveListener);
				
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
		//unregisterReceiver(broadcastReceiver);
		help.setText("Please wait while I look for services...");
		myNsdManager.discoverServices( "_http._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
	}
	
	// NSD Resolver
	public void initNsdResolver() {
	    resolveListener = new NsdManager.ResolveListener() {

	        @Override
	        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
	            // Called when the resolve fails.  Use the error code to debug.
	            Log.e("NSD", "Resolve failed" + errorCode);
	        }

	        @Override
	        public void onServiceResolved(final NsdServiceInfo serviceInfo) {
	            Log.e("NSD", "Resolve Succeeded. " + serviceInfo.getHost().toString());
	            MainActivity.this.runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                	addNsdServiceToUI(serviceInfo);

	                }
	            });
	        }
	    };
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // The following TextViews will be updated in the application life cycle. s
        help = (TextView) findViewById(R.id.textViewHelp);
        nsdList = (ListView) findViewById(R.id.listView1);
        resultList = new ArrayList<NsdServiceInfo>();
        adapter = new ArrayAdapter<NsdServiceInfo>(this, android.R.layout.simple_list_item_1, resultList);
        nsdList.setAdapter(adapter);
        nsdList.setOnItemClickListener(serviceClickedhandler);
        
        
    }
   
    @Override
    protected void onResume() {
        super.onResume();
		help.setText("Checking connection status, please wait");
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        myNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        initNsdScanner();
        initNsdResolver();
    	
    	// Unless wifi is enabled there is no point to continue. 
        if (wifi.isWifiEnabled() == false) {
        	wifi.setWifiEnabled(true);
        }
        
        // TODO: Wait until we get relevant info from NFC/RDID.
        breezehomeSSID = "\"breezehome\"";
        breezehomePass = "\"whiterun\"";
        breezehomeUrl = "http://192.168.1.100/media_player/";
        
        // Check if device is already connected to a access point. 
        wifiInfo = wifi.getConnectionInfo();
        if (wifiInfo != null) {
        	currentSSID = wifiInfo.getSSID();
        	// Check if device is already connected to the specified breezehome access point. 
        	if (currentSSID.equalsIgnoreCase(breezehomeSSID)) {
        		startNsdScan();
        	}	
        }
        help.setText("Hold the device close to a tag to begin");
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	try {
    		unregisterReceiver(broadcastReceiver);
    	} catch (IllegalArgumentException e) {
    		// This is okay...
    	}
    }
    
    private OnItemClickListener serviceClickedhandler = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
		}
    };
    
    private void removeNsdServiceFromUI(NsdServiceInfo result) {
    	resultList.remove(result);
    	adapter.notifyDataSetChanged();
    }
    
    private void addNsdServiceToUI(NsdServiceInfo result) {
    	resultList.add(result);
        //resultList.add(result.getServiceName().toString() + " - " + result.getHost());
    	adapter.notifyDataSetChanged();
    	
    }
    
    // TODO: This button will be replaced with a non-interactive task started after NFC/RFID scanning is complete.
    public void authenticate(View view) {
    	help.setText("Connecting to breezehome, please wait");
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
