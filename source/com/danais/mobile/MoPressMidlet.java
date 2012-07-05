package com.danais.mobile;


import java.util.Hashtable;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.danais.utils.Globals;


public class MoPressMidlet extends MIDlet {
  public static MoPressMidlet instance;
  private StartCanvas splashCanvas;
  private MainCanvas kmlCanv;
  

  public MoPressMidlet() {
    instance = this;
  }

  protected void startApp() throws MIDletStateChangeException {
	if (splashCanvas == null) {
		Globals globals = Globals.getIstance();

    	//Inizializzo le proprietà dell'applicativo
		String serverUrl = instance.getAppProperty("serverurl");
	    String serverSessionName = instance.getAppProperty("server_session_name");
	    String nutiteqApiKey=instance.getAppProperty("nutiteq_api_key");
		String cloudMadeMapKey=instance.getAppProperty("cloudmade_map_key");

		globals.setServerUrl(serverUrl);
		globals.setSessionName(serverSessionName);
		globals.setNutiteqApiKey(nutiteqApiKey);
		globals.setCloudMadeMapKey(cloudMadeMapKey);
	
		//creo un insieme di prorietà che saranno utilizzate da tutte le connessioni http efettuate dall'applicativo
		Hashtable httpRequestProperty = new Hashtable();
		//"User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"
		httpRequestProperty.put("User-Agent","Profile/MIDP-2.0 Configuration/CLDC-1.1");
		httpRequestProperty.put("Content-Language", "en-US");
		globals.setHttpRequestProperty(httpRequestProperty);
				
		kmlCanv=new MainCanvas(instance);
      	splashCanvas = new StartCanvas(instance,kmlCanv);      	
	}
            
   if(splashCanvas.isInitialized()==false) {
	   Display.getDisplay(instance).setCurrent(splashCanvas);
   } else {
    	Display.getDisplay(instance).setCurrent(kmlCanv);
    }
  }

  protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
    splashCanvas = null;
    kmlCanv = null;
    instance = null;
    notifyDestroyed();
  }

  protected void pauseApp() {  }
  
}