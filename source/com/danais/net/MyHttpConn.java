package com.danais.net;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.danais.utils.observer.Observable;

public abstract class MyHttpConn extends Observable implements Runnable   {
	
	protected String  authMessage;
	protected String urlConnessione;
	//protected String requestCookie = null; 

	protected Hashtable httpRequestProperty;
	protected MyHttpConnResponse connResponse=null;
	
	public MyHttpConn(String url, Hashtable requestProperty) {
		this.urlConnessione=url;
		
		this.httpRequestProperty=requestProperty;
		
		Thread t = new Thread(this);
		t.start();
	}

	// Do the connection and processing on
	// a separate thread...
	public void run() {
		authMessage="";
		
		HttpConnection conn = null;
		
		//#debug error
		System.out.println("connection url: "+urlConnessione);
		
		try {
			conn = (HttpConnection) Connector.open(urlConnessione);

		    //setta le proprietà della richiesta
			if(this.httpRequestProperty!=null){
			    for (Enumeration e = httpRequestProperty.keys(); e.hasMoreElements();) {
		    	    String chiaveCorrente=(String)e.nextElement();
		    	    String valoreCorrente=(String)httpRequestProperty.get(chiaveCorrente);
		    	    conn.setRequestProperty(chiaveCorrente,valoreCorrente);
			    }				
			}

			int rc = conn.getResponseCode();
			if( rc == HttpConnection.HTTP_OK ){
				connResponse=getResponse(conn);											
			} else {
			    // deal with errors, warnings, redirections, etc.
				throw new Exception("Problemi nel contattare il server di autenticazione, response code: "+conn.getResponseCode());
			}

		} catch (Exception e) {
			 this.setErrorMessage(e);
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				this.setErrorMessage(e);
			}
		}
		notifyObservers(connResponse);
	}

	//ritorna un oggetto di tipo response sulla connessione http
	public abstract MyHttpConnResponse  getResponse(HttpConnection conn) throws IOException;
	
	
	private void setErrorMessage(Exception e){
		connResponse=new MyHttpConnResponse();
		connResponse.setError(true);
		connResponse.setResponse(e.getMessage());
		//#debug error
		System.out.println("setErrorMessage failed: "+e);
	}
}