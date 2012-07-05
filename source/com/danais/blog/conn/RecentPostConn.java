package com.danais.blog.conn;

import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

import com.danais.blog.model.Blog;
import com.danais.blog.model.Post;

public class RecentPostConn extends BlogConn  {
	
	private int maxPost;
	private Blog blog;
	
	public RecentPostConn(String hint,String userHint, String passwordHint, TimeZone tz, Blog aBlog,int postCount) {
		super(hint, userHint, passwordHint, tz);
		maxPost= postCount;
		this.blog=aBlog;
	}

	/**
	 * reperisce da remoto gli i post più recenti
	 * @param provider
	 */
	public void run() {
		try{
		
	        Vector args = new Vector(4);
	        args.addElement(blog.getBlogId());
	        args.addElement(mUsername);
	        args.addElement(mPassword);
	        args.addElement(new Integer(maxPost));

	        Object response = execute("mt.getRecentPostTitles", args);
			if(connResponse.isError()) {
				//se il server xml-rpc è andato in err
				notifyObservers(connResponse);
				return;		
			}

            Vector responsePosts = (Vector) response;
            Hashtable postData = null;
            Post[] recentPosts = new Post[responsePosts.size()];
            for (int i = 0; i < recentPosts.length; i++) {
                postData = (Hashtable) responsePosts.elementAt(i);
                recentPosts[i] = new Post(blog,
                                          (String) postData.get("postid"),
                                          (String) postData.get("title"),
                                          (String) postData.get("userid"),
                                          (Date) postData.get("date_created_gmt"),
                                          ""
                                          );
            }

			connResponse.setResponseObject(recentPosts);
			notifyObservers(connResponse);
		} catch (Exception cce) {
			setErrorMessage(cce, "loadBlogs error");
			notifyObservers(connResponse);
		}
	}
}
