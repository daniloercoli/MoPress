/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */

package com.danais.mobile;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.StringItem;

import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;


public class AboutForm extends Form implements CommandListener {


    private MainCanvas mClient;

    private final static String LICENSE_INFO = " comes with ABSOLUTELY NO WARRANTY. This is free software, and you are welcome to redistribute it under certain conditions.\n\nContains code licensed under the General Public License 2.0.\n\n";

    public AboutForm(MainCanvas aClient) {
        super("About MoPress");
    	//#style myForm
    	UiAccess.setStyle( this ); 
        mClient = aClient;
        Image logo = null;
        String name = mClient.getAppProperty("MIDlet-Name");
        String version = mClient.getAppProperty("MIDlet-Version");
        String url = mClient.getAppProperty("MIDlet-Info-URL");

        try {
            logo = Image.createImage(mClient.getAppProperty("MIDlet-Icon"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        append(new ImageItem(null,logo,ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_NEWLINE_BEFORE
                             | ImageItem.LAYOUT_NEWLINE_AFTER, null));
        append(new StringItem(null, name + " " + version + "\n\n" + url));
        append(new StringItem(null, "Copyright (C) 2009 Danais s.r.l."));
        append(new StringItem(null, "\n" + name + LICENSE_INFO));
        append(new StringItem(null, "See the " + name + " website for more information."));
        
        addCommand(new Command(Locale.get("cmd.Back"), Command.BACK, 1));
        addCommand(new Command(Locale.get("cmd.MemFree"), Command.ITEM, 2));
        setCommandListener(this);
    }

    public void commandAction(Command aCommand, Displayable aDisplayable) {
        if (aCommand.getLabel() == Locale.get("cmd.Back")) {
            mClient.popCurrent();
        } else {
        	 Runtime rt = java.lang.Runtime.getRuntime();
        	 long totalMem=rt.totalMemory();
        	 long freeMem=rt.freeMemory();
        	 
        	 float a = (float)(totalMem);
        	 float b = 1048576f;
        	 String totalMemMB=Float.toString(a/b);
        	 
        	 a = (float)(freeMem);
        	 String totalFreeMemMB=Float.toString(a/b);
        	 
             mClient.displayMessage("Total MB heap: " + totalMemMB + "\nTotal MB free: " + totalFreeMemMB);
        }
    }
}


