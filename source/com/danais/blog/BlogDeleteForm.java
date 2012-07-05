/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */
package com.danais.blog;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.rms.RecordStoreException;

import com.danais.mobile.MainCanvas;

public class BlogDeleteForm extends Form implements CommandListener{

	private MainCanvas mainCanvas=null;
	private BlogController blogController=null;
	private int selectedBlog = 0; 
	
    public final static String LABEL_YES = "Yes";
    public final static String LABEL_NO = "No";
    public final static String TITLE = "Confirm";
	
	
	public BlogDeleteForm(MainCanvas mainCvn, int aBlogIndex, BlogController blogController) {
	    super("");
		this.mainCanvas=mainCvn;
		this.blogController=blogController;
		this.selectedBlog=aBlogIndex;
		
		String message = "Are you sure you want to delete " + blogController.getBlogNames()[aBlogIndex] + "?";
					
		append(new StringItem(null, message));
		addCommand(new Command(LABEL_NO, Command.BACK, 1));
		addCommand(new Command(LABEL_YES, Command.SCREEN, 2));
		setCommandListener(this);
		mainCanvas.pushCurrent(this);
	}

    public void commandAction(Command cmd, Displayable aDisplayable) {
        
    	mainCanvas.popCurrent();
        
    	if (cmd.getLabel() == ConfirmForm.LABEL_YES) {
            
    		String blogName= blogController.getBlogNames()[selectedBlog];
            //#debug
 			System.out.println("selezionato per la cancellazione: " + blogName);
            
 			try {
 				blogController.removeBlog(blogName);
 			} catch (RecordStoreException e) {
 				mainCanvas.displayError(e, "Error in remove: \n");
 			} catch (IOException e) {
 				mainCanvas.displayError(e, "Error in remove: \n");
 			}
             mainCanvas.displayBlogListUI();
    	}
    }

}
