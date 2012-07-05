/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */
package com.danais.utils;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import de.enough.polish.ui.UiAccess;


/**
 * Alert bloccante
 * @author dercoli
 *
 */
public class MessageBox extends Alert implements CommandListener
{
	protected MIDlet midlet;
	
	private boolean isReady = false;

	public MessageBox(String title, String text, AlertType type, MIDlet midlet, Displayable nextDisplay){
		super(title, text, null, type);
		
		this.midlet = midlet;
		
		this.setCommandListener(this);
		this.setTimeout(Alert.FOREVER);
		
			
		// Mostra l'alert
		//FIXME: Display.getDisplay(midlet).setCurrent(this, nextDisplay);
		//questo tipo si soluzione è differente da quella trovata perchè aggiunge nextDisp
		//subito. ed è stato aggiunto per superare i pbm del nokia.
		//dopo waitForDone non dovrebbe essere necessario chiamare la visualizzazione del next...

		//#style generalAlert
		UiAccess.setStyle( this );
		Display.getDisplay(midlet).setCurrent(this);
				
		// Attendi la conferma di chiusura
		waitForDone();
		
		// Visualizza il precedente Display
		Display.getDisplay(midlet).setCurrent(nextDisplay);
	}
	
	private void waitForDone()
	{
		try
		{
			while(!isReady)
			{
				synchronized(this)
				{
					this.wait();
					
				}
			}
		}
		catch(Exception error)
		{
			
		}
	}

	public void commandAction(Command cmd, Displayable dsp)
	{
		if(cmd == Alert.DISMISS_COMMAND)
		{
			isReady = true;
			
			synchronized(this)
			{
				this.notify();
			}			
		}
	}
}