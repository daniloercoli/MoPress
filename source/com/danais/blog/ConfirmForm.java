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
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import com.danais.mobile.MainCanvas;

import de.enough.polish.ui.UiAccess;


public class ConfirmForm extends Form implements CommandListener {


    private MainCanvas mClient;
    private CommandListener mListener;

    public final static String LABEL_YES = "Yes";
    public final static String LABEL_NO = "No";
    public final static String TITLE = "Confirm";

    
    public ConfirmForm(MainCanvas aClient,
                       CommandListener aListener,
                       String aMessage) {
        super(TITLE);
        mClient = aClient;
        mListener = aListener;

        //#style generalAlert
		UiAccess.setStyle( this );
        
        append(new StringItem(null, aMessage));
        addCommand(new Command(LABEL_NO, Command.BACK, 1));
        addCommand(new Command(LABEL_YES, Command.SCREEN, 2));
        setCommandListener(this);
        mClient.pushCurrent(this);
    }

    public void commandAction(Command aCommand, Displayable aDisplayable) {
        mClient.popCurrent();
        mListener.commandAction(aCommand, this);
    }

}

