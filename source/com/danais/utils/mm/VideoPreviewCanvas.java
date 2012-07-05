//#condition  polish.api.mmapi



package com.danais.utils.mm;

import java.io.ByteArrayInputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

public class VideoPreviewCanvas extends Canvas{
	  
	  private MediaObject multimediaObj;

	public VideoPreviewCanvas(MediaObject multimediaObj) {
	     setTitle("Video Entry Preview");
	     this.multimediaObj=multimediaObj;
	  }
	  
	  public void showCanvas() throws Exception {
	    // and then repaint it
	    repaint(); 
	    // finally playback the recording
	    playbackVideoRecording();
	    
	  }
	  
	  public void paint(Graphics g) {  
	    paint(g);  
	  }
	  
	  private void playbackVideoRecording() throws Exception{
	    ByteArrayInputStream bis = new ByteArrayInputStream(multimediaObj.getMediaData());
	    
	    Player player = null;
	    VideoControl vControl = null;
	    
	    try {
	      
	      // create the Playback player
	      player = Manager.createPlayer(bis, multimediaObj.getContentType());
	      
	      // realize it
	      player.realize();
	      
	      // create the playback video control
	      vControl = (VideoControl)player.getControl("javax.microedition.media.control.VideoControl");
	      
	      // initialize it
	      vControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, this);
	      
	      vControl.setDisplayLocation(5, 5);

	      try {
	        vControl.setDisplaySize(getWidth() - 10, getHeight() - 10);
	      } catch (MediaException me) {} // ignore
	        
	      vControl.setVisible(true); 
	      
	      // start it
	      player.start();
	      
	    } catch(Exception e) {
	      
	      // release this player instance
	      if(player != null) { player.close(); player = null; }
	      throw e;
	    
	      
	    }
	  }
	}
