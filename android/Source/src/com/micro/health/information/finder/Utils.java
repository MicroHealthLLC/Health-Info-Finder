package com.micro.health.information.finder;

import android.content.Context;


public class Utils {
	private static final String TAG = "Utils";
	private static int SIZE = 100;
	private static ActivityIndicator activityIndicator;
	public static void hideActivityViewer() {
		if (activityIndicator != null) {
			activityIndicator.dismiss();
		}
	}

	public static void showActivityViewer(Context context, String str, boolean inverse) {
		if (activityIndicator == null) {
			activityIndicator = new ActivityIndicator(context, str, inverse);
		}
		activityIndicator.show();
	} 
	
	 public static void clearDialogs()
	 {
		 activityIndicator = null;
	 }
	
}
