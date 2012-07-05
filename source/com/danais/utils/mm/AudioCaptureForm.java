//#condition  polish.api.mmapi


package com.danais.utils.mm;

import java.io.ByteArrayOutputStream;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import javax.microedition.media.Manager;
import javax.microedition.media.Player;

import javax.microedition.media.control.RecordControl;

public class AudioCaptureForm extends Form {

	private TextField description;
	private Player capturePlayer;
	
	private RecordControl recordControl;
	private final MultimediaCaptureController ctrl;
	
	private ByteArrayOutputStream bos;
	private final String encoding;
	
	// flag to keep track of when recording is started
	private boolean recording = false;
	
	//#style itemFlashText
	StringItem onAirStringItem = new StringItem("Mic status:","ON AIR!!");
	//#style itemText
	StringItem offAirStringItem = new StringItem("Mic status:","off");
	
	public AudioCaptureForm(MultimediaCaptureController ctrl, String encoding) throws Exception {
		super("Audio Capture");
		this.ctrl = ctrl;
		
		if(!encoding.trim().equals("")){
			this.encoding ="?"+encoding;
		} else {
			this.encoding = encoding;
		}
		
		
		Image image = Image.createImage("/mic.gif");
		append(image);
		
		this.append(offAirStringItem);
	
	}
	 
	public String getDescription(){
		return description.getString().trim();
	}
	

	  public void startAudioRecording()  {
			// start recording
		  // if the mic is recording at the moment, then just return
	    // unless the key code is DOWN to stop recording
		  if(recording) return;
	  	
		 this.delete(1);
		 this.append(onAirStringItem);
		  
		// svolgo l'azione in un thread separato per evitare il blocco della
		// gui sull'emulatore e su alcuni telefoni
		new Thread() {
			public void run() {
				
			    try {
			      
			      // create the capture player
		      capturePlayer = Manager.createPlayer("capture://audio"+encoding);
		
		      if (capturePlayer != null) {
		        
		        // if created, realize it
		        capturePlayer.realize();
		      
		        // and grab the RecordControl
		        recordControl = (RecordControl)capturePlayer.getControl("javax.microedition.media.control.RecordControl");        
		       
		        // if RecordControl is null throw exception
		        if(recordControl == null) throw new Exception("RecordControl not available for audio");
		        
		        // create the buffer in which recording will be done
		        bos = new ByteArrayOutputStream();
		        
		        // set the output of recording
		        recordControl.setRecordStream(bos);
		
		        // start the underlying player
		        capturePlayer.start();
		        
		        // and the actual recorder
		        recordControl.startRecord();
		        
		        // set flag
		        recording = true;
		        		        
		      } else {
		         ctrl.displayError( new Exception("Capture Audio Player is not available"), "") ;
		      }      
		    } catch(Exception e) {
		    	cleanUp();
		    	ctrl.displayError(e, "Capture Audio Player recording error") ;
		    }
	    
			}// fine run
		}.start();
	  }
	  
	  public void stopRecording(){
		 if (!recording) return;
		 
		 this.delete(1);
		 this.append(offAirStringItem);
		 
		  // complete the recording
	      try {


	          // stop recording 
	          recordControl.stopRecord();
	          
	          // commit the recording
	          recordControl.commit();
	          
	          // and close the Player instance
	          capturePlayer.stop();

	          // flush the buffer
	          bos.flush();
	    	  
	        capturePlayer = null;
			

	      } catch(Exception ex) {
	    	 ctrl.displayError(ex, "Recording error") ;
	        cleanUp();
	        return;
	      }
	      MediaObject mObject = new MediaObject();
	      
	      // set the media data on this entry
	      mObject.setMediaData(bos.toByteArray());
	      
	      // set the media content type
	      mObject.setContentType(recordControl.getContentType());
	      
		//#debug
		System.out.println("Captured photo with encoding= "+"encoding"+" and content type= "+recordControl.getContentType());

	      
	      // release the resources
	      bos = null;
	      recordControl = null;
	      
	      // reset the recording flag
		  recording = false;
			
	      ctrl.setCapturedObject(mObject);
	  }
	  
	  /**
	   * This method is used to cleanup and release resources on error
	   */
	  public void cleanUp() {
        // reset the recording flag
		recording = false;
		
	    // release resources on error
	    if(recordControl != null) { recordControl = null; }
	    if(capturePlayer != null) { 
	      capturePlayer.close(); 
	      capturePlayer = null; 
	    }
	  }  
}
