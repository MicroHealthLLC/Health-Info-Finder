package com.micro.health.information.xmlparsing;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.micro.health.information.webservice.SearchTerm;

public class CompleteReader extends DefaultHandler {
	private ArrayList<SearchTerm> adDataList;
	private SearchTerm temp;
	private int totcount;
	private String tempVal;
	private boolean isMedi;
	
 	
	public CompleteReader() {
		adDataList = new ArrayList<SearchTerm>();
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
			if (localName.equalsIgnoreCase("MedicalTopics")) {
				totcount =  Integer.parseInt(attributes.getValue("totalEnglish"));
			}
			if (localName.equalsIgnoreCase("MedicalTopic") && attributes.getValue("langcode").equals("English")) {
				temp = new SearchTerm();
				isMedi = true;
				if(attributes.getValue("status") != null)
					temp.setStatus(attributes.getValue("status"));
				else
					temp.setStatus("new");
			} else if (localName.equalsIgnoreCase("MedicalTopic") && !attributes.getValue("langcode").equals("English")) {
				isMedi = false;
				temp = null;
			}
			tempVal = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(isMedi) {
			tempVal += new String(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
			if(isMedi) {
				if (localName.equalsIgnoreCase("MedicalTopic")) {
					adDataList.add(temp);
				} else if (localName.equalsIgnoreCase("MedicalTopicName")) {
					temp.setName(tempVal);
				} else if (localName.equalsIgnoreCase("FullSummary")) {
					temp.setSummary(tempVal);
				} else if (localName.equalsIgnoreCase("ID")) {
					temp.setId(Integer.parseInt(tempVal));
				} else if (localName.equalsIgnoreCase("URL")) {
					temp.setUrl(tempVal);
				}
			}
	}

	public ArrayList<SearchTerm> getCompleteList() {
		return adDataList;
	}
	
	public int getCompleteCount() {
		return totcount;
	}
}
