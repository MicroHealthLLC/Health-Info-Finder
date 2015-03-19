package com.micro.health.information.webservice;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public class DBAdapter extends SQLiteOpenHelper 
{
	private static final String TAG = DBAdapter.class.getCanonicalName();
	private static String DB_NAME = "health_db.sqlite";
	private String DB_PATH  = "";
	
	private static DBAdapter adb;
	private SQLiteDatabase db;
	private Context myContext;
	public DBAdapter(Context context)
	{
		
		super(context, DB_NAME, null,1);
	
		this.myContext = context;
		DB_PATH = "/data/data/"+myContext.getPackageName()+"/databases/";
	}

	
	public static synchronized DBAdapter getAdapterInstance(Context context)
	{
		if(adb == null)
		{
		  adb = new DBAdapter(context);
		}
		
		return adb;
		
	}
	
	
	public void createdatabase()
	{
		boolean dbExist = checkDataBase();
		if(dbExist)
		{
			
			
		}
		else
		{
			this.getReadableDatabase();
			try {
                copyDataBase();
            } catch (IOException e) {
            	Log.v(TAG,e.toString());
                throw new Error("Error copying database");
            }

		}
	}
	
	private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

	 private boolean checkDataBase() {
         SQLiteDatabase checkDB = null;
         try {
             String myPath = DB_PATH + DB_NAME;
             checkDB = SQLiteDatabase.openDatabase(myPath, null,
                     SQLiteDatabase.OPEN_READONLY);
         } catch (SQLiteException e) {
         }
         if (checkDB != null) {
             checkDB.close();
         }
         return checkDB != null ? true : false;
     }
	 
	 
	 public SQLiteDatabase openDataBase() throws SQLException {
         // Open the database
         String myPath = DB_PATH + DB_NAME;
         
         db= SQLiteDatabase.openDatabase(myPath, null,
                 SQLiteDatabase.OPEN_READWRITE);
         
         return db;
     }
         
	 
	 public SearchTerm getSearchByTerm(SearchTerm term)
	 {
		 Cursor cr  = db.rawQuery("select summary, url from health_topic where id = " + term.getId(), null);
		 if(cr!=null)
		 {
			cr.moveToFirst();
			do
			{
				term.setSummary(cr.getString(0));
				term.setUrl(cr.getString(1));
			}while(cr.moveToNext());
			cr.close();
		 }
		 return term;
			
	 }
	 
	 public ArrayList<SearchTerm> getAllTerms()
	 {
		 ArrayList<SearchTerm> searchTerm = new ArrayList<SearchTerm>();
		 Cursor cr  = db.rawQuery("select id, topic from health_topic", null);
		 if(cr!=null)
		 {
			cr.moveToFirst();
			do
			{
				searchTerm.add(new SearchTerm(cr.getInt(0), cr.getString(1),"", "new", ""));
			}while(cr.moveToNext());
			cr.close();
		 }
		 return searchTerm;
			
	 }
	 
	 public int getTermCount()
	 {
		int no = 0;
		Cursor cr = db.rawQuery("select count(*) from health_topic", null);
		if(cr!=null)
		{
			cr.moveToFirst();
			do
			{
				no = cr.getInt(0);
			}while(cr.moveToNext());
			cr.close();
		}
		return no;
	 }
	 
	 public void saveTermsToDb(ArrayList<SearchTerm> termsList)
	 {
		openDataBase();
		db.beginTransaction();
		try {
			for (int i = 0; i <termsList.size(); i++) {
				if (termsList.get(i).getStatus().equals("new")) {
					insertTerm(termsList.get(i));
				} else if (termsList.get(i).getStatus().equals("modified")) {
					updateTerm(termsList.get(i));
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
	 }
	 
	 public void insertTerm(SearchTerm terms) {
			String sql = "INSERT INTO health_topic (id, topic, summary, url) VALUES (?, ?, ?, ?)";
			SQLiteStatement insert = db.compileStatement(sql);
			insert.bindLong(1, terms.getId());
			insert.bindString(2, terms.getName());
			insert.bindString(3, terms.getSummary());
			insert.bindString(4, terms.getUrl());
			long s = insert.executeInsert();
			if (s == -1) {
				Log.v(TAG, "Error while inserting term: " + terms.toString());
			}
			insert.clearBindings();
			insert.close();
	}
	 
	 public boolean updateTerm(SearchTerm terms) {
			ContentValues args = new ContentValues();
			args.put("topic", terms.getName());
			args.put("summary", terms.getSummary());
			args.put("url", terms.getUrl());
			boolean s = db.update("health_topic", args,
					"id=" + terms.getId(), null) > 0;
			if (!s) {
				Log.v(TAG, "Error while updating term:" + terms.getId());
			}
			return s;
		}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public synchronized void close() {
 
    	    if(db != null)
    		    db.close();
 
    	    super.close();
 
	}
}
