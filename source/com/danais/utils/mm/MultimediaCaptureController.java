//#condition  polish.api.mmapi
/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */

package com.danais.utils.mm;


import java.util.Hashtable;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import com.danais.blog.PostTextBox;
import com.danais.blog.conn.BlogConnResponse;
import com.danais.blog.conn.NewMediaObjectConn;
import com.danais.blog.model.Post;
import com.danais.mobile.MainCanvas;
import com.danais.utils.observer.Observable;
import com.danais.utils.observer.Observer;

import de.enough.polish.ui.UiAccess;

public class MultimediaCaptureController implements CommandListener, Observer {
	
	public static final int PHOTO=1;
	public static final int VIDEO=2;
	public static final int AUDIO=3;
	
	private MainCanvas mainCanvas=null;
    private Post post; //devo mantere il link al blog
    private PostTextBox insertBox=null; //l'elemento per inserire la url

    private Command cmdTakeScreenshot = new Command("Take Screenshot", Command.OK, 1 );
    private Command cmdStartRec = new Command("Start Recording", Command.OK, 2 );
    private Command cmdStopRec = new Command("Stop Recording", Command.OK, 3 );
	private Command cmdQuit = new Command("Quit", Command.EXIT, 4);
	
	private Command cmdPreviewOK = new Command("OK", Command.OK, 1);
	private Command cmdPreviewDiscard = new Command("Discard", Command.EXIT, 2);
	
	
	
	private MediaObject mmObject=null; //tieni un rifierimento all'ultimo obj catturato
	private int multiMediaCaptureType=-1; //see type defined above
	private CaptureVideoAndPhotoCanvas captureCanvas=null;
	private PhotoPreviewForm formPw=null;
	private VideoPreviewCanvas prevCanvas=null;
	private AudioCaptureForm audioCaptureForm=null;
	private AudioPreviewForm audioPrevForm=null;
	
	public MultimediaCaptureController(MainCanvas mainCanvas, Post mPost, PostTextBox insertBox, int type) throws Exception{
		this.mainCanvas = mainCanvas;
		this.post = mPost;
		this.insertBox=insertBox;
		multiMediaCaptureType=type;
		multimediaCaptureStart(type); //avvio il processo di cattura
	}


	/**
	 * @param e
	 * @param message
	 */
	public void displayError(final Exception e, String message) {
		//mainCanvas.displayError(e, message);
		//non ho potuto utilizzare il modo standard perchè j2me polish si bloccava
		
		//#debug error
		System.out.println(message);
		Alert errorAlert = new Alert("MoPress Error", message + "\n" + e.getMessage(), null, AlertType.ERROR);
		errorAlert.setTimeout(Alert.FOREVER);
		mainCanvas.show(errorAlert);
	}

	
	private void multimediaCaptureStart(int type) throws Exception {
		mmObject=null; //resetto l'oggetto media
		if(type == PHOTO){
			//#debug
			System.out.println("Photo enc: "+mainCanvas.getPreferences().getPhotoEncoding());
			captureCanvas= new CaptureVideoAndPhotoCanvas(this,type,mainCanvas.getPreferences().getPhotoEncoding());
			captureCanvas.addCommand(cmdQuit);
			captureCanvas.addCommand(cmdTakeScreenshot);
			captureCanvas.setCommandListener(this);
			mainCanvas.pushCurrent(captureCanvas);	
		} else if(type == VIDEO){
			String encoding=mainCanvas.getPreferences().getVideoEncoding();
			captureCanvas= new CaptureVideoAndPhotoCanvas(this,type,encoding);
			captureCanvas.addCommand(cmdQuit);
			//captureCanvas.addCommand(cmdTakeScreenshot);
			captureCanvas.addCommand(cmdStartRec);
			captureCanvas.addCommand(cmdStopRec);
			captureCanvas.setCommandListener(this);
			mainCanvas.pushCurrent(captureCanvas);	
		} else if(type == AUDIO){
			//#debug
			System.out.println("Codifica audio selezionata: "+mainCanvas.getPreferences().getAudioEncoding());
			String encoding=mainCanvas.getPreferences().getAudioEncoding();
			audioCaptureForm= new AudioCaptureForm(this,encoding);
			audioCaptureForm.addCommand(cmdQuit);
			audioCaptureForm.addCommand(cmdStartRec);
			audioCaptureForm.addCommand(cmdStopRec);
			audioCaptureForm.setCommandListener(this);
			mainCanvas.pushCurrent(audioCaptureForm);
		}
	}
	
