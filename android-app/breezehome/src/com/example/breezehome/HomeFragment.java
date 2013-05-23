package com.example.breezehome;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HomeFragment extends Fragment {
	
		public Activity mainActivity;
		OnServiceSelectedListener mListener;
	
		// UI
		public TextView helpTextView;
		public ListView serviceListView;
		
		// NFC
		public ArrayList<BreezehomeService> serviceList;
		public ArrayAdapter<BreezehomeService> adapter;
		
		public HomeFragment() {
			
		}
		
		///////////////////////////////////////////////////////////////////
		// Fragment Life-cycle events
		///////////////////////////////////////////////////////////////////
		
		@Override
	    public void onAttach(Activity activity) {
			super.onAttach(activity);
			Log.d("DEBUG", "HomeFragment.onAttach");
			this.mainActivity = activity;
			try {
	            mListener = (OnServiceSelectedListener) mainActivity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement OnServiceSelectedListener");
	        }
	    }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState) {
			Log.d("DEBUG", "HomeFragment.onCreateView");
			View mView = inflater.inflate(R.layout.home_view, container, false);
	        helpTextView = (TextView) mView.findViewById(R.id.textViewHelp);
	        return mView;
	    }
		
		@Override
	    public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			Log.d("DEBUG", "HomeFragment.onActivityCreated");
	        if (savedInstanceState != null) {
	            // Restore last state for checked position.
	            Log.d("DEBUG", "savedInstaceState NOT null");
	        }
	        serviceListView = (ListView) this.getView().findViewById(R.id.list);
	        
	        serviceList = new ArrayList<BreezehomeService>();
	        
	        // Mock data
	        serviceList.add(new BreezehomeService("Breeze1", "Media", "http://www.google.se/", true, "asdf"));
	        serviceList.add(new BreezehomeService("Breeze2", "Cake", "http://www.arla.se/", false, "qwert"));
	        serviceList.add(new BreezehomeService("Breeze3", "Light", "http://mobil.hitta.se/", false, "fish"));
	       
	        
	        adapter = new ArrayAdapter<BreezehomeService>(getActivity(), R.layout.service_row, R.id.servceListTextItem , serviceList);
	        serviceListView.setAdapter(adapter);
	        serviceListView.setOnItemClickListener(serviceClickedhandler);
		}
		
		@Override
		public void onResume() {
	        super.onResume();
	        Log.d("DEBUG", "HomeFragment.onResume");
			helpTextView.setText("Checking connection status");
	    }
	    
	    @Override
		public void onPause() {
	    	super.onPause();
	    	Log.d("DEBUG", "HomeFragment.onPause");
	    }
	    
	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	    	Log.d("DEBUG", "HomeFragment.onSaveinstanceState");
	    	outState.putString("helpText", helpTextView.getText().toString());
	    }
	    
	    
		///////////////////////////////////////////////////////////////////
		// Activity callback's
		///////////////////////////////////////////////////////////////////
		
		public void addService(BreezehomeService service) {
	    	serviceList.add(service);
	    	adapter.notifyDataSetChanged();
	    }
	    
	    public void removeService(BreezehomeService service) {
	    	serviceList.remove(service);
	    	adapter.notifyDataSetChanged();    	
	    }
	    
	    public void setHelpText(String text) {
			Log.d("DEBUG", "HomeFragment.setHelpText");
			if (helpTextView != null) {
				helpTextView.setText(text);
			}
		}
			    
	    
		///////////////////////////////////////////////////////////////////
		// Service List events
		///////////////////////////////////////////////////////////////////
	    
	    public interface OnServiceSelectedListener {
	        public void onServiceSelected(String url);
	        public ArrayList<BreezehomeService> getServiceList();
	    }
	    
	    private OnItemClickListener serviceClickedhandler = new OnItemClickListener() {

	    	@Override
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				Log.d("DEBUG", "User clicked on: " + serviceList.get(position));
				mListener.onServiceSelected(serviceList.get(position).getUrl());
			}
	    };  
	    
		
}
