
package com.danais.blog;



import java.util.Date;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;


import com.danais.blog.conn.BlogConnResponse;
import com.danais.blog.conn.DeleteCommentConn;
import com.danais.blog.conn.EditCommentConn;
import com.danais.blog.model.Blog;
import com.danais.blog.model.Post;
import com.danais.blog.model.Comment;
import com.danais.mobile.MainCanvas;
import com.danais.utils.CalendarUtils;
import com.danais.utils.observer.Observable;
import com.danais.utils.observer.Observer;
import com.danais.blog.BlogController;

import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;



public class CommentsList extends List implements CommandListener, Observer {
	
    private Blog mBlog;
    private BlogController blogController;
    private MainCanvas mainCanvas;
    private Comment[] commentsList;
    private Post post;
    private int selected;
    
   

    private final static String LABEL_DELETE_COMMENT = "Delete";
    private final static String LABEL_READ_COMMENTS = "Read";
    private final static String LABEL_APPROVE_COMMENT = "Approve";
    private final static String LABEL_UNAPPROVE_COMMENT = "Unapprove";
    private final static String LABEL_SPAM_COMMENT = "Mark as Spam";




    private final static String LABEL_EMPTY_TITLE = "<no title>";

    public CommentsList(MainCanvas mainCnv, BlogController aClient, Blog aBlog, Comment[] commList, Post aPost) {
        super(null, List.IMPLICIT);
        //#style myForm
    	UiAccess.setStyle( this ); 

        blogController = aClient;
        mBlog = aBlog;
        mainCanvas = mainCnv;
        post = aPost;
        commentsList = commList;

        String title;

        Command readcmd = new Command(LABEL_READ_COMMENTS, Command.ITEM, 2);
        addCommand(readcmd);
        
        Command delCmd=  new Command(LABEL_DELETE_COMMENT, Command.ITEM, 3);
        addCommand(delCmd);
        
        Command approveCmd=  new Command(LABEL_APPROVE_COMMENT, Command.ITEM, 4);
        addCommand(approveCmd);
        
        Command unapproveCmd=  new Command(LABEL_UNAPPROVE_COMMENT, Command.ITEM, 5);
        addCommand(unapproveCmd);
        
        Command spamCmd=  new Command(LABEL_SPAM_COMMENT, Command.ITEM, 6);
        addCommand(spamCmd);


     
        addCommand(new Command(Locale.get("cmd.Back"), Command.BACK, 4));
        
        setCommandListener(this);
        setSelectCommand(readcmd);
        mainCnv.setCurrent(this);
        
        title = post.getTitle() + ": Comments";
        setTitle(title);
        showComments();
    }

	private void showComments() {
		String title;
		long timeToAdd;
		Date dateCreatedTZ;

		for (int i = 0; i < commentsList.length; i++) {
			if (commentsList[i] != null){
				String author = ((Comment) commentsList[i]).getAuthor();
				Date date =  ((Comment) commentsList[i]).getDate_created_gmt();
				String status =  ((Comment) commentsList[i]).getStatus();

				
				timeToAdd = date.getTime();
	        	timeToAdd = CalendarUtils.adjustTimeToDefaultTimezone(timeToAdd);
	        	dateCreatedTZ = new Date(timeToAdd); //costruttore con TZ
	        	
				String authoredOn = dateCreatedTZ.toString();
				authoredOn = authoredOn.substring(0, 20);
				
				title ="By " + author + " on " + authoredOn + ". STATUS: " + status;
				
				if (title != null){
					if (title.length() == 0) {
						title = LABEL_EMPTY_TITLE;
					}
					append(title, null);
				}
			}
			
		}
		mainCanvas.setCurrent(this);
		
	}


	public void commandAction(Command aCommand, Displayable aDisplayable) {

        
		if (aCommand.getLabel() == Locale.get("cmd.Back")) {
			new PostList(mainCanvas, blogController, mBlog, blogController.TYPE_RECENT);
		}
		else if (aCommand.getLabel() == LABEL_READ_COMMENTS || aCommand == List.SELECT_COMMAND){
	        selected = getSelectedIndex();
	        Comment comment = (Comment) commentsList[selected];
			 String body = comment.getContent();
			 mainCanvas.displayMessage(body); 
		 }
		
		
		else if (aCommand.getLabel() == LABEL_APPROVE_COMMENT){
		       selected = getSelectedIndex();
		        Comment comment = (Comment) commentsList[selected];
		        comment.setStatus("approve");
				EditCommentConn editConn = new EditCommentConn( mBlog.getBlogXmlRpcUrl(), mBlog.getBlogId(), 
						mBlog.getUsername(), mBlog.getPassword(),  mainCanvas.getPreferences().getTimeZone(), comment);
				
				editConn.addObserver(this);
	            mainCanvas.displayConnectionInProgress("Approving comment...", editConn);
	            editConn.startConnWork();
		}
		
		else if (aCommand.getLabel() == LABEL_UNAPPROVE_COMMENT){
		      selected = getSelectedIndex();
		        Comment comment = (Comment) commentsList[selected];
		        comment.setStatus("hold");

				EditCommentConn editConn = new EditCommentConn( mBlog.getBlogXmlRpcUrl(), mBlog.getBlogId(), 
						mBlog.getUsername(), mBlog.getPassword(),  mainCanvas.getPreferences().getTimeZone(), comment);
				
				editConn.addObserver(this);
	            mainCanvas.displayConnectionInProgress("Holding comment...", editConn);
	            editConn.startConnWork();
			
		}
		
		else if (aCommand.getLabel() == LABEL_SPAM_COMMENT){
		      selected = getSelectedIndex();
		        Comment comment = (Comment) commentsList[selected];
		        comment.setStatus("spam");
				EditCommentConn editConn = new EditCommentConn( mBlog.getBlogXmlRpcUrl(), mBlog.getBlogId(), 
						mBlog.getUsername(), mBlog.getPassword(),  mainCanvas.getPreferences().getTimeZone(), comment);
				
				editConn.addObserver(this);
	            mainCanvas.displayConnectionInProgress("Marking comment as spam...", editConn);
	            editConn.startConnWork();
				this.commentsList[selected] = null;

			
		}
		
		else if (aCommand.getLabel() == LABEL_DELETE_COMMENT){
	        selected = getSelectedIndex();
	        Comment comment = (Comment) commentsList[selected];
			int comment_ID = comment.getID();
			DeleteCommentConn deleteConn = new DeleteCommentConn( mBlog.getBlogXmlRpcUrl(), mBlog.getBlogId(), 
					mBlog.getUsername(), mBlog.getPassword(),  mainCanvas.getPreferences().getTimeZone(), comment_ID);
			
			deleteConn.addObserver(this);
            mainCanvas.displayConnectionInProgress("Deleting comment...", deleteConn);
            deleteConn.startConnWork();
			this.commentsList[selected] = null;

		}
			 
	}

	public void update(Observable observable, Object object) {
		BlogConnResponse resp=(BlogConnResponse)object;
		
		if(!resp.isError()) {
			if(resp.isStopped()){
				return;
			}	
			Boolean result=(Boolean)resp.getResponseObject();
			
			if (result.booleanValue()== true){
				CommentsList commentsCnv = new CommentsList(mainCanvas, blogController, mBlog, commentsList, post);
				mainCanvas.setCurrent(commentsCnv);
			}
			else mainCanvas.displayMessage("Error during comment's marking");
			
		}
		
		else {  
			mainCanvas.displayErrorBlocking(resp.getResponse());
		}
		
	}
		
}
