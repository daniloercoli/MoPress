/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */

package com.danais.blog;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;

import com.danais.blog.model.Post;

import de.enough.polish.ui.UiAccess;


public class PostTextBox extends TextField{


    public final static int TYPE_BODY = 1;
    public final static int TYPE_EXTENDED = 2;
    public final static int TYPE_EXCERPT = 3;
    private int mType;
    
	private int mMark = -1;
    
	private Post mPost;

    public final static String LABEL_MARK = "Mark";
    public final static String LABEL_EM = "Emphasis";
    public final static String LABEL_STRONG = "Strong";
    public final static String LABEL_A = "Anchor";
    public final static String LABEL_P = "Paragraph";
    public final static String LABEL_LI = "List Item";
    public final static String LABEL_UL = "Unordered List";
    public final static String LABEL_OL = "Ordered List";
    
    final static Command mark= new Command(PostTextBox.LABEL_MARK, Command.SCREEN, 2);
    final static Command em=(new Command(PostTextBox.LABEL_EM, Command.SCREEN, 3));
    final static Command strong=(new Command(PostTextBox.LABEL_STRONG, Command.SCREEN, 4));
    final static Command label=(new Command(PostTextBox.LABEL_A, Command.SCREEN, 5));
    final static Command p=(new Command(PostTextBox.LABEL_P, Command.SCREEN, 6));
    final static Command li=(new Command(PostTextBox.LABEL_LI, Command.SCREEN, 7));
    final static Command ul=(new Command(PostTextBox.LABEL_UL, Command.SCREEN, 8));
    final static Command ol=(new Command(PostTextBox.LABEL_OL, Command.SCREEN, 9));
    
	final static Command newPhotoCommand = new Command("New Photo", Command.SCREEN, 1);
	final static Command newVideoCommand = new Command("New Video", Command.SCREEN, 2);
	final static Command newAudioCommand = new Command("New Audio", Command.SCREEN, 3);
    

    public PostTextBox(Post aPost, int aType, int maxSize) {
        super("", null, maxSize, TextField.ANY);
        
		//#style postItemInput
		UiAccess.setStyle( this );
		
        mType = aType;
        mPost = aPost;

        String body;

        switch (mType) {
        case TYPE_BODY:
            body = mPost.getBody();
            break;

        case TYPE_EXTENDED:
            body = mPost.getExtendedBody();
            break;

        case TYPE_EXCERPT:
            body = mPost.getExcerpt();
            break;

        default:
            throw new RuntimeException();
        }
        
        setString(body);        
    }
    
    public int getType() {
		return mType;
	}

    public void commandAction(Command aCommand, Displayable aDisplayable) {
        
    }

    protected void insertTag(String aTag) {
        insertTag(aTag, aTag);
    }

    public void insertImage(String url, String descr){
    	int caret = getCaretPosition();
        insert("<a href=\""+url+"\"  alt=\""+descr+"\">"+descr+"</a>", caret);
    }
    
    protected void insertTag(String aStart, String aEnd) {
        int caret = getCaretPosition();

        if (mMark == -1 || mMark == caret) {
            insert('<' + aStart + "></" + aEnd + '>', caret);
        } else {
            String start = '<' + aStart + '>';
            String end = "</" + aEnd + '>';
            int open;
            int close;
            
            if (mMark < caret) {
                open = mMark;
                close = caret;
            } else {
                open = caret;
                close = mMark;
            }

            insert(start, open);
            insert(end, close + start.length());
        }

        mMark = -1;
    }
    
 protected void insertHtmlMarkup(Command aCommand){
     if (aCommand.getLabel() == PostTextBox.LABEL_MARK) {
     	mMark=getCaretPosition();	    		
     } else if (aCommand.getLabel() == PostTextBox.LABEL_EM) {
     	insertTag("em");	
     } else if (aCommand.getLabel() == PostTextBox.LABEL_STRONG) {
     	insertTag("strong");	
     } else if (aCommand.getLabel() == PostTextBox.LABEL_A) {
     	insertTag("a href=\"http://\"", "a");
     } else if (aCommand.getLabel() == PostTextBox.LABEL_P) {
     	insertTag("p");
     } else if (aCommand.getLabel() == PostTextBox.LABEL_LI) {
     	insertTag("li");	
     } else if (aCommand.getLabel() == PostTextBox.LABEL_UL) {
     	insertTag("ul");	
     } else if (aCommand.getLabel() == PostTextBox.LABEL_OL) {
     	insertTag("ol");
     }
 }

}

