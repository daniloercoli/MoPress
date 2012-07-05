package com.danais.utils;

import java.util.Hashtable;

public class Globals {

	private static Globals singletonObject;
	
	private String phpsessid=""; //cookie di sessione
	private String sessionName=""; //nome della sessione es.CASSISID
	private String serverUrl=""; //url del server 
	private String nutiteqApiKey=""; //chiave delle api per accedere alle mappe
	private String cloudMadeMapKey=""; //chiave delle mappe cloudmade
	private Hashtable httpRequestProperty = null; //tabella delle proprietà delle connessioni http	
	
	// Note that the constructor is private
	private Globals() {
		
	}
	
	public static Globals getIstance() {
		if (singletonObject == null) {
			singletonObject = new Globals();
		}
		return singletonObject;
	}

	public String getPhpsessid() {
		return phpsessid;
	}

	public void setPhpsessid(String phpsessid) {
		this.phpsessid = phpsessid;
	}
	
	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getNutiteqApiKey() {
		return nutiteqApiKey;
	}

	public void setNutiteqApiKey(String nutiteqApiKey) {
		this.nutiteqApiKey = nutiteqApiKey;
	}

	public String getCloudMadeMapKey() {
		return cloudMadeMapKey;
	}

	public void setCloudMadeMapKey(String cloudMadeMapKey) {
		this.cloudMadeMapKey = cloudMadeMapKey;
	}

	public Hashtable getHttpRequestProperty() {
		return httpRequestProperty;
	}

	public void setHttpRequestProperty(Hashtable httpRequestProperty) {
		this.httpRequestProperty = httpRequestProperty;
	}

}
