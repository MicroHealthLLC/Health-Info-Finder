package com.micro.health.information.finder;

import java.util.ArrayList;

import android.app.Application;

import com.micro.health.information.webservice.AppPreferences;

public class HealthApplication extends Application
{
	
	public static final String TOTALCOUNT = "totalcount";
	public static final String GENERATEDDATE = "generatedDate";
	public static final String DELTAGENERATEDDATE = "deltageneratedDate";
	
	public static final String URL = "url";
	
	public static final int SUCCESS = 1;
	public static final int FAIL = 2;
	
	//XML PARSERS
	public static final int COMPLETE = 1;
	public static final int COUNTONLY = 3;
	public static final int DELTACOUNT = 4;
	
	public static AppPreferences prefs;
	
	public ArrayList<String> history = new ArrayList<String>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		prefs = new AppPreferences(getApplicationContext());
	}
	
	public ArrayList<String> getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history.add(history);
	}
}
