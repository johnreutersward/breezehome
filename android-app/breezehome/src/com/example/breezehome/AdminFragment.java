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
import android.widget.Toast;

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
        nameTextView.setText("breezehomeStart");
    	descriptionTextView = (TextView) v.findViewById(R.id.editTextDescription);
    	descriptionTextView.setText("Technology is exciting!");
    	urlTextView = (TextView) v.findViewById(R.id.editTextUrl);
    	urlTextView.setText("http://");
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
	
	///////////////////////////////////////////////////////////////////
	// Activity callback's
	///////////////////////////////////////////////////////////////////
	
	public interface ActivityListener {
		public void writeToTag(String tagText);
    }
	
	///////////////////////////////////////////////////////////////////
	// Tag Writing events
	///////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick(View v) {
		String name = nameTextView.getText().toString(); 
		String description =  descriptionTextView.getText().toString();
		String url =  urlTextView.getText().toString(); 
		if (name.length() > 0 || description.length() > 0 || url.length() > 0) {
			String userLevel = "user";
			if (adminCheckBox.isChecked()) {
				userLevel = "admin";
				url += "?isdadmin=True";
			}
			String tagText = ";" + name + 
							 ";" + description + 
							 ";" + url + 
							 ";" + userLevel;
			Log.d("DEBUG", "AdminFragment.onClick");
			mListener.writeToTag(tagText);
			Toast.makeText(getActivity().getApplicationContext(), "Hold a tag close to the device", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Please fill in all the fields!", Toast.LENGTH_LONG).show();
		}
	}
	
}
