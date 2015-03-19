package com.micro.health.information.xmlparsing;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CountOnlyReader extends DefaultHandler {
	private int totcount;
	private String tempVal;
	
	public CountOnlyReader() {
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
			if (localName.equalsIgnoreCase("count")) {
				tempVal = "";
			}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tempVal += new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equalsIgnoreCase("count")) {
			totcount =  Integer.parseInt(tempVal);
		}

	}
	
	public int getCompleteCount() {
		return totcount;
	}
}
