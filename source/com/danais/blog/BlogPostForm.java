/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */
package com.danais.blog;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;
	
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;

import com.danais.blog.conn.BlogConnResponse;
import com.danais.blog.conn.EditPostConn;
import com.danais.blog.conn.NewPostConn;
import com.danais.blog.model.Blog;
import com.danais.blog.model.Category;
import com.danais.blog.model.Post;
import com.danais.blog.model.PostState;
import com.danais.mobile.MainCanvas;
import com.danais.utils.CalendarUtils;


//#if  polish.api.mmapi
import com.danais.utils.mm.MultimediaCaptureController;
//#endif

import com.danais.utils.observer.Observable;
import com.danais.utils.observer.Observer;

import de.enough.polish.ui.TabbedForm;
import de.enough.polish.ui.TabbedFormListener;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;


public class BlogPostForm extends TabbedForm implements CommandListener, Observer, TabbedFormListener {

    private Post mPost;
    private PostState mState = null;
    private int mType;
    private int mDraftId = -1;
    
    private BlogController blogController;
	private MainCanvas mainCanvas=null;

    public final static String LABEL_SUBMIT = "Submit";
    public final static String LABEL_SAVE_LOCAL = "Save Local";
    public final static String LABEL_TITLE = "Title";
    public final static String LABEL_CATEGORY = "Category";
    public final static String LABEL_AUTHORED = "Authored On";
    public final static String LABEL_OPTIONS = "Options";

   // public final static String CONFIRM_MESSAGE ="Could not save draft post, do you want to discard your changes?";
    public final static String CONFIRM_MESSAGE ="Are you sure?";
    public final static String ERROR_MESSAGE ="Could not save draft post";

    
	private	Command htmlMarkupCommand = new Command("Html tag", Command.SCREEN, 1);
	private	Command mediaCommand = new Command("Media", Command.SCREEN, 2);
	
    private static String title= Locale.get("post.form.title");
    private static String[] tabsName = new String[] {
    		Locale.get("post.tabs.summary"),
    		Locale.get("post.tabs.body"),
    		Locale.get("post.tabs.extended"),
    		Locale.get("post.tabs.excerpt")
    		};
    
    private static String[] CHOICES_OPTIONS =
        new String[] { "Publish",
                       "Convert linebreaks",
                       "Allow comments",
                       "Allow trackbacks"
        };


    /**
     * private costructor for basic init operations
     */
    private BlogPostForm(BlogController aClient, MainCanvas mainCvn){
    	super(title,tabsName,null);
    	//#style tabbedForm
    	UiAccess.setStyle( this );  
        this.blogController = aClient;
		this.mainCanvas=mainCvn;
    }
    
    public BlogPostForm(Blog aBlog, BlogController aClient, MainCanvas mainCvn) {
    	this(aClient, mainCvn);
        mPost = new Post(aBlog);
        mState = new PostState();
        mType = BlogController.TYPE_NEW;
        buildUI();
    }

    /**
     * I draft sono i messaggi locali del telefonino.
     * @param aPost
     * @param aClient
     * @param aDraftId
     * @param mainCvn
     */
    public BlogPostForm(Post aPost, BlogController aClient, int aDraftId, MainCanvas mainCvn) {
    	this(aClient, mainCvn);
        mPost = aPost;
        mState = new PostState();
        mType = BlogController.TYPE_DRAFT;
        mDraftId = aDraftId;
        buildUI();
    }

    public BlogPostForm(Post aPost, BlogController aClient, MainCanvas mainCvn) {
    	this(aClient, mainCvn);
        mPost = aPost;
        mType = BlogController.TYPE_RECENT;
        mState = new PostState();
        buildUI();
    }

