/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */

package com.danais.blog.model;

import java.util.Date;


/**
 * 
 * @author dercoli
 *
 */
public class Post {

    private Blog mBlog;
    private String mId = null;
    private String mTitle = null;
    private String mAuthor = null;
    private Category mPrimaryCategory = null;
    private Date mAuthoredOn_GMT = null;
    private boolean mConvertBreaks = true;
    private boolean mAllowComments = true;
    private boolean mAllowTrackback = true;
    private String mBody = "";
    private String mExtended = "";
    private String mExcerpt = "";
    private String mURI = "";
    private String tags = "";
    
    

    public Post(Blog aBlog) {
        mBlog = aBlog;
    }

    public Post(Blog aBlog, String aId, String aTitle, String aAuthor, Date aAuthoredOn, String URI) {
        mBlog = aBlog;
        mId = aId;
        mTitle = aTitle;
        mAuthor = aAuthor;
        mAuthoredOn_GMT = aAuthoredOn;
        setURI(URI);
    }

    public Blog getBlog() {
        return mBlog;
    }

    public String getId() {
        return mId;
    }

    public void setId(String aId) {
        mId = aId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String aTitle) {
        mTitle = aTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String aAuthor) {
        mAuthor = aAuthor;
    }

    public Category getPrimaryCategory() {
        return mPrimaryCategory;
    }

    public void setPrimaryCategory(Category aCategory) {
        mPrimaryCategory = aCategory;
    }

    public Date getAuthoredOn_GMT() {
        return mAuthoredOn_GMT;
    }

    public void setAuthoredOn_GMT(Date aAuthored) {
        mAuthoredOn_GMT = aAuthored;
    }

    public boolean isConvertLinebreaksEnabled() {
        return mConvertBreaks;
    }

    public void setConvertLinebreaksEnabled(boolean aEnabled) {
        mConvertBreaks = aEnabled;
    }

    public boolean isCommentsEnabled() {
        return mAllowComments;
    }

    public void setCommentsEnabled(boolean aEnabled) {
        mAllowComments = aEnabled;
    }

    public boolean isTrackbackEnabled() {
        return mAllowTrackback;
    }

    public void setTrackbackEnabled(boolean aEnabled) {
        mAllowTrackback = aEnabled;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String aBody) {
        mBody = aBody;
    }

    public String getExtendedBody() {
        return mExtended;
    }

    public void setExtendedBody(String aExtended) {
        mExtended = aExtended;
    }

    public String getExcerpt() {
        return mExcerpt;
    }

    public void setExcerpt(String aExcerpt) {
        mExcerpt = aExcerpt;
    }

	public void setURI(String mURI) {
		this.mURI = mURI;
	}

	public String getURI() {
		return mURI;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTags() {
		return tags;
	}
}
