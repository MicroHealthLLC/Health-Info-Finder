package com.micro.health.information.finder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableRow;

public class SettingActivity extends Activity
{
	Button done;
	TableRow feedback,privacy,agreement,about;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		feedback = (TableRow) findViewById(R.id.feedback);
		privacy = (TableRow) findViewById(R.id.privacy);
		agreement = (TableRow) findViewById(R.id.user_agrrement);
		about = (TableRow) findViewById(R.id.About);
		
		done = (Button) findViewById(R.id.done);
		feedback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				String[] recipients = new String[] { "feedback@microhealthonline.com" };
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,recipients);
				emailIntent.putExtra(android.content.Intent.EXTRA_CC, "");
				emailIntent.putExtra(android.content.Intent.EXTRA_BCC, "tonyinae@mac.com");
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"FeedBack for Health Info 1.1.1");
				//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,health.get(1).toString());
				emailIntent.setType("text/plain");
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));				
			}
		});
		
		done.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
			
				finish();
				
			}
		});
		
	about.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
			
				Intent i = new Intent(SettingActivity.this,AboutActivity.class);
				startActivity(i);
				
			}
		});
	
	
	agreement.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) 
		{
		
			Intent i = new Intent(SettingActivity.this,AgrrementActivity.class);
			startActivity(i);
			
		}
	});
	
	privacy.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) 
		{
		
			Intent i = new Intent(SettingActivity.this,PrivacyActivity.class);
			startActivity(i);
			
		}
		});
	}
     
}
