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
import android.widget.CheckBox;
import android.widget.TextView;

public class AdminFragment extends Fragment implements OnClickListener {
	
	private Button buttonClick;
	private TextView nameTextView;
	private TextView descriptionTextView;
	private TextView urlTextView;
	private CheckBox adminCheckBox;
	private ActivityListener mListener;
	private View v;
	
	public AdminFragment() {
		
	}
		
	///////////////////////////////////////////////////////////////////
	// Fragment Life-cycle events
	///////////////////////////////////////////////////////////////////
	
	@Override
    public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("DEBUG", "AdminFragment.onAttach");
		try {
            mListener = (ActivityListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ActivityListener");
        }
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		Log.d("DEBUG", "AdminFragment.onCreateView");
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.admin_view, container, false);
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
        nameTextView = (TextView) v.findViewById(R.id.editTextName);
        nameTextView.setText("breezehome");
    	descriptionTextView = (TextView) v.findViewById(R.id.editTextDescription);
    	urlTextView = (TextView) v.findViewById(R.id.editTextUrl);
    	urlTextView.setText("http://www.arla.se/");
    	adminCheckBox = (CheckBox) v.findViewById(R.id.checkBoxAdmin);
    	
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
    }
	
	public interface ActivityListener {
		public void writeToTag(String tagText);
    }
	
	@Override
	public void onClick(View v) {
		String userLevel = "user";
		if (adminCheckBox.isChecked()) {
			userLevel = "admin";
		}
		String tagText = ";" + nameTextView.getText().toString() + 
						 ";" + descriptionTextView.getText().toString() + 
						 ";" + urlTextView.getText().toString() + 
						 ";" + userLevel;
		Log.d("DEBUG", "AdminFragment.onClick");
		mListener.writeToTag(tagText);
	}
	
}
