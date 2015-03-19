package com.micro.health.information.finder;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryActivity extends Activity
{
	private static final int SWIPE_MIN_DISTANCE = 20;
	
	private static final int NONE = 0;
	private static final int ISFLING = 1;
	private static final int ISSINGLETAP = 2;
	
	private int currentAction = NONE;
	
	private ListView history_view;
	private ArrayList<String> a;
	private HealthApplication app;
	
 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		
		app = ((HealthApplication)HistoryActivity.this.getApplicationContext());
		history_view = (ListView) findViewById(R.id.history);
		
		a = app.getHistory();
		Collections.reverse(a);
		history_view.setAdapter(new ListAdapter(this, R.id.history, a));
	}
	
	private class ListAdapter extends ArrayAdapter<String> { 
		private ArrayList<String> mList;
		private TextView hisory_txt;
		private Button delete;
		private RelativeLayout rel;
		private GestureDetector gestureScanner;
		
		public ListAdapter(Context context, int textViewResourceId,ArrayList<String> list) { 
			super(context, textViewResourceId, list);
			this.mList = list;
			gestureScanner = new GestureDetector(myGestureListener);
		}

		public View getView(final int position, View convertView, ViewGroup parent){
			
			View view = convertView;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.history_custom, null); 
			}
		
			hisory_txt = (TextView) view.findViewById(R.id.history_txt);
			delete = (Button) view.findViewById(R.id.delete);
			rel  = (RelativeLayout) view.findViewById(R.id.relativeLayout1);
			delete.setVisibility(View.GONE);
			
			hisory_txt.setOnTouchListener(myTouchListener);
			hisory_txt.setTag(position);
			
			final String listItem = mList.get(position); 	
			
			if (listItem != null) {
				hisory_txt.setText(listItem);
				delete.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) 
					{
						app.getHistory().remove(listItem);
						a.remove(listItem);
						if(a.size()>0)
							history_view.setAdapter(new ListAdapter(HistoryActivity.this, R.id.history, a));
						else
							finish();	
					}
				});
			
				/*hisory_txt.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) 
					{
						Intent i = new Intent(HistoryActivity.this,MainActivity.class);
						i.putExtra("term", listItem);
						startActivity(i);
						finish();
					}
				});*/
			}
			return view;
		}
		
		private OnGestureListener myGestureListener = new OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				Log.v("log_tag", "singletap");
				currentAction = ISSINGLETAP;
				return true;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				currentAction = NONE;
				try {
			           if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
			        	   	currentAction = ISFLING;
					        return true;
			           }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
			        	   currentAction = ISFLING;
			               return true;
			           }
			   } catch (Exception e) {
		    	   e.printStackTrace();
	               return false;
		       }
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		};
		
		private OnTouchListener myTouchListener = new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(gestureScanner.onTouchEvent(event)) {
					if(currentAction == ISFLING) {
						for(int i=0;i<history_view.getChildCount();i++)
						{
							history_view.getChildAt(i).findViewById(R.id.delete).setVisibility(View.GONE);
						}
						((RelativeLayout)v.getParent()).findViewById(R.id.delete).setVisibility(View.VISIBLE);
					}
					if(currentAction == ISSINGLETAP)
					{
						Intent i = new Intent(HistoryActivity.this,MainActivity.class);
						i.putExtra("term", mList.get(Integer.parseInt(v.getTag().toString())));
						startActivity(i);
						finish();
					}
					currentAction = NONE;
					return true;
				} else {
					currentAction = NONE;
					return false;
				}
			}
		};
	}   
	
}
