/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */
package com.danais.blog;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.danais.mobile.MainCanvas;
import com.danais.utils.Language;

import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;


public class PreferencesForm extends Form 
    implements CommandListener {

    
    public final static String LABEL_SAVE = "Save";
    //public final static String LABEL_TIMEZONE = "Time Zone";
    public final static String LABEL_CANCEL = "Cancel";

	private MainCanvas mainCanvas=null;
    private Preferences mPrefs;
	
    //#ifdef polish.i18n.useDynamicTranslations
	private int lastSelectedLocaleId;
	private ChoiceGroup languageGroup;
	//#endif
	
	//#if polish.api.mmapi
	private ChoiceGroup audioGroup;
	private ChoiceGroup videoGroup;
	private ChoiceGroup photoGroup;
	//#endif

    public PreferencesForm(MainCanvas aClient, Preferences aPrefs) {
        super("Setup");
        mainCanvas = aClient;
        mPrefs = aPrefs;

        //#style myForm
    	UiAccess.setStyle( this ); 
    	
    	//#style myItem
        TextField textFieldBodySize = new TextField("Max Body Size", Integer.toString(mPrefs.getMaxBodySize()), 10, TextField.NUMERIC);
		append(textFieldBodySize);
		//#style myItem
		TextField textFieldRecentPostCount = new TextField("Recent Post Count", Integer.toString(mPrefs.getRecentPostCount()), 10, TextField.NUMERIC);
		append(textFieldRecentPostCount);
        
		//#ifdef polish.i18n.useDynamicTranslations
        lastSelectedLocaleId=mPrefs.getLocaleIndex();
        //#style horizontalChoice
        languageGroup = new ChoiceGroup( Locale.get("setting.language"), ChoiceGroup.EXCLUSIVE );
		
        String[] languagesNames = Language.getIstance().getLanguagesNames();
        for (int i = 0; i < languagesNames.length; i++) {
        	//#style choiceItem
			languageGroup.append( languagesNames[i], null );	
		}
		
		languageGroup.setSelectedIndex( this.lastSelectedLocaleId, true );	
		this.append( languageGroup );
		//#endif
        
		
		//#if polish.api.mmapi
		addMultimediaOption();		
		//#endif
		
		
        addCommand(new Command(LABEL_SAVE, Command.OK, 1));
        addCommand(new Command(LABEL_CANCEL, Command.CANCEL, 2));
        //addCommand(new Command(LABEL_TIMEZONE, Command.SCREEN, 3));
        setCommandListener(this);
    }
	//#if polish.api.mmapi
	private void addMultimediaOption() {
		//audio config 
		if( System.getProperty("supports.video.capture")!= null &&
				System.getProperty("supports.audio.capture").trim().equalsIgnoreCase("true")){
			String formatiSuportati=System.getProperty("audio.encodings");
			String[] lines=TextUtil.splitAndTrim(formatiSuportati, ' ');
			//#style horizontalChoice
			audioGroup = new ChoiceGroup( Locale.get("setting.audio"), ChoiceGroup.EXCLUSIVE );
			//#style choiceItem
        	audioGroup.append( "default", null ); //aggiungo un item per evidenziare il default
			int selectedIndex=0;
	        for (int i = 0; i < lines.length; i++) {
	        	//#style choiceItem
	        	audioGroup.append( lines[i], null );
	        	if(lines[i].equalsIgnoreCase(mPrefs.getAudioEncoding())){
	        		selectedIndex=i+1; //+1 perchè il cg in zero ha un elemento fittizio. l'array degli elementi trovati parte da zero..
	    	 	}
			}
	        	       
			this.append( audioGroup );
			audioGroup.setSelectedIndex( selectedIndex, true );	
		}
		
		//photo config
		if( System.getProperty("supports.video.capture")!=null
				&& System.getProperty("supports.video.capture").trim().equalsIgnoreCase("true") 
				&& System.getProperty("video.snapshot.encodings")!=null){
			String formatiSuportati=System.getProperty("video.snapshot.encodings");
			String[] lines=TextUtil.splitAndTrim(formatiSuportati, ' ');
			//#style horizontalChoice
			photoGroup = new ChoiceGroup( Locale.get("setting.photo"), ChoiceGroup.EXCLUSIVE );
			//#style choiceItem
			photoGroup.append( "default", null ); //aggiungo un item per evidenziare il default
			int selectedIndex=0;
	        for (int i = 0; i < lines.length; i++) {
	        	//#style choiceItem
	        	photoGroup.append( lines[i], null );	
	        	if(lines[i].equalsIgnoreCase(mPrefs.getPhotoEncoding()))
	        		selectedIndex=i+1;
			}
	        photoGroup.setSelectedIndex( selectedIndex, true );	
			this.append( photoGroup );
		}
		      
		//video config
		if(System.getProperty("supports.video.capture") != null
				&& System.getProperty("supports.video.capture").trim().equalsIgnoreCase("true")
				&& System.getProperty("video.encodings")!=null){
			String formatiSuportati=System.getProperty("video.encodings");
			String[] lines=TextUtil.splitAndTrim(formatiSuportati, ' ');
			//#style horizontalChoice
			videoGroup = new ChoiceGroup( Locale.get("setting.video"), ChoiceGroup.EXCLUSIVE );
			//#style choiceItem
			videoGroup.append( "default", null ); //aggiungo un item per evidenziare il default
			int selectedIndex=0;
	        for (int i = 0; i < lines.length; i++) {
	        	//#style choiceItem
	        	videoGroup.append( lines[i], null );
	        	if(lines[i].equalsIgnoreCase(mPrefs.getVideoEncoding()))
	        		selectedIndex=i+1;
			}
	        videoGroup.setSelectedIndex( selectedIndex, true );	
			this.append( videoGroup );
		}
	}
	//#endif

    private boolean isLanguageChanged(){
    	int selectedIndex = languageGroup.getSelectedIndex();
		if (selectedIndex != this.lastSelectedLocaleId) {
			return true;
		} else
			return false;
    }
    
    public void commandAction(Command aCommand, Displayable aDisplayable) {
        if (aCommand.getLabel() == LABEL_SAVE) {
            int maxBody = Integer.parseInt(((TextField) get(0)).getString());
            int recent = Integer.parseInt(((TextField) get(1)).getString());

            if (maxBody > 256) {
                mPrefs.setMaxBodySize(maxBody);
            }

            if (recent > 1) {
                mPrefs.setRecentPostCount(recent);
            }
            
        	//#if polish.api.mmapi
            if(videoGroup != null){ 
            	if(videoGroup.getSelectedIndex() != 0 ){
            	String enc=videoGroup.getString(videoGroup.getSelectedIndex());
            	mPrefs.setVideoEncoding(enc);
            	} else {
            		mPrefs.setVideoEncoding("");
            	}
            }
            
            if(audioGroup != null){
            	if(audioGroup.getSelectedIndex() != 0 ){
            	String enc=audioGroup.getString(audioGroup.getSelectedIndex());
            	mPrefs.setAudioEncoding(enc);
            	} else {
            		mPrefs.setAudioEncoding("");	
            	}
            }
            
            if(photoGroup != null){
            	if(photoGroup.getSelectedIndex() != 0 ){
            	String enc=photoGroup.getString(photoGroup.getSelectedIndex());
            	mPrefs.setPhotoEncoding(enc);
            	} else {
            		mPrefs.setPhotoEncoding("");
            	}
            }
            //#endif
            
          //#if polish.i18n.useDynamicTranslations
			// read locale settings:
			if (isLanguageChanged()) {
				mPrefs.setLocaleIndex(languageGroup.getSelectedIndex());
			}
		  //#endif
           
		// svolgo l'azione in un thread separato per evitare il blocco della
			// gui sull'emulatore e su alcuni telefoni
			new Thread() {
				public void run() {
					try {
						mPrefs.save();
						if (isLanguageChanged()) {
							mainCanvas.displayMessageBlocking(Locale.get("message.LanguageChanged"));
						}
					} catch (Exception e) {
						mainCanvas.displayErrorBlocking("Failed to save preferences");
					}
					mainCanvas.popCurrent();
				}// fine run
			}.start();
    			
                        
        }  else if (aCommand.getLabel() == LABEL_CANCEL) {    
    		// svolgo l'azione in un thread separato per evitare il blocco della
			// gui sull'emulatore e su alcuni telefoni
			new Thread() {
				public void run() {
					try {
						mPrefs.load();
					} catch (Exception e) {
						mainCanvas.displayErrorBlocking("Failed to restore preferences");
					}
					mainCanvas.popCurrent();
				}// fine run
			}.start();
        
        }
    }
}

