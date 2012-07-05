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

import com.danais.blog.conn.BlogConnResponse;
import com.danais.blog.conn.DeletePostConn;
import com.danais.blog.conn.GetCommentsConn;
import com.danais.blog.conn.GetPostConn;
import com.danais.blog.conn.RecentPostConn;
import com.danais.blog.model.Blog;
import com.danais.blog.model.Post;
import com.danais.blog.model.Comment;
import com.danais.mobile.MainCanvas;
import com.danais.utils.observer.Observable;
import com.danais.utils.observer.Observer;
import com.danais.blog.BlogPostPreviewForm;

import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;



public class PostList extends List implements CommandListener, Observer {
    private Blog mBlog;
    private BlogController blogController;
    private MainCanvas mainCanvas;
    private int mType;
    private Object[] mPosts = null;
    private Post selectedPost;
    
    private boolean isLoadingRecentPost=false; //indica se stiamo caricando da remoto un post recente
    private boolean isDeletingRecentPost=false;  
    private boolean isLoadingRecentComments=false;
    private boolean isPreview=false;  



    private final static String LABEL_EDIT_POST = "Edit";
    private final static String LABEL_DELETE_POST = "Delete";
    private final static String LABEL_RECENT_COMMENTS = "Comments";
    private final static String LABEL_PREVIEW = "Preview";


    private final static String LABEL_EMPTY_TITLE = "<no title>";

    public PostList(MainCanvas mainCnv, BlogController aClient, Blog aBlog, int aType) {
        super(null, List.IMPLICIT);
        //#style myForm
    	UiAccess.setStyle( this ); 

        blogController = aClient;
        mBlog = aBlog;
        mType = aType;
        mainCanvas=mainCnv;

        String title;

        Command editCmd=  new Command(LABEL_EDIT_POST, Command.ITEM, 2);
        addCommand(editCmd);
        //if (aType == BlogController.TYPE_DRAFT) {
        addCommand(new Command(LABEL_DELETE_POST, Command.ITEM, 3));
        addCommand(new Command(LABEL_PREVIEW, Command.ITEM, 3));

        //}
        if (aType != BlogController.TYPE_DRAFT) {
        	addCommand(new Command(LABEL_RECENT_COMMENTS, Command.ITEM, 3));
        }
        addCommand(new Command(Locale.get("cmd.Back"), Command.BACK, 4));
        setCommandListener(this);
        setSelectCommand(editCmd);
        mainCnv.setCurrent(this);
                
        switch (aType) {
        case BlogController.TYPE_DRAFT:
            try {
                mPosts = blogController.getDraftPostList(aBlog);
                for (int i = 1; i < mPosts.length; i += 2) {
                    title = ((Post) mPosts[i]).getTitle();
                    if (title == null || title.length() == 0) {
                        title = LABEL_EMPTY_TITLE;
                    }
                    append(title, null);
                }
            } catch (Exception e) {
               mainCanvas.displayError("Could not create list of drafts");
             
            }
            title = aBlog.getBlogName() + ": Drafts";
            setTitle(title);
            break;

        case BlogController.TYPE_RECENT:
            title = aBlog.getBlogName() + ": Recent";
            setTitle(title);
	        loadRemotePosts();
            break;

        default:
            throw new RuntimeException("unsupported type");
        }

    }

    /**
     * carica i posts remoti 
     */
	private void loadRemotePosts() {
		if(mType != BlogController.TYPE_RECENT) return; // si assicura che siamo nel caso dei recenti
		Preferences prefs = mainCanvas.getPreferences();
		RecentPostConn connection = new RecentPostConn (mBlog.getBlogXmlRpcUrl(),mBlog.getUsername(),
				mBlog.getPassword(),  prefs.getTimeZone(), mBlog, prefs.getRecentPostCount());
		connection.addObserver(this);
		mainCanvas.displayConnectionInProgress("Caricamento Post Recenti in corso...",connection);
		connection.startConnWork(); //esegue il lavoro della connessione
	}

