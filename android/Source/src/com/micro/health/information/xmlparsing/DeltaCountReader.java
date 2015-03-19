package com.micro.health.information.xmlparsing;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DeltaCountReader extends DefaultHandler {
	private int totcount;
	private String genDate;
 	
	public DeltaCountReader() {
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
				genDate = attributes.getValue("enddate");
			}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	}

	public int getDeltaCount() {
		return totcount;
	}
	
	public String getDeltaGen() {
		return genDate;
	}
}
