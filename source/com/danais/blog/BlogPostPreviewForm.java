
package com.danais.blog;




import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;


import com.danais.blog.model.Post;
import com.danais.blog.model.PostState;
import com.danais.mobile.MainCanvas;
import com.danais.utils.FileUtils;
import com.danais.utils.StringUtils;
import com.danais.utils.observer.Observable;
import com.danais.utils.observer.Observer;

import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.util.Locale;



public class BlogPostPreviewForm extends Form implements CommandListener, Observer {

    private Post mPost;
    private PostState mState = null;
    private int mType;
    private int mDraftId = -1;
    
    private BlogController blogController;
	private MainCanvas mainCanvas=null;
	HtmlBrowser myBrowser;

    

    public BlogPostPreviewForm(Post aPost, BlogController aClient, int aDraftId, MainCanvas mainCvn) {
    	super("");
        mPost = aPost;
        mType = BlogController.TYPE_DRAFT;
        mDraftId = aDraftId;
        mainCanvas = mainCvn;
        addCommand(new Command(Locale.get("cmd.Back"), Command.BACK, 4));
        setCommandListener(this);
        
        //#style htmlBrowser
    	myBrowser = new HtmlBrowser();


        buildPreview();
        
    }

    public BlogPostPreviewForm(Post aPost, BlogController aClient, MainCanvas mainCvn) {
    	super("");
        mPost = aPost;
        mType = BlogController.TYPE_RECENT;
        mainCanvas = mainCvn;
        
        addCommand(new Command(Locale.get("cmd.Back"), Command.BACK, 4));
        setCommandListener(this);
        //#style htmlBrowser
    	myBrowser = new HtmlBrowser();


        get();
    }

    private void get() {
    	String link = mPost.getURI();
    	myBrowser.go(link);
    	this.append(myBrowser);
    	
    	mainCanvas.setCurrent(this);
	}

	private void buildPreview() {
		String html = FileUtils.readTxtFile("defaultPostTemplate.html");
		
		String title = mPost.getTitle();
    	String body = mPost.getBody();
    	String cat = mPost.getPrimaryCategory().getLabel();
    	String tags = mPost.getTags();
    	
    	
    	html = StringUtils.replaceAll(html, "!$title$!", title);

        html = StringUtils.replaceAll(html, "!$text$!", body);

        html = StringUtils.replaceAll(html, "!$mt_keywords$!", tags);

        html = StringUtils.replaceAll(html, "!$categories$!", cat);
         
    	myBrowser.loadPage(html);
   
    	this.append(myBrowser);
    	
    	mainCanvas.setCurrent(this);
    	
    }

	
	public void commandAction(Command aCommand, Displayable aDisp) {
		// TODO Auto-generated method stub
		//if (aCommand.getLabel() == LABEL_EXIT){
		if (aCommand.getLabel() == Locale.get("cmd.Back")) {
            mainCanvas.popCurrent();
        }
			
	
			 
	}
			 
		
		
	public void update(Observable observable, Object object) {
		// TODO Auto-generated method stub
		
	}
}