    private void buildUI(){
    	    	
        append(0,new TextField(LABEL_TITLE, mPost.getTitle(), 64, TextField.ANY));
        Category[] availableCategories = mPost.getBlog().getCategories();
        String[] categoryLabels;
        int primaryIndex = -1;
        if (availableCategories != null) {
            Category primaryCategory = mPost.getPrimaryCategory();
            categoryLabels = new String[availableCategories.length];
            for (int i = 0; i < availableCategories.length; i++) {
                categoryLabels[i] = availableCategories[i].getLabel();
                if (availableCategories[i].equals(primaryCategory)) {
                    primaryIndex = i;
                }
            }
            ChoiceGroup categories = new ChoiceGroup(LABEL_CATEGORY, ChoiceGroup.EXCLUSIVE, categoryLabels, null);
            if (primaryIndex != -1) {
                categories.setSelectedIndex(primaryIndex, true);
            }
            append(0,categories);
        } else {
            append(0,new StringItem(null, "No post categories"));
        }

        //#debug
        //System.out.println("Creato il campo data con il seguente timezone: "+mainCanvas.getPreferences().getTimeZone().getRawOffset());
        
       
//FIXME
        Date dateCreated = (Date) mPost.getAuthoredOn_GMT();
        long timeToAdd;
        Date dateCreatedTZ = null;

        if (dateCreated != null){
        	timeToAdd = dateCreated.getTime();
        	timeToAdd = CalendarUtils.adjustTimeToDefaultTimezone(timeToAdd);
        	dateCreatedTZ = new Date(timeToAdd);
        }

        DateField authored = new DateField(LABEL_AUTHORED, DateField.DATE_TIME);
        authored.setDate((dateCreatedTZ == null) ? new Date() : dateCreatedTZ);
        append(0,authored);
      

        

        ChoiceGroup options = new ChoiceGroup(LABEL_OPTIONS, ChoiceGroup.MULTIPLE, CHOICES_OPTIONS, null);
        options.setSelectedIndex(0, mState.isPublished());
        options.setSelectedIndex(1, mPost.isConvertLinebreaksEnabled());
        options.setSelectedIndex(2, mPost.isCommentsEnabled());
        options.setSelectedIndex(3, mPost.isTrackbackEnabled());
        append(0,options);
        
        addCommand(new Command(LABEL_SUBMIT, Command.SCREEN, 4));
        addCommand(new Command(LABEL_SAVE_LOCAL, Command.SCREEN, 5));
        addCommand(new Command(Locale.get("cmd.Back"), Command.BACK, 6));
        setCommandListener(this);
        setTabbedFormListener(this); //ascolta i cambimenti di tab
        setItemStateListener(mState);
                
        append(1,new PostTextBox(mPost, PostTextBox.TYPE_BODY,4096));
        append(2,new PostTextBox(mPost, PostTextBox.TYPE_EXTENDED,4096));
        append(3,new PostTextBox(mPost, PostTextBox.TYPE_EXCERPT,4096));

    }

    private void saveTabZero()  throws IndexOutOfBoundsException{
        int count = 0;

        TextField title = (TextField) get(0,0); count++;
        ChoiceGroup primaryCategory; count++;
        DateField authoredOn = (DateField) get(0,2); count++;
        ChoiceGroup options = (ChoiceGroup) get(0,3); count++;
        Category[] categories = mPost.getBlog().getCategories(); count++;
        
        mPost.setTitle(title.getString()); count++;
        if (categories != null) {
            primaryCategory = (ChoiceGroup) get(0,1); count++;
            mPost.setPrimaryCategory
                (categories[primaryCategory.getSelectedIndex()]); count++;
        }
        
        //FIXME
        Date dateCreatedMyTz = authoredOn.getDate();
        long timeToSub;
        Date dateCreatedGMT;
        timeToSub = dateCreatedMyTz.getTime();
    	timeToSub = CalendarUtils.adjustTimeFromDefaultTimezone(timeToSub);
    	dateCreatedGMT = new Date(timeToSub);
    	
        mPost.setAuthoredOn_GMT(dateCreatedGMT); count++;        
    	
        mPost.setConvertLinebreaksEnabled(options.isSelected(1)); count++;
        mPost.setCommentsEnabled(options.isSelected(2)); count++;
        mPost.setTrackbackEnabled(options.isSelected(3)); count++;
        
        mState.setPublished(options.isSelected(0)); count++;
    }