    public void commandAction(Command aCommand, Displayable aDisplayable) {
        if (aCommand.getLabel() == LABEL_EDIT_POST || aCommand == List.SELECT_COMMAND) {
            int selected = getSelectedIndex();
            if (selected != -1) {
                try {
                    Post post;
                                    
                    if (mType == BlogController.TYPE_DRAFT) {
                        post = (Post) mPosts[1 + (selected * 2)];
                        int id = ((Integer) mPosts[selected * 2]).intValue();
                        blogController.updateDraftPost(post, mBlog, id);
                        BlogPostForm postform = new BlogPostForm(post, blogController, id, mainCanvas);
                        mainCanvas.setCurrent(postform);
                    } else {
                    	//qua andiamo a caricare il post da remoto!!
                        post = (Post) mPosts[selected];
                        isLoadingRecentPost=true;
                        GetPostConn connection = new GetPostConn (mBlog.getBlogXmlRpcUrl(),mBlog.getUsername(),
                        		mBlog.getPassword(),  null, post);
                        connection.addObserver(this);
                        mainCanvas.displayConnectionInProgress("Remote Posts loading...",connection);
                        connection.startConnWork(); //esegue il lavoro della connessione                        
                    }
                } catch (Exception e) {
    	         	//#debug error
    	    		System.out.println("commandAction failed: " +e);
                }
            }
        } 
        
        
        if (aCommand.getLabel() == LABEL_PREVIEW) {
            int selected = getSelectedIndex();
            if (selected != -1) {
                try {
                    Post post;
                                    
                    if (mType == BlogController.TYPE_DRAFT) {
                        post = (Post) mPosts[1 + (selected * 2)];
                        int id = ((Integer) mPosts[selected * 2]).intValue();
                        blogController.updateDraftPost(post, mBlog, id);
                		mainCanvas.pushCurrent(this);
                        new BlogPostPreviewForm(post, blogController, id, mainCanvas);
                    } else {
                    	//qua andiamo a caricare il post da remoto!!
                        post = (Post) mPosts[selected];
                        isPreview=true;
                        GetPostConn connection = new GetPostConn (mBlog.getBlogXmlRpcUrl(),mBlog.getUsername(),
                        		mBlog.getPassword(),  null, post);
                        connection.addObserver(this);
                        mainCanvas.displayConnectionInProgress("Remote Posts loading...",connection);
                        connection.startConnWork(); //esegue il lavoro della connessione                        
                    }
                } catch (Exception e) {
    	         	//#debug error
    	    		System.out.println("commandAction failed: " +e);
                }
            }
        } 
        
        
        if (aCommand.getLabel() == LABEL_RECENT_COMMENTS){
        	int selected = getSelectedIndex();
        	this.isLoadingRecentComments = true;
        	
        	
        	if (selected != -1){
        		try{
        			Post post = (Post) mPosts[selected];
        			this.selectedPost = post;
           		 	GetCommentsConn connection = new GetCommentsConn (mBlog.getBlogXmlRpcUrl(), Integer.parseInt(mBlog.getBlogId()), mBlog.getUsername(),
     mBlog.getPassword(), mainCanvas.getPreferences().getTimeZone(), Integer.parseInt(post.getId()), "approve", 0, 10);
                    connection.addObserver(this);
                    mainCanvas.displayConnectionInProgress("Getting recent comments...",connection);
                    connection.startConnWork(); //esegue il lavoro della connessione 
        			
        		}
        		catch (Exception e) {
    	         	//#debug error
    	    		System.out.println("commandAction failed: " +e);
                }
        	}
        }
        
        
        
        else if (aCommand.getLabel() == LABEL_DELETE_POST) {
            int selected = getSelectedIndex();
            
            if (selected != -1) {
                try {
                	 if (mType == BlogController.TYPE_DRAFT) {
	                    int id = ((Integer) mPosts[selected * 2]).intValue();
	                    blogController.removeDraftPost(mBlog, id);
	                    //FIXME: non bene cosi, perchè vado a ricaricare la lista da capo
	                    new PostList(mainCanvas,blogController, mBlog, mType);
	                    //mainCanvas.setCurrent(new PostList(mainCanvas,blogController, mBlog, mType));
                	 } else {
                		 isDeletingRecentPost=true;
                		 Post post = (Post) mPosts[selected];
                		 DeletePostConn connection = new DeletePostConn (mBlog.getBlogXmlRpcUrl(),mBlog.getUsername(),
                         		mBlog.getPassword(),  null, post);
                         connection.addObserver(this);
                         mainCanvas.displayConnectionInProgress("Deleting Remote Posts...",connection);
                         connection.startConnWork(); //esegue il lavoro della connessione                        
                	 }
                } catch (Exception e) {
    	         	//#debug error
    	    		System.out.println("commandAction failed: " +e);
                }
            }
        } else if (aCommand.getLabel() == Locale.get("cmd.Back")) {
            mainCanvas.displayBlogListUI();
        }
    }

