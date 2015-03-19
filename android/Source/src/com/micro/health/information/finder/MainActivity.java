/*
Contributed by Tim Huynh Le, for MicroHealth, LLC.,
under the GN Affero General Public License 3.0
*/

package com.micro.health.information.finder;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.micro.health.information.webservice.DBAdapter;
import com.micro.health.information.webservice.SearchTerm;


public class MainActivity extends Activity implements OnClickListener
{

	private static final String TAG = MainActivity.class.getCanonicalName();
	private HealthApplication app;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private String iSearchTerm = "";
	private Animation bottomUpAnimation,bottomDownAnimation;
	
	ImageView search,setting,history,zoom_chk,zoom,share,share_cancel,share_browse,share_email;
	ImageView next,back,setting_bottom;
	ImageView clear;
	RelativeLayout header;
	AutoCompleteTextView search_txt;
	WebView healthdata;  
	
	private int currentPos = 0;
	
	boolean isdata =false;
	RelativeLayout bottomup,footer,re1;
	
	ArrayList<SearchTerm> historyforback = new ArrayList<SearchTerm>();
	ArrayList<SearchTerm> searchTerm = new ArrayList<SearchTerm>();
	
	private SearchTerm currentSearchTerm;
	TextView site,datafrom;
	ListView mList;
	ArrayList<String> matches = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		  
		Bundle bn = getIntent().getExtras();
		if(bn!=null)
		{
			if(bn.containsKey("term"))
	        {
	        	if(bn.getString("term").length()>0) {
	        		iSearchTerm = bn.getString("term");
	        	}
	        }
		}
		
		search = (ImageView) findViewById(R.id.search_btn);
		header = (RelativeLayout) findViewById(R.id.header);
		footer = (RelativeLayout) findViewById(R.id.footer);
		re1 = (RelativeLayout) findViewById(R.id.relativeLayout1); 
		search_txt = (AutoCompleteTextView)findViewById(R.id.search_txt);
		healthdata = (WebView) findViewById(R.id.health_content);
		back = (ImageView) findViewById(R.id.back);
		next = (ImageView) findViewById(R.id.next);
		share = (ImageView) findViewById(R.id.share);
		share_cancel = (ImageView) findViewById(R.id.share_cancel);
		share_browse = (ImageView) findViewById(R.id.share_browser);
		share_email = (ImageView) findViewById(R.id.share_email);
		zoom = (ImageView) findViewById(R.id.zoom);
		zoom_chk = (ImageView) findViewById(R.id.zoom_chk);
		bottomup = (RelativeLayout) findViewById(R.id.bottomup);
		history = (ImageView) findViewById(R.id.history_relative);
		setting = (ImageView) findViewById(R.id.setting);
		setting_bottom = (ImageView) findViewById(R.id.setting_bottom);
		site = (TextView) findViewById(R.id.site);
		datafrom = (TextView) findViewById(R.id.datafrom);
		mList = (ListView) findViewById(R.id.list);
		clear = (ImageView) findViewById(R.id.clear);
		
		clear.setVisibility(View.GONE);
		
		clear.setOnClickListener(this);
		back.setOnClickListener(this);
		next.setOnClickListener(this);
		history.setOnClickListener(this);
		share.setOnClickListener(this);
		share_cancel.setOnClickListener(this);
		share_browse.setOnClickListener(this);
		share_email.setOnClickListener(this);
		zoom.setOnClickListener(this);
		zoom_chk.setOnClickListener(this);
		setting.setOnClickListener(this);
		setting_bottom.setOnClickListener(this);
		site.setOnClickListener(this);
		search.setOnClickListener(this);
		
		back.setEnabled(false);
		next.setEnabled(false);
		
		search_txt.addTextChangedListener(searchTextWatcher);
	    search.setEnabled(false);
	
	    int no=0;
		app = ((HealthApplication)MainActivity.this.getApplicationContext());
		
		search_txt.setThreshold(3);
		
