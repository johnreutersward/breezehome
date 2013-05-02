package com.example.breezehome;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
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
import android.widget.Toast;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.SupplicantState;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;

public class MainActivity extends Activity {

	// Wifi
	private WifiManager wifi;
	private WifiInfo wifiInfo;
	private WifiConfiguration wifiConf;
	private IntentFilter intentFilter;
	private boolean disconnectOccurred;
	private String currentSSID;
	
	// UI
	private TextView helpTextView;
	private ListView serviceListView;
	
	// NFC
	private String breezehomeSSID;
	private String breezehomePass;
	private String breezehomeName;
	private String breezehomeDescription;
	private String breezehomeUrl;
	private BreezehomeService breezehomeService;
	public ArrayList<BreezehomeService> serviceList;
	public ArrayAdapter<BreezehomeService> adapter;
	public NfcAdapter mNfcAdapter;
	private PendingIntent mNfcPendingIntent;
	
	// Monitor WiFi state during authentication.
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
	    				Log.d("DEBUG", "Connected to access point");
	    				// Add service to List
	    				addService(breezehomeService);
	    				helpTextView.setText("Select a service or scan a new tag.");
	    			}
	    		}
	    	} 
	    }
	};
	
	private void addService(BreezehomeService service) {
    	serviceList.add(service);
    	adapter.notifyDataSetChanged();
    }
    
    private void removeService(BreezehomeService service) {
    	serviceList.remove(service);
    	adapter.notifyDataSetChanged();    	
    }
	
    // NFC/RFID scanner
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		resolveIntent(intent);
	}
    
    private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				NdefMessage[] messages = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					messages[i] = (NdefMessage) rawMsgs[i];
				}
				String str = new String(
						messages[0].getRecords()[0].getPayload());
				Log.d("NFC",str);
				String[] nfcInfo = str.split(";");
				if (nfcInfo.length == 4) {
					Log.d("NFC","Scanned a service tag");
					addService(new BreezehomeService(nfcInfo[1], nfcInfo[2], nfcInfo[3]));
				} else if (nfcInfo.length == 6) {
					Log.d("NFC","Scanned a auth/service tag");
					breezehomeSSID = "\"" + nfcInfo[1] + "\"";
					breezehomePass = "\"" + nfcInfo[2] + "\"";
					breezehomeService = new BreezehomeService(nfcInfo[3], nfcInfo[4], nfcInfo[5]);
					wifiAuth();
				}
				for (int i = 0; i < nfcInfo.length; i++) {
					Log.d("NFC",nfcInfo[i]);
				}
			}
		}
	}
    
    private void wifiAuth() {
    	helpTextView.setText("Connecting to breezehome, please wait");
    	wifiConf = (WifiConfiguration) new WifiConfiguration();
    	wifiConf.SSID = breezehomeSSID;
    	wifiConf.preSharedKey = breezehomePass;
    	wifiConf.hiddenSSID = false;
    	int netID = wifi.addNetwork(wifiConf);
    	wifi.enableNetwork(netID, true);
    	intentFilter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
    	registerReceiver(broadcastReceiver, intentFilter);
    	
    }
    
	// MainActivity UI
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("DEBUG", "Setting up UI elements");
        helpTextView = (TextView) findViewById(R.id.textViewHelp);
        serviceListView = (ListView) findViewById(R.id.listViewService);
        serviceList = new ArrayList<BreezehomeService>();
        adapter = new ArrayAdapter<BreezehomeService>(this, R.layout.service_row, serviceList);
        serviceListView.setAdapter(adapter);
        serviceListView.setOnItemClickListener(serviceClickedhandler);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Log.d("NFC", "NFC Not available");
        }
    }
   
    @Override
    protected void onResume() {
        super.onResume();
		helpTextView.setText("Checking connection status");
		
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	// Unless wifi is enabled there is no point to continue. 
        if (wifi.isWifiEnabled() == false) {
        	wifi.setWifiEnabled(true);
        }
        
        // Check if device is already connected to a access point. 
        wifiInfo = wifi.getConnectionInfo();
        
        if (wifiInfo != null) {
        	currentSSID = wifiInfo.getSSID();
        	if (breezehomeSSID != null) { 
        		Log.d("DEBUG", currentSSID.replace("\"", "") + " : " + breezehomeSSID.replace("\"", ""));
        		// Check if device is already connected to the specified breezehome access point. 
        		if (currentSSID.replace("\"", "").equalsIgnoreCase(breezehomeSSID.replace("\"", ""))) {
        			Log.d("DEBUG", "Device is connected to breezehome AP");
        			if (serviceList.isEmpty()) {
        				helpTextView.setText("Connected to brezzehome, scan a tag");
        			} else {
        				helpTextView.setText("Select a service or scan a new tag.");
        			}
        		} else {
        			helpTextView.setText("Hold the device close to a tag to begin");
        		}
        	} else {
        		helpTextView.setText("Hold the device close to a tag to begin");
        	}
        }
        
        Intent intent = new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNfcAdapter.enableForegroundDispatch(this, pIntent, null, null);
        
        
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	try {
    		unregisterReceiver(broadcastReceiver);
    	} catch (IllegalArgumentException e) {
    		// This is okay...
    	}
    	mNfcAdapter.disableForegroundDispatch(this);
    }
    
    private OnItemClickListener serviceClickedhandler = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			Log.d("DEBUG", "User clicked on: " + serviceList.get(position));
			new OpenBrowser().execute(serviceList.get(position).getUrl(), MainActivity.this);
		}
    };  
    
}