	/*
	 * metodo di call-back
	 */
	public void setCapturedObject(MediaObject mObject) {
		
		mainCanvas.popCurrent(); // toglie la canvas dallo schermo
		mmObject = mObject;
		captureCanvas = null;
		
		if (multiMediaCaptureType == PHOTO) {
			// mostra la preview
			try {
				formPw = new PhotoPreviewForm("Preview", mObject);
				formPw.addCommand(cmdPreviewOK);
				formPw.addCommand(cmdPreviewDiscard);
				formPw.setCommandListener(this);
				mainCanvas.pushCurrent(formPw);
			} catch (Exception e) {
				displayError(e, "Preview Init Error");
			}
		} else if (multiMediaCaptureType == VIDEO) {
			prevCanvas = new VideoPreviewCanvas(mObject);
			prevCanvas.addCommand(cmdPreviewOK);
			prevCanvas.addCommand(cmdPreviewDiscard);
			prevCanvas.setCommandListener(this);
			try {
				prevCanvas.showCanvas();
				mainCanvas.pushCurrent(prevCanvas);
			} catch (Exception e) {
				displayError(e, "Preview Init Error");
			}
		} else if (multiMediaCaptureType == AUDIO) {
			try {
			audioPrevForm = new AudioPreviewForm("Audio preview", mObject);
			audioPrevForm.addCommand(cmdPreviewOK);
			audioPrevForm.addCommand(cmdPreviewDiscard);
			audioPrevForm.setCommandListener(this);
			mainCanvas.pushCurrent(audioPrevForm);
			
				audioPrevForm.playbackAudioRecording();
			} catch (Exception e) {
				this.displayError(e, "Errore di preview audio");
			}
		}//fine else
	} 
	
		
	public void commandAction(Command cmd, Displayable dis) {
		
		if (cmd == this.cmdQuit ) {
	        mmObject=null;
	        if(captureCanvas!=null) captureCanvas.stopCamera();
	        captureCanvas=null;
	        if(audioCaptureForm!=null) audioCaptureForm.cleanUp();
	        audioCaptureForm=null;
			mainCanvas.popCurrent();
		} else if (cmd == this.cmdTakeScreenshot) { //fai una foto
			captureCanvas.getPhoto();
		}else if( cmd == cmdPreviewOK){
			
			if (multiMediaCaptureType == PHOTO) {
				mmObject.setDescription(formPw.getDescription()); //imposto la descrizione
				formPw=null;
			} else if (multiMediaCaptureType == VIDEO) {
				prevCanvas=null;
			} else if (multiMediaCaptureType == AUDIO) {
				mmObject.setDescription(audioPrevForm.getDescription()); //imposto la descrizione
			}
			
	         String filename=post.getBlog().getUsername()+System.currentTimeMillis()+"."+mmObject.guessFileExtension();
	         NewMediaObjectConn connection = new NewMediaObjectConn (post.getBlog().getBlogXmlRpcUrl(), 
	         		   post.getBlog().getUsername(),post.getBlog().getPassword(),null, post.getBlog().getBlogId(), 
	         		   filename,mmObject.getMediaData() );
	         connection.addObserver(this);
	         mainCanvas.displayConnectionInProgress("Invio dati in corso...",connection);
	         connection.startConnWork(); //esegue il lavoro della connessione
	         
		} else if( cmd == cmdPreviewDiscard) {
			mainCanvas.popCurrent(); //toglie la form di preview
			try {
				multimediaCaptureStart(multiMediaCaptureType);
			} catch (Exception e) {
				mainCanvas.displayError(e, "Restart MM Error");
			}
		} else if(cmd == cmdStartRec){ //inizia la registran!!
			if (multiMediaCaptureType == AUDIO) {
				//UiAccess.setAccessible( audioCaptureForm, cmdStartRec, false );
				//UiAccess.setAccessible( audioCaptureForm, cmdStopRec, true );
				audioCaptureForm.startAudioRecording();
			} else if(multiMediaCaptureType == VIDEO) {
				captureCanvas.startRecording();
			}
		} else if(cmd == cmdStopRec){
			//UiAccess.setAccessible( audioCaptureForm, cmdStartRec, true );
			//UiAccess.setAccessible( audioCaptureForm, cmdStopRec, false );
			if (multiMediaCaptureType == AUDIO) {
				audioCaptureForm.stopRecording();
			} else if(multiMediaCaptureType == VIDEO) {
				captureCanvas.stopRecording();
			}
		}
	}//end command action

	public void update(Observable observable, Object object) {
		BlogConnResponse resp=(BlogConnResponse)object;
		if(!resp.isError()) {
			mainCanvas.popCurrent();
			Hashtable content =(Hashtable)resp.getResponseObject();
			
			//#debug
			System.out.println("url del file remoto: "+content.get("url") );
			//#debug
			System.out.println("nome file remoto: "+content.get("file") );	
			
			String desc=(String)content.get("file");
			if(mmObject!=null && !mmObject.getDescription().equals(""))
				desc=mmObject.getDescription();
			insertBox.insertImage((String)content.get("url"),desc );
			
		} else { //errore della connessione http sottostante  
			mainCanvas.displayErrorBlocking(resp.getResponse());	  	
		}				
	}
}
