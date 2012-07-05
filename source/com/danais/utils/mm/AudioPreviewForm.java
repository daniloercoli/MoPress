package com.danais.utils.mm;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;

public class AudioPreviewForm extends Form {

	private TextField description;
	private MediaObject multimediaObj;
	
	public AudioPreviewForm(String title, MediaObject multimediaObj) throws IOException {
		super(title);
		this.multimediaObj=multimediaObj;
		
		append(new StringItem("Listen your recording!",""));	
		Image image;

		image = Image.createImage("/audio.gif");
		append(image);
		
		description=new TextField("Description", multimediaObj.getDescription(), 64, TextField.ANY);
		append(description);	
		
	}
	 
	public String getDescription(){
		return description.getString().trim();
	}
	
	public void playbackAudioRecording()throws Exception {
	    
	    ByteArrayInputStream bis = new ByteArrayInputStream(multimediaObj.getMediaData());
	    
	    Player player = null;
	    
	    try {
	      
	      // create the Playback player
	      player = Manager.createPlayer(bis, multimediaObj.getContentType());
	      
	      // start it
	      player.start();
	      
	    } catch(Exception e) {
	      // release this player instance
	      if(player != null) { player.close(); player = null; }
	      throw e;
	      	}
	  }
	
}
