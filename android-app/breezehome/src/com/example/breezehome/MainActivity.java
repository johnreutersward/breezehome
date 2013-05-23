package com.example.breezehome;

import java.util.ArrayList;

import com.example.breezehome.MainActivity.TabListener;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;
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

public class MainActivity extends Activity implements 
		HomeFragment.OnServiceSelectedListener, 
		WebServiceFragment.GetUrl {
	
	public static Context appContext;
	public NfcAdapter mNfcAdapter;
	private String breezehomeSSID;
	private String breezehomePass;
	private String breezehomeName;
	private String breezehomeDescription;
	private String breezehomeUrl;
	private BreezehomeService breezehomeService;
	private WifiManager wifi;
	private WifiInfo wifiInfo;
	private boolean disconnectOccurred;
	private String currentSSID;
	
	private HomeFragment homeFragment;
	private TabListener<HomeFragment> homeTabListener;
	ActionBar actionbar;
	
	ActionBar.Tab homeTab;
    ActionBar.Tab webServiceTab;
    ActionBar.Tab adminTab;
	
	private WebServiceFragment webServiceFragment;
	private TabListener<WebServiceFragment> webServiceTabListener;
	
	private String selectedUrl = "";
	
	private AdminFragment adminFragment;
	
	///////////////////////////////////////////////////////////////////
	// Activity Life-cycle events
	///////////////////////////////////////////////////////////////////
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "MainActivity.onCreate");
        appContext = getApplicationContext();

        actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        homeTab = actionbar.newTab().setText("Home").setIcon(R.drawable.home_fragment_icon);
        webServiceTab = actionbar.newTab().setText("Service").setIcon(R.drawable.webservice_fragment_icon);
        adminTab = actionbar.newTab().setText("Admin").setIcon(R.drawable.admin_fragment_icon);
        
        //homeFragment = new HomeFragment();
        homeTabListener = new TabListener<HomeFragment>(this, "home", HomeFragment.class);
        homeTab.setTabListener(homeTabListener);
        
        webServiceTab.setTabListener(new TabListener<WebServiceFragment>(this, "webService", WebServiceFragment.class));
        adminTab.setTabListener(new TabListener<AdminFragment>(this, "admin", AdminFragment.class));
        
        actionbar.addTab(homeTab);
        actionbar.addTab(webServiceTab);
        actionbar.addTab(adminTab);
        
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Log.d("NFC", "NFC Not available");
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUG", "MainActivity.onResume");
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
        			// HomeFragment should show that we are connected!
//        			if (serviceList.isEmpty()) {
//        				helpTextView.setText("Connected to brezzehome, scan a tag");
//        			} else {
//        				helpTextView.setText("Select a service or scan a new tag.");
//        			}
        		} else {
        			// HomeFragment should show that we need to scan first!
        			//helpTextView.setText("Hold the device close to a tag to begin");
        		}
        	} else {
        		// HomeFragment should show that we need to scan first!
        		//helpTextView.setText("Hold the device close to a tag to begin");
        	}
        }
		Intent intent = new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNfcAdapter.enableForegroundDispatch(this, pIntent, null, null);
        
        if (homeFragment == null) {
        	homeFragment = (HomeFragment)getFragmentManager().findFragmentByTag("home");
       
        }
        if (webServiceFragment == null) {
        	webServiceFragment = (WebServiceFragment)getFragmentManager().findFragmentByTag("webService");
        }
        if (adminFragment == null) {
        	adminFragment = (AdminFragment)getFragmentManager().findFragmentByTag("admin");
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Log.d("DEBUG", "MainActivity.onPause");
    	try {
    		unregisterReceiver(broadcastReceiver);
    	} catch (IllegalArgumentException e) {
    		// This is okay...
    	}
    	mNfcAdapter.disableForegroundDispatch(this);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("DEBUG", "MainActivity.onSaveInstanceState");
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }
    
    
	///////////////////////////////////////////////////////////////////
	// Action Bar events
	///////////////////////////////////////////////////////////////////
    
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        
    	private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.

        }
    }
    
    
	///////////////////////////////////////////////////////////////////
	// HomeFragment interface events
	///////////////////////////////////////////////////////////////////

    @Override
	public void onServiceSelected(String url) {
		Log.d("DEBUG", url);
		if (!url.equalsIgnoreCase(this.selectedUrl)) {
			this.selectedUrl = url;
			actionbar.selectTab(webServiceTab);
		}
	}
    
    @Override
	public ArrayList<BreezehomeService> getServiceList() {
		// TODO Auto-generated method stub
		return null;
	}

    
	///////////////////////////////////////////////////////////////////
	// WebServiceFragment interface events
	///////////////////////////////////////////////////////////////////
    
	@Override
	public String onGetUrl() {
		return this.selectedUrl;
	}
	
    
	///////////////////////////////////////////////////////////////////
	// NFC Scanner
	///////////////////////////////////////////////////////////////////
    
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		resolveNfcIntent(intent);
	}
    
    private void resolveNfcIntent(Intent intent) {
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
				boolean isAdmin = false;
				if (nfcInfo.length == 5) {
					Log.d("NFC","Scanned a service tag");
					if (nfcInfo[4].equalsIgnoreCase("admin")) {
						isAdmin = true;
					}
					HomeFragment homeFragment = (HomeFragment)getFragmentManager().findFragmentByTag("home");
					homeFragment.addService(new BreezehomeService(nfcInfo[1], nfcInfo[2], nfcInfo[3], isAdmin, str));
				} else if (nfcInfo.length == 7) {
					Log.d("NFC","Scanned a auth/service tag");
					breezehomeSSID = "\"" + nfcInfo[1] + "\"";
					breezehomePass = "\"" + nfcInfo[2] + "\"";
					if (nfcInfo[6].equalsIgnoreCase("admin")) {
						isAdmin = true;
					}
					
					breezehomeService = new BreezehomeService(nfcInfo[3], nfcInfo[4], nfcInfo[5], isAdmin, str);
					wifiInfo = wifi.getConnectionInfo();
			        
			        if (wifiInfo != null) {
			        	currentSSID = wifiInfo.getSSID();
			        }
					if (currentSSID.replace("\"", "").equalsIgnoreCase(breezehomeSSID.replace("\"", ""))) {
						HomeFragment homeFragment = (HomeFragment)getFragmentManager().findFragmentByTag("home");
						homeFragment.addService(breezehomeService);
					} else {
						wifiAuth();
					}
				}
				for (int i = 0; i < nfcInfo.length; i++) {
					Log.d("NFC",nfcInfo[i]);
				}
			}
		}
	}
    
    
	///////////////////////////////////////////////////////////////////
	// WiFi state events
	///////////////////////////////////////////////////////////////////
    
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
 	    				//addService(breezehomeService);
 	    				//helpTextView.setText("Select a service or scan a new tag.");
 	    				HomeFragment homeFragment = (HomeFragment)getFragmentManager().findFragmentByTag("home");
						homeFragment.addService(breezehomeService);
 	    			}
 	    		}
 	    	} 
 	    }
 	};

    private void wifiAuth() {
    	// TELL HOME THAT WE ARE CONNECTIING helpTextView.setText("Connecting to breezehome, please wait");
    	WifiConfiguration wifiConf = (WifiConfiguration) new WifiConfiguration();
    	wifiConf.SSID = breezehomeSSID;
    	wifiConf.preSharedKey = breezehomePass;
    	wifiConf.hiddenSSID = false;
    	int netID = wifi.addNetwork(wifiConf);
    	wifi.enableNetwork(netID, true);
    	registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
    }

	
    
}