package com.example.breezehome;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class AdminFragment extends Fragment implements OnClickListener {
	
	Button buttonClick;
	
	public AdminFragment() {
		
	}
		
	///////////////////////////////////////////////////////////////////
	// Fragment Life-cycle events
	///////////////////////////////////////////////////////////////////
	
	@Override
    public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("DEBUG", "AdminFragment.onAttach");
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		Log.d("DEBUG", "AdminFragment.onCreateView");
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.admin_view, container, false);
        buttonClick = (Button) v.findViewById(R.id.buttonClickMe);
        buttonClick.setOnClickListener(this);
        return v;
    }

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("DEBUG", "AdminFragment.onActivityCreated");
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            Log.d("DEBUG", "savedInstaceState was null");
        }
	}
	
	@Override
	public void onResume() {
        super.onResume();
        Log.d("DEBUG", "AdminFragment.onResume");
    }
    
    @Override
	public void onPause() {
    	super.onPause();
    	Log.d("DEBUG", "AdminFragment.onPause");
    }
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("DEBUG", "AdminFragment.onSaveInstanceState");
        //outState.putInt("curChoice", mCurCheckPosition);
    }
	

	@Override
	public void onClick(View v) {
		ImageView logo = (ImageView) this.getView().findViewById(R.id.imageViewLogoAdmin);
		logo.setVisibility(View.INVISIBLE);
	}
	
}
