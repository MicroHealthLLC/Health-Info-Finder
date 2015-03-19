package com.micro.health.information.xmlparsing;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

import com.micro.health.information.finder.HealthApplication;
import com.micro.health.information.webservice.SearchTerm;

public class XMLParser {
	protected static final String TAG = XMLParser.class.getCanonicalName();
	private static CompleteReader myCompleteHandler;
	private static CountOnlyReader myCountOnlyHandler;
	private static DeltaCountReader myDeltaCountHandler;
	
	public XMLParser(int initHandler) {
		if (initHandler == HealthApplication.COMPLETE) {
			myCompleteHandler = new CompleteReader();
		} else if (initHandler == HealthApplication.COUNTONLY) {
			myCountOnlyHandler = new CountOnlyReader();
		} else if (initHandler == HealthApplication.DELTACOUNT) {
			myDeltaCountHandler = new DeltaCountReader();
		}
	}
	
	public int parseXml(String parseString, int type) {
		try {
			/* Create a URL we want to load some xml-data from. */
			URI lUri = new URI(parseString);

			// Prepares the request.
			HttpClient lHttpClient = new DefaultHttpClient();
			HttpGet lHttpGet = new HttpGet();
			lHttpGet.setURI(lUri);

			// Sends the request and read the response
			HttpResponse lHttpResponse = lHttpClient.execute(lHttpGet);
			InputStream lInputStream = lHttpResponse.getEntity().getContent();
			int status = lHttpResponse.getStatusLine().getStatusCode();
			
			/* Get a SAXParser from the SAXPArserFactory. */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
			if(type == HealthApplication.COMPLETE)	{
				xr.setContentHandler(myCompleteHandler);	
			} else if(type == HealthApplication.COUNTONLY)	{
					xr.setContentHandler(myCountOnlyHandler);	
			} else if(type == HealthApplication.DELTACOUNT)	{
				xr.setContentHandler(myDeltaCountHandler);	
			}
			
			
			/* Create a new ContentHandler and apply it to the XML-Reader */

			/* Parse the xml-data from our URL. */
			
			if (!((status >= 200) && (status < 300))) {
			}
			else
			{
				InputSource is = new InputSource(lInputStream);
				is.setEncoding("UTF-8");
				xr.parse(is);
				
			}
			/* Parsing has finished. */
			return HealthApplication.SUCCESS;
		}
		catch(UnknownHostException e)
		{
			Log.e(TAG, e.toString());
			return HealthApplication.FAIL;
		}
		catch (Exception e) {
			/* Display any Error to the GUI:. */
			Log.e(TAG, e.toString() + ":" );
			e.printStackTrace();
			return HealthApplication.FAIL;
		}
	
	}
	
	public int parseXml(InputStream is, int type) {
		try {
			/* Get a SAXParser from the SAXPArserFactory. */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
			if(type == HealthApplication.COMPLETE)	{
				xr.setContentHandler(myCompleteHandler);	
			} else if(type == HealthApplication.COUNTONLY)	{
				xr.setContentHandler(myCountOnlyHandler);	
			} else if(type == HealthApplication.DELTACOUNT)	{
				xr.setContentHandler(myDeltaCountHandler);	
			}
			
			/* Create a new ContentHandler and apply it to the XML-Reader */

			/* Parse the xml-data from our URL. */
			InputSource is1 = new InputSource(is);
			is1.setEncoding("UTF-8");
			xr.parse(is1);
			/* Parsing has finished. */
			return HealthApplication.SUCCESS;
		}
		catch(UnknownHostException e)
		{
			Log.e(TAG, e.toString());
			return HealthApplication.FAIL;
		}
		catch (Exception e) {
			/* Display any Error to the GUI:. */
			Log.e(TAG, "Exception XML parser: " + e.toString());
			return HealthApplication.FAIL;
		}
	
	}
	
	
		
	public ArrayList<SearchTerm> getCompleteList() {
		return myCompleteHandler.getCompleteList();
	}
	
	public int getCompleteCount() {
		return myCompleteHandler.getCompleteCount();
	}
	
	public int getCountOnly() {
		return myCountOnlyHandler.getCompleteCount();
	}
	
	public int getDeltaCount() {
		return myDeltaCountHandler.getDeltaCount();
	}
	
	public String getDeltaGen() {
		return myDeltaCountHandler.getDeltaGen();
	}
	
}
