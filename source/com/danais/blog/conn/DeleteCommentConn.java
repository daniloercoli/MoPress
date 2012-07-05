package com.danais.blog.conn;

import java.util.TimeZone;
import java.util.Vector;




public class DeleteCommentConn extends BlogConn  {
	
	private String blogId;
	private int commentID;
	
	public DeleteCommentConn(String hint, String blog_Id, String userHint, String passwordHint,  TimeZone tz,
			int comment_ID){
		
		super(hint, userHint, passwordHint, tz);
		
		blogId=blog_Id;
		commentID= comment_ID;
	
	}
	
	public void run() {
		try {
			
			
			Vector args = new Vector(4);
			
	        args.addElement(blogId);
	        args.addElement(mUsername);
	        args.addElement(mPassword);
	        args.addElement(String.valueOf(commentID));
		
	        Object response = execute("wp.deleteComment", args);

	        
			if(connResponse.isError()) {
				notifyObservers(connResponse);
				return;		
			}
			
			
	
           /* Hashtable commentData = (Hashtable) response;
            String result;
            
            if( commentData.get("status") instanceof String) {

            	result = (String) commentData.get("status");
            	System.out.println("RESULTTTTT" + result);
            	
            } else {
            	
            	result = String.valueOf(commentData.get("status"));
            	System.out.println("RESULT BOOOLL" + result);

    
            }*/
            
         
			connResponse.setResponseObject(response);
			notifyObservers(connResponse);
            
			}
		
			catch (Exception e) {
				setErrorMessage(e, "Deletecomments error: Invalid server response");
	        }
			
			try {
				notifyObservers(connResponse);
			} catch (Exception e) {
				System.out.println("GetComments error: Notify error"); 
			}
			
		}
	}