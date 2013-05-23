package com.example.breezehome;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebServiceFragment extends Fragment {
    
	private WebView mWebView;
    private boolean mIsWebViewAvailable;
    GetUrl mListener;

    public WebServiceFragment() {
    
    }
    
	///////////////////////////////////////////////////////////////////
	// Fragment Life-cycle events
	///////////////////////////////////////////////////////////////////
    
    @Override
    public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("DEBUG", "WebServiceFragment.onAttach");
		try {
            mListener = (GetUrl) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnGetUrlListener");
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.d("DEBUG", "WebServiceFragment.onCreateView");
        if (mWebView != null) {
        	Log.d("DEBUG", "WebView not null");
        	String activityUrl = mListener.onGetUrl();
        	String webViewUrl = mWebView.getUrl();
        	if (webViewUrl == null) {
        		webViewUrl = "";
        	}
        	Log.d("DEBUG", "activityUrl: " + activityUrl + " webViewUrl: " + webViewUrl);
        	if (webViewUrl.equalsIgnoreCase(activityUrl)) {
        		Log.d("DEBUG", "WebView url unchanged");
                return mWebView;
        	}
        }
        mWebView = new WebView(getActivity());
        mIsWebViewAvailable = true;
        Log.d("DEBUG", "WebView created");
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        String setUrl = mListener.onGetUrl();
        if (setUrl != null) {
        	mWebView.loadUrl(setUrl);	
        }
        return mWebView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
       super.onActivityCreated(savedInstanceState);
       Log.d("DEBUG", "WebServiceFragment.onActivityCreated");
       mWebView.restoreState(savedInstanceState);
    }
    
    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
        Log.d("DEBUG", "WebServiceFragment.onResume");
        
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
        Log.d("DEBUG", "WebServiceFragment.onPause");
    }
    
    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
        Log.d("DEBUG", "WebServiceFragment.onDestroyView");
    }
    
    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
        Log.d("DEBUG", "WebServiceFragment.onDestroy");
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
       Log.d("DEBUG", "WebServiceFragment.onSaveInstanceState");
       mWebView.saveState(outState);
    }
     

	///////////////////////////////////////////////////////////////////
	// Activity callback's
	///////////////////////////////////////////////////////////////////

    public interface GetUrl {
        public String onGetUrl();
    }
    
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }
}