    public void commandAction(Command aCommand, Displayable aDisplayable) {
    	//#debug
		System.out.println("Called commandAction" );
    	
    	if (aCommand.getLabel() == LABEL_SUBMIT) {
           
    		try {
				saveTab(this.getActiveTab());
    		} catch (Exception e) {
    			mainCanvas.displayError(e,"Post not submited");
    			return;
    		}
    		
			switch (mType) {
            
            case BlogController.TYPE_NEW:
            	  if (!mState.isModified()) {
                      goBack(); // XXX prompt user instead
                      break;
                  } else {
                	  
	        	  NewPostConn connection = new NewPostConn (mPost.getBlog().getBlogXmlRpcUrl(), 
	                		mPost.getBlog().getUsername(),mPost.getBlog().getPassword(),null, mPost, mState.isPublished());
	                connection.addObserver(this);
	                mainCanvas.displayConnectionInProgress("Invio dati in corso...",connection);
	                connection.startConnWork(); //esegue il lavoro della connessione
	                break;
                  }
            	  
            case BlogController.TYPE_RECENT:
          	  if (!mState.isModified()) {
                  goBack(); // XXX prompt user instead
                  break;
              } else {
            	  
            	 EditPostConn connection = new EditPostConn (mPost.getBlog().getBlogXmlRpcUrl(), 
                		mPost.getBlog().getUsername(),mPost.getBlog().getPassword(),null, mPost, mState.isPublished());
                connection.addObserver(this);
        		mainCanvas.displayConnectionInProgress("Invio dati in corso...",connection);
                connection.startConnWork(); //esegue il lavoro della connessione
                break;
              }
            	  
            case BlogController.TYPE_DRAFT:
                if (mPost.getId() == null) {
               	 NewPostConn connection = new NewPostConn (mPost.getBlog().getBlogXmlRpcUrl(), 
                  		mPost.getBlog().getUsername(),mPost.getBlog().getPassword(),null, mPost, mState.isPublished());
                  connection.addObserver(this);
                  mainCanvas.displayConnectionInProgress("Invio dati in corso...",connection);
                  connection.startConnWork(); //esegue il lavoro della connessione
                    
                } else {
               	 EditPostConn connection = new EditPostConn (mPost.getBlog().getBlogXmlRpcUrl(), 
                   		mPost.getBlog().getUsername(),mPost.getBlog().getPassword(),null, mPost, mState.isPublished());
                   connection.addObserver(this);
                   mainCanvas.displayConnectionInProgress("Invio dati in corso...",connection);
                   connection.startConnWork(); //esegue il lavoro della connessione
                }
            	                          	
                break;

            default:
                throw new RuntimeException("unknown state");
            }
        } else if (aCommand.getLabel() == Locale.get("cmd.Back")) {//ramo relativo al comando indietro
        	
    		try {
				saveTab(this.getActiveTab());
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (mState.isModified()) {
               	new ConfirmForm(mainCanvas, this, CONFIRM_MESSAGE);
            } else {
                goBack();
            }
        } else if (aCommand.getLabel() == ConfirmForm.LABEL_YES) { //ramo relativo alla conferma
            goBack();
        } else if (aCommand.getLabel() == LABEL_SAVE_LOCAL) { //ramo relativo al salvataggio in locale
        	try {
				saveTab(this.getActiveTab());
    		} catch (Exception e) {
    			mainCanvas.displayError(e,ERROR_MESSAGE);
    			return;
    		}
			if (mState.isModified()) {
                try {
                    blogController.saveDraftPost(mPost, mDraftId);
                } catch (Exception e) {
                    mainCanvas.displayError(e,ERROR_MESSAGE);
                }
            } else {
            	 mainCanvas.displayMessage("Nothing changed!");
            }
        }else if(aCommand == PostTextBox.newPhotoCommand || aCommand == PostTextBox.newVideoCommand
        		|| aCommand == PostTextBox.newAudioCommand){ //ramo relativo alla fotocamera
        	//#if polish.api.mmapi
        	try {
				int activeTab=this.getActiveTab();
				PostTextBox itemContenitoreTesto=(PostTextBox)this.get(activeTab,0);
				//passo il controllo al controller dei media
				int tipo=-1;
				if(aCommand == PostTextBox.newPhotoCommand){
					tipo=MultimediaCaptureController.PHOTO;
				} else if(aCommand == PostTextBox.newVideoCommand){
					tipo=MultimediaCaptureController.VIDEO;
				} else if(aCommand == PostTextBox.newAudioCommand){
					tipo=MultimediaCaptureController.AUDIO;
				}
				MultimediaCaptureController screen = new MultimediaCaptureController(mainCanvas, mPost,
						itemContenitoreTesto, tipo);
				
			} catch (Exception e) {
				mainCanvas.displayError(e,"Error");
			}
        	//#endif   
		} else { //comandi dell'item che stiamo scrivendo
        	int tabIdx=this.getActiveTab();
        	PostTextBox itemContenitoreTesto=(PostTextBox)this.get(tabIdx,0);	
        	itemContenitoreTesto.insertHtmlMarkup(aCommand);
        }
    }


    private void goBack() {
        PostList postlist;

        switch (mType) {
        case BlogController.TYPE_NEW:
        	mainCanvas.popCurrent();
            break;

        case BlogController.TYPE_DRAFT:
            postlist = new PostList(mainCanvas,blogController,mPost.getBlog(),BlogController.TYPE_DRAFT);
          //  mainCanvas.setCurrent(postlist);
            break;

        case BlogController.TYPE_RECENT:
            postlist = new PostList(mainCanvas,blogController,mPost.getBlog(),BlogController.TYPE_RECENT);
            //mainCanvas.setCurrent(postlist);
            break;

        default:
            throw new RuntimeException("unknown state");
        }
    }

	public void update(Observable observable, Object object) {
		BlogConnResponse resp=(BlogConnResponse)object;
		if(!resp.isError()) {
		
		  switch (mType) {
            case BlogController.TYPE_DRAFT:
            	try {
					blogController.removeDraftPost(mPost.getBlog(), mDraftId);
				} catch (RecordStoreException e) {
					mainCanvas.displayErrorBlocking("Non riesco a cancellare il post dal datastore RMS");
				} catch (IOException e) {
					mainCanvas.displayErrorBlocking("Non riesco a cancellare il post dal datastore RMS");
				}
            	break;
		  }//fine switch
		  goBack();
		  
		} else { //errore della connessione http sottostante  
			mainCanvas.displayErrorBlocking(resp.getResponse());	  	
		}		

	}
	
	//salva il testo nel tab
	private void saveTab(int tabIndex) throws Exception{
		//#debug
		System.out.println("saveTab tab=" + tabIndex );
		
		if(tabIndex == 0) {
			try {
				saveTabZero();
				return;
			} catch (Exception e) {
	            throw new Exception("Cannot save description Tabs data: \n"+e.getMessage());
			}
		}
		
		PostTextBox itemContenitoreTesto=(PostTextBox)this.get(tabIndex,0);		
		
		 // Kluge around weird-arse P800 line breaks
       StringBuffer buf = new StringBuffer(itemContenitoreTesto.getString());
       int len = buf.length();
       for (int i = 0; i < len; i++) {
           if (buf.charAt(i) == '\u2029') {
               buf.setCharAt(i, '\n');
           }
       }

       String newBody = buf.toString();
       
       switch (itemContenitoreTesto.getType()) {
       case PostTextBox.TYPE_BODY:
    	   if(!newBody.equals(mPost.getBody())){
    		   mState.setModified(true);
    		   mPost.setBody(newBody);
    	   }   	  
           break;

       case PostTextBox.TYPE_EXTENDED:
           if(!newBody.equals(mPost.getExtendedBody())){
        	   mState.setModified(true);
        	   mPost.setExtendedBody(newBody);
           }
           break;

       case PostTextBox.TYPE_EXCERPT:
    	   if(!newBody.equals(mPost.getExcerpt())){
	           mState.setModified(!newBody.equals(mPost.getExcerpt()));
	           mPost.setExcerpt(newBody);
    	   }
           break;
       }
	}
	
	public boolean	notifyTabChangeRequested(int oldTabIndex, int newTabIndex){
		//#debug
		System.out.println("RequestTabChanged: new tab=" + newTabIndex );
		
		try {
			saveTab(oldTabIndex);
		} catch (Exception e) {
			mainCanvas.displayError(e.getMessage());	
		}
		//controlla il tab che il tab lasciato si ail primo ed aggiunge dei comandi
		if(oldTabIndex == 0){
			//aggiungi i comandi
			addCommand(htmlMarkupCommand);
			UiAccess.addSubCommand( PostTextBox.em , this.htmlMarkupCommand, this);
			UiAccess.addSubCommand( PostTextBox.label , this.htmlMarkupCommand, this);
			UiAccess.addSubCommand( PostTextBox.li , this.htmlMarkupCommand, this);
			UiAccess.addSubCommand( PostTextBox.mark , this.htmlMarkupCommand, this);
			UiAccess.addSubCommand( PostTextBox.ol , this.htmlMarkupCommand, this);
			UiAccess.addSubCommand( PostTextBox.p , this.htmlMarkupCommand, this);
			UiAccess.addSubCommand( PostTextBox.strong , this.htmlMarkupCommand, this);
			UiAccess.addSubCommand( PostTextBox.ul , this.htmlMarkupCommand, this);
			//#if polish.api.mmapi
			addCommand(mediaCommand);
			UiAccess.addSubCommand( PostTextBox.newPhotoCommand , this.mediaCommand, this);
			UiAccess.addSubCommand( PostTextBox.newVideoCommand , this.mediaCommand, this);
			UiAccess.addSubCommand( PostTextBox.newAudioCommand , this.mediaCommand, this);
			
			
			//#endif

		} else		
		if( newTabIndex == 0 ) {
			removeCommand(htmlMarkupCommand);
			removeCommand(mediaCommand);
		}
		return true;
	}
	
	public void notifyTabChangeCompleted(int oldTabIndex, int newTabIndex)  {
	}
}