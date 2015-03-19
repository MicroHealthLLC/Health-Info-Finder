package com.micro.health.information.finder;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TagFindingVisitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.micro.health.information.webservice.DBAdapter;
import com.micro.health.information.xmlparsing.XMLParser;

public class Splash extends Activity
{
	private static final String TAG = Splash.class.getName();
	private static final int COMPLETE = 0;
	private static final int UPDATE = 1;
	
	private static String completeLink;
	private static String deltaLink;
	
	private static int completeCount=0;
	private static int deltaCount=0;
	
	public static SQLiteDatabase db;
	int dbTermCount =0;
	DBAdapter a;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		a = DBAdapter.getAdapterInstance(getApplicationContext());
		
		a.createdatabase();
		db = a.openDataBase();
		dbTermCount = a.getTermCount();
		db.close();
			
		MyThread t = new MyThread(this);
		t.start();
	}
	class MyThread extends Thread {
		Context con;

		MyThread(Context con) {
			this.con = con;
		}
        
		public void run(){
	    		XMLParser xmlParser = new XMLParser(HealthApplication.COUNTONLY);
	    		xmlParser.parseXml("http://wsearch.nlm.nih.gov/ws/query?db=healthTopics", HealthApplication.COUNTONLY);
	    		int xmlTermCount= xmlParser.getCountOnly();
	    		xmlParser = null;
	    		
	    		if(HealthApplication.prefs.getTotalCount() ==  0 || dbTermCount == 0)
	    		{
	    			parseHTML(COMPLETE);
		    		xmlParser = new XMLParser(HealthApplication.COMPLETE);
		    		xmlParser.parseXml(completeLink, HealthApplication.COMPLETE);
		    		a.saveTermsToDb(xmlParser.getCompleteList());
		    		HealthApplication.prefs.setTotalCount(xmlTermCount);
		    		xmlParser = null;
		    	}
	    		
	    		parseHTML(UPDATE);
    			xmlParser = new XMLParser(HealthApplication.DELTACOUNT);
	    		xmlParser.parseXml(deltaLink, HealthApplication.DELTACOUNT);
	    		if(xmlParser.getDeltaCount() > 0 && !xmlParser.getDeltaGen().equals(HealthApplication.prefs.getDeltaGenDate())) {
	    			xmlParser = new XMLParser(HealthApplication.COMPLETE);
		    		xmlParser.parseXml(deltaLink, HealthApplication.COMPLETE);
		    		HealthApplication.prefs.setDeltaGenDate(xmlParser.getDeltaGen());	
		    		a.saveTermsToDb(xmlParser.getCompleteList());
		    		db = a.openDataBase();
		    		dbTermCount = a.getTermCount();
		    		db.close();
		    		HealthApplication.prefs.setTotalCount(dbTermCount);	
	    		}
	    		xmlParser = null;
	    		
	    		Intent i = new Intent(Splash.this,MainActivity.class);
				startActivity(i);
				finish();
		}
	}
	
	private String parseHTML(int type)
	{
		String outputText;
	    Parser parser = null;
        try {
    		parser =  new Parser("http://www.nlm.nih.gov/medlineplus/xml.html");
            String tags[] = { "A" };

            TagFindingVisitor visitor = new TagFindingVisitor(tags);
            try {
                parser.visitAllNodesWith(visitor);
                outputText =  "there are " + visitor.getTags(0).length + " A nodes.\n";
                for (int i = 0;i<visitor.getTags(0).length;i++) {
                	String tmp = visitor.getTags(0)[i].toString();
                	String tmp1 = visitor.getTags(0)[i].toPlainTextString();
                	if(type == COMPLETE) {
	                	if(tmp1 != null && tmp1.equals("Complete MedlinePlus Vocabulary and Summaries XML") && completeCount == 0 && type == COMPLETE) {
	                		String[] spl = tmp.split(";");
	                		String[] spl1 = spl[0].split(":");
	                		completeLink = spl1[1] +":"+ spl1[2];
	                		completeLink = completeLink.trim();
	                		completeCount++;
	                		break;
	                	}
                	}
                	if(type == UPDATE) {
	                	if(tmp1 != null && tmp1.equals("MedlinePlus Vocabulary and Summaries Delta XML") && deltaCount == 0) { 
	                		String[] spl = tmp.split(";");
	                		String[] spl1 = spl[0].split(":");
	                		deltaLink = spl1[1] +":" +spl1[2];
	                		deltaLink = deltaLink.trim();
	                		deltaCount++;
	                		break;
	                	}
                	}
                	outputText = outputText + visitor.getTags(0)[i].toString();
                }
                return outputText;
            } catch (ParserException e) {
                Log.e(TAG, "Exception in +++ ON CREATE +++ \n" + "parser.visitAllNodesWith (visitor) failed\n" + e.toString());
                return null;
		    }
        } catch (ParserException e1) {
            Log.e(TAG, "Exception in +++ ON CREATE +++ \n" + "creation of parser failed\n" + e1.toString());
            return null;
        }
 	}
}