		long time  = System.currentTimeMillis();
		DBAdapter a = DBAdapter.getAdapterInstance(getApplicationContext());
		
		a.createdatabase();
		SQLiteDatabase db = a.openDataBase();
		
		searchTerm = a.getAllTerms();
		no = searchTerm.size();
		
		db.close();
		
		PackageManager pm = getPackageManager();
	    List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
        	search.setEnabled(true);
        } else {
        	search.setEnabled(false);
        }
		
        animationInitialization();
		
		if(no > 0) {
			ArrayAdapter<SearchTerm> adapter = new SearchTermAdapter(MainActivity.this, R.layout.list_item, searchTerm);
			search_txt.setAdapter(adapter);
		}
		
		
      	
		search_txt.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				currentSearchTerm = null;
				search((SearchTerm)arg0.getItemAtPosition(arg2));
				currentSearchTerm = (SearchTerm)arg0.getItemAtPosition(arg2);
			}
        });
        
        search_txt.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				if(event.getAction()==KeyEvent.ACTION_DOWN && keyCode==KeyEvent.KEYCODE_ENTER)
				{
					searchTxtTerm(search_txt.getText().toString());
				}	
				return false;
			}
		});
        
        healthdata.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				setBackNextButtons();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
				intent.putExtra(HealthApplication.URL, url);
				startActivity(intent);
				return true;
			}
        	
        });
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		search_txt.setText(iSearchTerm);
		searchTxtTerm(iSearchTerm);
	}



	public void animationInitialization() {
		bottomUpAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
		bottomDownAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_down);
	}

	public void showMenu() {
		bottomup.clearAnimation();
		bottomup.startAnimation(bottomUpAnimation);
		bottomup.setVisibility(View.VISIBLE);
	}

	public void hideMenu() {
		bottomup.clearAnimation();
		bottomup.startAnimation(bottomDownAnimation);
		bottomup.setVisibility(View.GONE);
	}
	
	public void search(SearchTerm term1)
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(search_txt.getWindowToken(), 0);
		mList.setVisibility(View.GONE);
		
		String term = term1.getName();
		if(term.length()>0)
		{
			healthdata.setVisibility(View.VISIBLE);
			site.setVisibility(View.GONE);
			datafrom.setVisibility(View.GONE);
			
			if(app.getHistory().contains(term))
				app.getHistory().remove(term);	
				app.setHistory(term);
				historyforback.add(term1);
				healthdata.setBackgroundColor(Color.parseColor("#E2EEF2"));
				header.setVisibility(View.GONE);
				currentPos++;
			
				DBAdapter db = new DBAdapter(MainActivity.this);
				db.openDataBase();
				term1 = db.getSearchByTerm(term1);
				currentSearchTerm = term1;
				db.close();
			
				healthdata.loadData(currentSearchTerm.getSummary() , "text/html", "utf-8");
				setBackNextButtons();
				
				footer.setVisibility(View.VISIBLE);
				isdata = true;
				search_txt.setText("");
				search_txt.setHint(term);
		}
		else
			Toast.makeText(getApplicationContext(), "Please enter disease name", 1).show();
	}
	
	private void setBackNextButtons()
	{
		
		back.setEnabled(false);
		next.setEnabled(false);
		if(currentPos>0)
		{
			back.setEnabled(true);
		}
		if(currentPos < historyforback.size() - 1)
		{
			next.setEnabled(true);
		}
	}
	
	public void searchTxtTerm(String term1)
	{
		currentSearchTerm = null;
		if(term1.length() > 0) {
			for(int i=0;i<searchTerm.size();i++)
			{
				if(searchTerm.get(i).getName().toLowerCase().contains(term1.toLowerCase())) {
					currentSearchTerm = searchTerm.get(i);
					search(currentSearchTerm);
					break;
				}
			}
			if(currentSearchTerm == null)
			{
				Toast.makeText(getApplicationContext(), "Data for this term not found", 1).show();
			}
		}
	}

	 private void startVoiceRecognitionActivity() {
	        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
	        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	    }
	 
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 	super.onActivityResult(requestCode, resultCode, data);
	        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
	            // Fill the list view with the strings the recognizer thought it could have heard
	            matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	            healthdata.setVisibility(View.GONE);
	            footer.setVisibility(View.GONE);
	            mList.setVisibility(View.VISIBLE);
	            mList.setAdapter(new ListAdapter(MainActivity.this, R.id.list,matches));
	        }
	 } 
	 
	 
	 private class ListAdapter extends ArrayAdapter<String> { 
			private ArrayList<String> mList1;  
			TextView speak_txt;
			RelativeLayout rel;
			
			public ListAdapter(Context context, int textViewResourceId,ArrayList<String> list) {
				super(context, textViewResourceId, list);
				this.mList1 = list;
			}

			public View getView(final int position, View convertView, ViewGroup parent){
				
				View view = convertView;
				try{
				if (view == null) {
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.speak_custum, null); 
				}
			
				final String listItem = mList1.get(position); 	// --CloneChangeRequired
				if (listItem != null) {
					// setting list_item views	
					rel = (RelativeLayout) view.findViewById(R.id.re);
					speak_txt = (TextView) view.findViewById(R.id.textView1);

					speak_txt.setText(listItem);
					rel.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) 
						{
							search_txt.setText(listItem.toString());
							searchTxtTerm(listItem.toString());
							mList.setVisibility(View.GONE);
							healthdata.setVisibility(View.VISIBLE);
						}
					});
		    	
				}
				}catch(Exception e){
					Log.v(TAG, e.toString());			
				}
				return view;
			}
		}


	@Override
	public void onBackPressed() {
		if(healthdata.getVisibility()==0) {
			mList.setVisibility(View.GONE);
			site.setVisibility(View.VISIBLE);
			datafrom.setVisibility(View.VISIBLE);
			header.setVisibility(View.VISIBLE);
			back.setEnabled(false);
			next.setEnabled(false);
			bottomup.setVisibility(View.GONE);
			healthdata.setVisibility(View.GONE);
			footer.setVisibility(View.GONE);
			re1.setVisibility(View.VISIBLE);
			zoom_chk.setVisibility(View.GONE);
			search_txt.setText("");
		} else {
			finish();			
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.clear:
				search_txt.setText("");
				break;
			case R.id.site:
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.medlineplus.gov"));
				startActivity(i);
				break;
			case R.id.search_btn:
				 startVoiceRecognitionActivity();
				break;
			case R.id.zoom:
				if(isdata) {
					header.setVisibility(View.GONE);
					re1.setVisibility(View.GONE);
					footer.setVisibility(View.GONE);
					bottomup.setVisibility(View.GONE);
					zoom_chk.setVisibility(View.VISIBLE);	
				}
				break;
			case R.id.share:
				if(isdata) {
					showMenu();
					search_txt.setEnabled(false);
					search_txt.setClickable(false);
				}
				break;
			case R.id.share_cancel:
				hideMenu();	
				search_txt.setEnabled(true);
				search_txt.setClickable(true);
				break;
			case R.id.share_browser:
				if(isdata)
				{
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentSearchTerm.getUrl()));
					startActivity(browserIntent);

					hideMenu();	
					search_txt.setEnabled(true);
					search_txt.setClickable(true);	
				}
				break;
			case R.id.share_email:
				if(isdata)
				{
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					String[] recipients = new String[] { "" };
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,recipients);
					emailIntent.putExtra(android.content.Intent.EXTRA_CC, "");
					emailIntent.putExtra(android.content.Intent.EXTRA_BCC, "tonyinae@mac.com");
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"");
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, currentSearchTerm.getUrl());
					emailIntent.setType("text/plain");
					startActivity(Intent.createChooser(emailIntent, "Send mail..."));
					
					hideMenu();	
					search_txt.setClickable(true);	
					search_txt.setEnabled(true);
				}
				break;
			case R.id.zoom_chk:
				header.setVisibility(View.GONE);
				re1.setVisibility(View.VISIBLE);
				
				footer.setVisibility(View.VISIBLE);
				bottomup.setVisibility(View.GONE);
				zoom_chk.setVisibility(View.GONE);
				setBackNextButtons();
				break;
			case R.id.history_relative:
				if(app.getHistory().size()>0) {
					Intent in = new Intent(MainActivity.this,HistoryActivity.class);
					startActivity(in);
				} else {
					Toast.makeText(getApplicationContext(), "No History", 1).show();
				}
				break;
			case R.id.setting:
				Intent is = new Intent(MainActivity.this,SettingActivity.class);
				startActivity(is);
				break;
			case R.id.setting_bottom:
				Intent isb = new Intent(MainActivity.this,SettingActivity.class);
				startActivity(isb);
				break;
			case R.id.back:
				if(currentPos > 0)
				{
					currentPos--;
					search_txt.setText("");
					search_txt.setHint(historyforback.get(currentPos).getName());
					healthdata.loadData(historyforback.get(currentPos).getSummary(), "text/html", "utf-8");
				}
				setBackNextButtons();
				break;
			case R.id.next:
				if(currentPos < historyforback.size()-1)
				{
					currentPos++;
					search_txt.setText("");
					search_txt.setHint(historyforback.get(currentPos).getName());
					healthdata.loadData(historyforback.get(currentPos).getSummary(), "text/html", "utf-8");
				}
				setBackNextButtons();
				break;
		}
	} 
	
	private class SearchTermAdapter extends ArrayAdapter<SearchTerm> implements Filterable {
	    private ArrayList<SearchTerm> items;
	    private ArrayList<SearchTerm> itemsAll;
	    private ArrayList<SearchTerm> suggestions;
	    private int viewResourceId;

	    public SearchTermAdapter(Context context, int viewResourceId, ArrayList<SearchTerm> items) {
	        super(context, viewResourceId, items);
	        this.items = items;
	        this.itemsAll = (ArrayList<SearchTerm>) items.clone();
	        this.suggestions = new ArrayList<SearchTerm>();
	        this.viewResourceId = viewResourceId;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(viewResourceId, null);
	        }
	        SearchTerm searchTerm = items.get(position);
	        if (searchTerm != null) {
	            TextView customerNameLabel = (TextView) v.findViewById(R.id.main);
	            if (customerNameLabel != null) {
	                customerNameLabel.setText(searchTerm.getName());
	            }
	        }
	        return v;
	    }

	    @Override
	    public Filter getFilter() {
	        return nameFilter;
	    }

	    Filter nameFilter = new Filter() {
	        public String convertResultToString(Object resultValue) {
	            String str = ((SearchTerm)(resultValue)).getName(); 
	            return str;
	        }
	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            if(constraint != null) {
	                suggestions.clear();
	                for (SearchTerm customer : itemsAll) {
	                    if(customer.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
	                        suggestions.add(customer);
	                    }
	                }
	                FilterResults filterResults = new FilterResults();
	                filterResults.values = suggestions;
	                filterResults.count = suggestions.size();
	                return filterResults;
	            } else {
	                return new FilterResults();
	            }
	        }
	        @Override
	        protected void publishResults(CharSequence constraint, FilterResults results) {
	        	if(results != null && results.count > 0)
	        	{
	        	  clear();
	              ArrayList<SearchTerm> newValues = (ArrayList<SearchTerm>) results.values;
	              for (int i = 0; i < newValues.size(); i++) {
	                  add(newValues.get(i));
	              }
	              if(results.count>0){
	                  notifyDataSetChanged();
	              } else{
	                  notifyDataSetInvalidated();
	              }
	        	}
	        }
	    };

	
	}


	
	private TextWatcher searchTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(s.length() > 0) {
				clear.setVisibility(View.VISIBLE);
			} else {
				clear.setVisibility(View.GONE);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			
		}
	};
}
