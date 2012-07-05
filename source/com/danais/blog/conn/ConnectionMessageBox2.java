/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */
package com.danais.blog.conn;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import javax.microedition.midlet.MIDlet;

import com.danais.mobile.MainCanvas;

import de.enough.polish.ui.UiAccess;


/**
 * 
 * @author dercoli
 *
 */
public class ConnectionMessageBox2 extends Alert implements CommandListener
{
	protected BlogConn connection;
	protected Command cancelCommand ;
	protected MIDlet midlet;
	protected Displayable next;
	
	public ConnectionMessageBox2(String title, String text, AlertType type, BlogConn conn, MIDlet midlet, Displayable nextDisplay){
		super(title, text, null, type);
		this.connection=conn;
		this.midlet = midlet;
		this.next= nextDisplay;
		this.setTimeout(Alert.FOREVER);
		this.setCommandListener(this);
		
		try{
			Gauge indicator = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
			this.setIndicator(indicator);
		} catch (Exception e) {
		}
		
		cancelCommand = new Command(MainCanvas.LABEL_CANCEL, Command.CANCEL, 1);
		addCommand(cancelCommand);
		
		//FIXME:Display.getDisplay(midlet).setCurrent(this,next);
		
		//#style generalAlert
		UiAccess.setStyle( this );
		Display.getDisplay(midlet).setCurrent(this); //next è stato aggiunto per superare i pbm del nokia. anche se non serviva.
	}
	
	public void commandAction(Command cmd, Displayable dsp){
		if(cmd == cancelCommand){	
			connection.stopConnWork();
			// Visualizza il precedente Display
			Display.getDisplay(midlet).setCurrent(next);
		}
	}
}