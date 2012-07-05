/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */
package com.danais.blog;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.danais.mobile.MainCanvas;

import de.enough.polish.ui.UiAccess;

public class TimeZoneList extends List implements CommandListener {

    
    public final static String LABEL_SET = "Save";
    public final static String LABEL_CANCEL = "Cancel";

	private MainCanvas mainCanvas=null;
    private Preferences mPrefs;


    public TimeZoneList(MainCanvas aClient, Preferences aPrefs) {
        super("Select time zone", List.IMPLICIT, SimpleTimeZone.TIME_ZONE_IDS, null);
        //#style myForm
    	UiAccess.setStyle( this ); 

       // TimeZone tz = TimeZone.getDefault();
      //  int selected = SimpleTimeZone.getIndexForOffset(tz.getRawOffset());
        
        int selected = SimpleTimeZone.getIndexForOffset(aPrefs.getTimeZone().getRawOffset());

        mainCanvas = aClient;
        mPrefs = aPrefs;

        setSelectedIndex(selected, true);
        addCommand(new Command(LABEL_SET, Command.OK, 1));
        addCommand(new Command(LABEL_CANCEL, Command.CANCEL, 2));
        setCommandListener(this);
    }

    public void commandAction(Command aCommand, Displayable aDisplayable) {
        if (aCommand.getLabel() == LABEL_SET ||
            aCommand == List.SELECT_COMMAND) {
            mPrefs.setTimeZone(new SimpleTimeZone(getSelectedIndex()));
            
         // svolgo l'azione in un thread separato per evitare il blocco della
			// gui sull'emulatore e su alcuni telefoni
			new Thread() {
				public void run() {
					try {
			            mPrefs.save();
			            mainCanvas.popCurrent();
					} catch (Exception e) {
						mainCanvas.displayErrorBlocking("Failed to save Tz preferences");
					}
					mainCanvas.popCurrent();
				}// fine run
			}.start();
            
            
        } else if (aCommand.getLabel() == LABEL_CANCEL) {
        	mainCanvas.popCurrent();
        }
    }
}