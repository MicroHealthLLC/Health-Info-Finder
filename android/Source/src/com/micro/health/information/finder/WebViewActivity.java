package com.micro.health.information.finder;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

public class WebViewActivity extends Activity implements OnClickListener{
	private static final String TAG = WebViewActivity.class.getCanonicalName();
	private Button back;
	private WebView urlWebView;
	
	private String url;
	 private static final FrameLayout.LayoutParams ZOOM_PARAMS =  
			 new FrameLayout.LayoutParams(  
			 ViewGroup.LayoutParams.FILL_PARENT,  
			 ViewGroup.LayoutParams.WRAP_CONTENT,  
			 Gravity.BOTTOM);  ;;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.urlview);
        
        Bundle bn = getIntent().getExtras();
        if(bn != null)
        {
        	if(bn.containsKey(HealthApplication.URL))
        		url = bn.getString(HealthApplication.URL);
        }
        
        back = (Button) findViewById(R.id.back);
        urlWebView = (WebView) findViewById(R.id.urlWeb);
        
        back.setOnClickListener(this);
        

        FrameLayout mContentView = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);  
        final View zoom = this.urlWebView.getZoomControls();  
        mContentView.addView(zoom, ZOOM_PARAMS);  
      
        Utils.showActivityViewer(WebViewActivity.this, "Loading...", true);
        
        urlWebView.getSettings().setJavaScriptEnabled(true);
        urlWebView.getSettings().setDefaultTextEncodingName("utf-8");
        urlWebView.setWebViewClient(new MyWebViewClient());
		urlWebView.setBackgroundColor(Color.WHITE);
		urlWebView.setInitialScale(1);
        urlWebView.loadUrl(url);
        
       
    	
    }
	


	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String sUrl) {
			return super.shouldOverrideUrlLoading(view, sUrl);
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Utils.hideActivityViewer();
		}
	
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		Utils.clearDialogs();
	}


	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.back:
				onBackPressed();
				break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT)
				&& keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		finish();
	}
	
	@Override
	public void onDestroy() {
	  super.onDestroy();
	}

    
}
