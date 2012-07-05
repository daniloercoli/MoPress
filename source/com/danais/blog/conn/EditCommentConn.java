package com.danais.blog.conn;

import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

import com.danais.blog.model.Comment;
 



public class EditCommentConn extends BlogConn  {
	
	private String blogId;
	private Comment comment;
	
	public EditCommentConn(String hint, String blog_Id, String userHint, String passwordHint, TimeZone tz, Comment aComment){
		super(hint, userHint, passwordHint, tz);
		
		this.blogId = blog_Id;
		this.comment = aComment;
	}
	
	public void run() {
		try {

			if (comment.getID() < 0 ) {
				 setErrorMessage("Error CommentId");
				 notifyObservers(connResponse);
		         return;
			}
	        Hashtable vcomment = new Hashtable(10);
	        if (comment.getStatus() != null) {
	        	vcomment.put("status", comment.getStatus());
	        }
	        if (comment.getDate_created_gmt() != null) {
	        	vcomment.put("date_created_gmt", comment.getDate_created_gmt());
	        }
	        if (comment.getContent() != null) {
	            vcomment.put("content", comment.getContent());
	        }
	        if (comment.getAuthor() != null) {
	            vcomment.put("author", comment.getAuthor());
	        }
	        if (comment.getAuthorUrl() != null) {
	            vcomment.put("author_url", comment.getAuthorUrl());
	        }
	        if (comment.getAuthorEmail() != null) {
	            vcomment.put("author_email", comment.getAuthorEmail());
	        }        
	        Vector args = new Vector(5);
	        args.addElement(blogId);
	        args.addElement(mUsername);
	        args.addElement(mPassword);
	        args.addElement(String.valueOf(comment.getID()));
	        args.addElement(vcomment);
	        
	        Object response = execute("wp.editComment", args);
			
	        if(connResponse.isError()) {
				notifyObservers(connResponse);
				return;	
			}
	        connResponse.setResponseObject(response);
		}
		catch (Exception e) {
			setErrorMessage(e, "EditComment error: Invalid server response");
		}
		
		try {
			notifyObservers(connResponse);
		} catch (Exception e) {
			System.out.println("EditComment error: Notify error"); 
		}
		
	}
}
