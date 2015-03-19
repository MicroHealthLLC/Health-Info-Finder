package com.micro.health.information.webservice;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.micro.health.information.finder.HealthApplication;

public class AppPreferences {
	
	private static final String APP_SHARED_PREFS = "healthinfoprefs"; //  Name of the file -.xml
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;
    
    public AppPreferences(Context context)
    {
    	this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

   public int getTotalCount() {
		return appSharedPrefs.getInt(HealthApplication.TOTALCOUNT, 0);
	}

	public void setTotalCount(int totcount) {
		prefsEditor.putInt(HealthApplication.TOTALCOUNT, totcount);
        prefsEditor.commit();
	}

	public int getGenDate() {
		return appSharedPrefs.getInt(HealthApplication.GENERATEDDATE, 1);
	}

	public void setGenDate(int password) {
		prefsEditor.putInt(HealthApplication.GENERATEDDATE, password);
        prefsEditor.commit();
	}
	
	public String getDeltaGenDate() {
		return appSharedPrefs.getString(HealthApplication.DELTAGENERATEDDATE, "");
	}

	public void setDeltaGenDate(String password) {
		prefsEditor.putString(HealthApplication.DELTAGENERATEDDATE, password);
        prefsEditor.commit();
	}
}
