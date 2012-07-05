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
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;

import com.danais.blog.conn.BlogAuthConn;
import com.danais.blog.conn.BlogConnResponse;
import com.danais.blog.model.Blog;
import com.danais.mobile.MainCanvas;
import com.danais.utils.observer.Observable;
import com.danais.utils.observer.Observer;
import com.danais.utils.StringUtils;

import de.enough.polish.ui.UiAccess;

/**
 * Form per aggiugere nuovi "provider"
 * @author dercoli
 *
 */
public class LoginDetailsForm extends Form implements CommandListener, Observer {


    private MainCanvas mainCanvas;

    public final static String LABEL_YES = "Yes";
    public final static String LABEL_NO = "No";
    public final static String TITLE = "Confirm";

	private Command okCommand = new Command(MainCanvas.LABEL_OKAY, Command.SCREEN, 1);
	private Command cancelCommand = new Command(MainCanvas.LABEL_CANCEL, Command.BACK, 2);
	
	private Blog blog = null;
    private BlogController blogController;
	
    public LoginDetailsForm(MainCanvas maincnv, String uri, String user, String password, Blog aBlog, BlogController aBlogC) {
        super("Add Blogs");
        
    	blog = aBlog;
    	blogController = aBlogC;
    	
        //#style myForm
    	UiAccess.setStyle( this ); 

    	mainCanvas = maincnv;
        setCommandListener(this);
        String address = (uri == null) ? "http://" : uri;
		//#style myItem
        TextField textField = new TextField("Blogs address", address,1024, TextField.URL);
		append(textField);
		//#style myItem
		TextField textField2 = new TextField("Username", user, 100, TextField.ANY);
		append(textField2);
		//#style myItem
		TextField textField3 = new TextField("Password", password, 100, TextField.PASSWORD| TextField.SENSITIVE);
		append(textField3);
        addCommand(okCommand);
        addCommand(cancelCommand);    
    }

    public void commandAction(Command aCommand, Displayable aDisplayable) {
        if (aCommand == okCommand) {
            Form form = (Form) aDisplayable;
            String uri = ((TextField) form.get(0)).getString();
            
            uri = StringUtils.checkURL(uri);
            
            String username = ((TextField) form.get(1)).getString();
            String password = ((TextField) form.get(2)).getString();

            if (password != null && password.length() == 0) {
                password = null;
                // FIXME lapassword opzionale esiste ancora??
                mainCanvas.displayError("Please enter a password");
                return;
            }

            if (uri != null && username != null &&
                uri.length() > 0 && username.length() > 0 && blog == null) {
                Preferences prefs = mainCanvas.getPreferences();
                BlogAuthConn connection = new BlogAuthConn (uri,username,password,prefs.getTimeZone());
                connection.addObserver(this);
                mainCanvas.displayConnectionInProgress("Loading Blogs...",connection);
                connection.startConnWork(); //esegue il lavoro della connessione
            } 
            
            else if (uri != null && username != null &&
                    uri.length() > 0 && username.length() > 0 && blog != null) {
            	blog.setBlogXmlRpcUrl(uri);
            	blog.setUsername(username);
            	blog.setPassword(password);
            	try {
					blogController.saveBlog(blog);
				} catch (RecordStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mainCanvas.popCurrent();
            	
            }
            
            else {
            	mainCanvas.displayError("Please enter an address and username");
            }
        } else if(aCommand == cancelCommand){
        	mainCanvas.popCurrent();
        }
    }
    
    
	public void update(Observable observable, Object object) {
		BlogConnResponse resp=(BlogConnResponse)object;
		if(!resp.isError()) {
			mainCanvas.popCurrent();
			if(resp.isStopped()){
				return;
			}
			//TODO scrivere un metodo per il refresh della main canvas
			mainCanvas.addNewBlogs((Blog[])resp.getResponseObject());
		} else {
			mainCanvas.displayErrorBlocking(resp.getResponse());	  	
		}		
	}
}