	public void update(Observable observable, Object object) {
		BlogConnResponse resp=(BlogConnResponse)object;

		if(isLoadingRecentPost) { //quando voglio visualizzare un post recente...
			handleLoadPost(resp);
			return;
		} 
		else if (isLoadingRecentComments){
			handleLoadComments(resp);
			return;
		}
		
		else if (isPreview){
			handlePreview(resp);
			return;
		}
		
		else if (isDeletingRecentPost) { //stiamo cancellando un post remoto
			 isDeletingRecentPost=false;
			if(!resp.isError()) {
				if(resp.isStopped()){
					return;
				}
				new PostList(mainCanvas,blogController, mBlog, mType);
				//mainCanvas.setCurrent(new PostList(mainCanvas,blogController, mBlog, mType));
			} else {  
				mainCanvas.displayErrorBlocking(resp.getResponse());
			}
			return;
		}

		//in questo caso stiamo caricando la lista dei post per la prima volta
		if(!resp.isError()) {
			if(resp.isStopped()){
				return;
			}
					
		mPosts= (Post[]) resp.getResponseObject();
		String title;
	        for (int i = 0; i < mPosts.length; i++) {
	            title = ((Post) mPosts[i]).getTitle();
	            if (title == null || title.length() == 0) {
	                title = LABEL_EMPTY_TITLE;
	            }
	            append(title, null);
	        }
	        mainCanvas.setCurrent(this);
		} else {  
			mainCanvas.displayErrorBlocking(resp.getResponse());
			mainCanvas.displayBlogListUI();
		}	
	}
	
	private void handleLoadPost(BlogConnResponse resp){
		isLoadingRecentPost=false;	
		if(!resp.isError()) {
			if(resp.isStopped()){
				return;
			}
			Post post=(Post)resp.getResponseObject();
			BlogPostForm postform = new BlogPostForm(post, blogController, mainCanvas);
            mainCanvas.setCurrent(postform);

		} else {  
			mainCanvas.displayErrorBlocking(resp.getResponse());
		}	
	}
	
	private void handlePreview(BlogConnResponse resp){
		isPreview = false;	
		if(!resp.isError()) {
			if(resp.isStopped()){
				return;
			}
			
			Post post=(Post)resp.getResponseObject();
			mainCanvas.pushCurrent(this);

			new BlogPostPreviewForm(post, blogController, mainCanvas);
		}else {  
			mainCanvas.displayErrorBlocking(resp.getResponse());
		}	
	}
		

	
	private void handleLoadComments(BlogConnResponse resp){
		isLoadingRecentComments = false;
		if(!resp.isError()) {
			if(resp.isStopped()){
				return;
			}
			
			if ((resp.getResponseObject())== null)
				System.out.println("Some problem here");
			else System.out.println("No problem");
			
			Comment[] commentsList=(Comment[])resp.getResponseObject();
			
			if (commentsList.length != 0){
				CommentsList commentsCnv = new CommentsList(mainCanvas, blogController, mBlog, commentsList, selectedPost);
				mainCanvas.setCurrent(commentsCnv);
			}
			else mainCanvas.displayMessage("No comment for this post");
			
		}
		
		else {  
			mainCanvas.displayErrorBlocking(resp.getResponse());
		}
	}
}