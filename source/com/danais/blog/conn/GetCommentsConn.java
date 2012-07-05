package com.danais.blog.conn;

import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;


import com.danais.blog.model.Comment;


import java.util.Date;


public class GetCommentsConn extends BlogConn  {
	
	private int blogId;
	private int  postID=-1;
	private String status="";
	private int offset=0;
	private int number=0;
	private TimeZone time;
	
	public GetCommentsConn(String hint, int blog_Id, String userHint, String passwordHint,  TimeZone tz,
			int post_ID, String stat, int off_set, int num){
		super(hint, userHint, passwordHint, tz);
		
		this.blogId=blog_Id;
		this.postID=post_ID;
		this.status=stat;
		this.offset=off_set;
		this.number=num;
		this.time = tz;
	}
	
	public void run() {
		try {
			
			Hashtable StructData = new Hashtable(5);
			if (postID > 0) {
	            StructData.put("post_id", String.valueOf(postID));
	        } else {
	        	
	        }
			
            StructData.put("comment_status", status);
            
			if (offset!=0 ) {
	            StructData.put("offset", String.valueOf(offset));
	        }
			if (number != 0) {
	            StructData.put("number", String.valueOf(number));
	        }
			
			Vector args = new Vector(5);
	        args.addElement(String.valueOf(blogId));
	        args.addElement(mUsername);
	        args.addElement(mPassword);
	        args.addElement(StructData);
		
	        
	        
	        Object response = execute("wp.getComments", args);
			if(connResponse.isError()) {
				notifyObservers(connResponse);
				return;		
			}
			
			
			Vector responseComments = (Vector) response;
            Hashtable commentData = null;
            Comment[] comments = new Comment[responseComments.size()];
            for (int i = 0; i < comments.length; i++) {
                commentData = (Hashtable) responseComments.elementAt(i);
                comments[i] = new Comment(
                                          (String) commentData.get("author"),
                                          (String) commentData.get("content"),
                                          (Date) commentData.get("date_created_gmt"), 
                                          (String) commentData.get("user_id"),
                                          (String) commentData.get("comment_id"),
                                          (String) commentData.get("parent"),
                                          (String) commentData.get("status"),
                                          (String) commentData.get("link"),
                                          (String) commentData.get("post_id"),
                                          (String) commentData.get("post_title"),
                                          (String) commentData.get("author_url"),
                                          (String) commentData.get("author_email"),
                                          (String) commentData.get("author_ip")
                                          );
            }
			connResponse.setResponseObject(comments);
			notifyObservers(connResponse);
            
			}
		
			catch (Exception e) {
				setErrorMessage(e, "GetComments error: Invalid server response");
	        }
			
			try {
				notifyObservers(connResponse);
			} catch (Exception e) {
				System.out.println("GetComments error: Notify error"); 
			}
			
		}
	}