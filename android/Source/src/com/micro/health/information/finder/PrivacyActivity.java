package com.micro.health.information.finder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class PrivacyActivity extends Activity
{
	ImageView setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacypolicy);
		
		setting = (ImageView) findViewById(R.id.setting_back);
		
		
		setting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
	}
	
}
