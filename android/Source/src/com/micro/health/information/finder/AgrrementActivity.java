package com.micro.health.information.finder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;

public class AgrrementActivity extends Activity
{
	private ImageView setting;
	private WebView condition_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.useragreement);
		
		setting = (ImageView) findViewById(R.id.setting_back);
		condition_data =  (WebView) findViewById(R.id.condition_data);
		condition_data.loadData(getString(R.string.condition), "text/html", null);
		
		setting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
	}
	
}
