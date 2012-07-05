package com.danais.mobile;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

public class StartCanvas extends Canvas {

  private MIDlet midlet;
  private Displayable nextGui;
  private Image splash;
  private final Timer timer;
  private boolean initialized;

  
  public StartCanvas(MIDlet midlet, Displayable nextGui) {
    this.midlet=midlet; 
    this.nextGui=nextGui;
    
    try {
      splash = Image.createImage("/splash.png");
    } catch (final IOException e) {
      e.printStackTrace();
    }
    
    timer = new Timer();
    timer.schedule(new TimerTask() {
      public void run() {
        dismiss();
      }
    }, 3000);
  }

  private void dismiss(){
      timer.cancel();
      initialized=true;
      Display.getDisplay(this.midlet).setCurrent(this.nextGui);
  }


  protected void paint(final Graphics g) {
	    if (!initialized) { //la prima volta che si entra nel paint visualizza lo splash
	    	setFullScreenMode(true);
	      //paint splash
	      g.setColor(0xFFFFFFFF);
	      g.setClip(0, 0, getWidth(), getHeight());
	      g.fillRect(0, 0, getWidth(), getHeight());
	      g.drawImage(splash, (getWidth() - splash.getWidth()) / 2,
	          (getHeight() - splash.getHeight()) / 2, Graphics.TOP | Graphics.LEFT);
	      return;
	    } else { 
	    	//in questo ramo non si dovrebbe entrare mai. per sicurezza ho impostato il forward.
	    	Display.getDisplay(this.midlet).setCurrent(this.nextGui);
	    }
  }

	public boolean isInitialized() {
		return initialized;
	}
}