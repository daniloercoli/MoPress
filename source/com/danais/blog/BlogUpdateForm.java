/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */
package com.danais.blog;

import com.danais.blog.conn.BlogConnResponse;
import com.danais.blog.conn.BlogUpdateConn;
import com.danais.blog.model.Blog;
import com.danais.mobile.MainCanvas;
import com.danais.utils.observer.Observable;
import com.danais.utils.observer.Observer;

public class BlogUpdateForm  implements Observer{
	private MainCanvas mainCanvas=null;
	private BlogUpdateConn connection = null;
	
	public BlogUpdateForm(Blog aBlog, MainCanvas mainCvn) {
		this.mainCanvas=mainCvn;
		
		connection = new BlogUpdateConn (null, aBlog);
	    connection.addObserver(this);
        mainCanvas.displayConnectionInProgress("Refreshing blog data...",connection);
	    connection.startConnWork(); //esegue il lavoro della connessione		
	}
	
	public void update(Observable observable, Object object) {
		BlogConnResponse resp=(BlogConnResponse)object;
		if(!resp.isError()) {
			if(!resp.isStopped()){
			mainCanvas.displayMessageBlocking("Refresh completed!");
			}
		} else {  
			mainCanvas.displayErrorBlocking(resp.getResponse());	  	
		}	
		mainCanvas.displayBlogListUI();
	}
}
