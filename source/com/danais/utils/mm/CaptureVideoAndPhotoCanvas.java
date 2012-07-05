//#condition  polish.api.mmapi

/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */
package com.danais.utils.mm;

import java.io.ByteArrayOutputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;
import javax.microedition.media.control.VideoControl;

// the canvas that holds the video
class CaptureVideoAndPhotoCanvas extends Canvas {

	// the video control
	private VideoControl videoControl = null;
	private MultimediaCaptureController captureController = null;
	private Player capturePlayer = null;
	// the record control

	private RecordControl rControl = null;
	// the output stream
	ByteArrayOutputStream bos = null;

	// flag to keep track of when recording is started
	private boolean recording = false;

	//the choosed encoding
	private String encoding=null;
	
	//the type: video or audio
	private int type=-1;
	
	public CaptureVideoAndPhotoCanvas(MultimediaCaptureController ctrl, int type, String encoding) throws Exception {

		this.captureController = ctrl;
		this.encoding =encoding;
		this.type=type;
		
		if(!encoding.trim().equals("")){
			capturePlayer = Manager.createPlayer("capture://video"+"?"+encoding);
		} else {
			capturePlayer = Manager.createPlayer("capture://video");
		}
		
		
		capturePlayer.realize();
		// initialize the video control
		videoControl = (VideoControl) capturePlayer.getControl("javax.microedition.media.control.VideoControl");
		// if not present, throw error
		if (videoControl == null)
			throw new Exception("No Video Control for capturing!");

		// init display mode to use direct video and this canvas
		videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, this);

		try { // try and set to full screen
			videoControl.setDisplayFullScreen(true);
		} catch (MediaException me) {
			// but some devices may not support full screen mode
			videoControl.setDisplayLocation(5, 5);
			videoControl.setDisplaySize(getWidth() - 10, getHeight() - 10);
			repaint();
		}
		// and make the video control visible
		videoControl.setVisible(true);
		
		capturePlayer.start(); //inizia a funzionare il player
	}
	
	/**
	 * Ferma le componenti multimediali
	 */
	protected void stopCamera(){
		if(videoControl!=null) 	videoControl.setVisible(false);
		capturePlayer.close();
		capturePlayer = null;
        videoControl = null;
        rControl=null;
        repaint();
	}
	
	
	public void getPhoto(){
	    
	    // if the camera is recording at the moment, then just return
	    // unless the key code is DOWN to stop recording
	    if(recording) return;
	    	    
	    // if is camera recording mode 
	    if(type == MultimediaCaptureController.VIDEO) return;
	    
		// svolgo l'azione in un thread separato per evitare il blocco della
		// gui sull'emulatore e su alcuni telefoni
		new Thread() {
			public void run() {
				try {
					MediaObject mObject = new MediaObject();
					byte[] imageArray =null;
					
					//FIXME: trovare un modo migliore
					
					if( encoding.indexOf("bmp")!=-1){
						mObject.setContentType("image/bmp");
					}else if( encoding.indexOf("gif")!=-1){
						mObject.setContentType("image/gif");
					}else if( encoding.indexOf("png")!=-1){
					// use the control to take the picture, using the default encoding					
						mObject.setContentType("image/png");
					} else {						
						mObject.setContentType("image/jpg");
						encoding="encoding=jpeg";
					}
										
					//#debug
					System.out.println("Captured photo with encoding= "+encoding);
					imageArray = videoControl.getSnapshot(encoding);
					
					mObject.setMediaData(imageArray);
					stopCamera();
					captureController.setCapturedObject(mObject);
				} catch (Exception e) {
					captureController.displayError(e, "Photo Camera Error!");
				}
			}// fine run
		}.start();
	}
	
	public void startRecording(){
		 // start recording
		  // if the camera is recording at the moment, then just return
	    // unless the key code is DOWN to stop recording
	    if(recording) return;
	    		
		// svolgo l'azione in un thread separato per evitare il blocco della
		// gui sull'emulatore e su alcuni telefoni
		new Thread() {
			public void run() {
				try {
      
			        // grab a record control
			        rControl = (RecordControl)capturePlayer.getControl("javax.microedition.media.control.RecordControl");
			        
			        // if not found, throw exception
			        if(rControl == null) throw new Exception("No RecordControl found!");
			        
			        // create a ByteArrayOutputStream to store this recorded data in
			        bos = new ByteArrayOutputStream(); 
			        
			        // set up the stream
			        rControl.setRecordStream(bos);
			        
			        // and start recording - no need to start the underlying player
			        // as it is already started
			        rControl.startRecord();
			        
			        // set flag
			        recording = true;
			      } catch(Exception e) {
			    	  captureController.displayError(e, "Recording Error!");
			      }
			      
			}// fine run
		}.start();
	}
	
	
	public void stopRecording() {
		try {

			rControl.stopRecord();
			rControl.commit();
			MediaObject mObject = new MediaObject();
			
			// set the media data on this entry
			mObject.setMediaData(bos.toByteArray());
			// set the media content type
			mObject.setContentType(rControl.getContentType());
			// release the resources
			bos = null;
		
			//#debug 
			System.out.println("Captured video with encoding= "+encoding+" and content type= "+rControl.getContentType());
			stopCamera();		
			captureController.setCapturedObject(mObject);
			
			// reset the recording flag
			recording = false;
		} catch (Exception e) {
			captureController.displayError(e, "Recording Error!");
		}
	}
	
	
	public void keyPressed(int keyCode) {
		// see what game action key the user has pressed
		int key = getGameAction(keyCode);

		// if the camera is recording at the moment, then just return
		// unless the key code is DOWN to stop recording
		if (recording && (key != DOWN))
			return;

		// if fire, take a snapshot
		if (key == FIRE) {
			getPhoto();
		} else if (key == UP) {
			startRecording();
		} else if (key == DOWN) {
			stopRecording();
		}
	}

	public void paint(Graphics g) {
		// clear background
		g.setColor(0xffffff);
		g.fillRect(0, 0, getWidth(), getHeight());
		// and draw a rectangle with a different color
		g.setColor(0x44ff66);
		g.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
	}
}