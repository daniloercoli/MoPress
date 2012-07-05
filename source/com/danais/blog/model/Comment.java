/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */

package com.danais.blog.model;


import java.util.Date;

public class Comment {

	private int commentId = -1;
	private int postID = -1;
	private String postTitle = "";
	private int commentParent;
	private String content = "";
	private String author = "";
	private String authorUrl = "";
	private String authorEmail = "";
	private String commentStatus = "";
	private Date dateCreatedGmt;
	private String userID = "";
	private String link = "";
	private String authorIp = "";
	private int offset = 0;
	private int number = 0;
	private String count = "";
	
    /**  datetime dateCreated (ISO.8601, always GMT)
    * string user_id
    * string comment_id
    * string parent
    * string status
    * string content
    * string link
    * string post_id
    * string post_title
    * string author
    * string author_url
    * string author_email
    * string author_ip */
	
	
	public Comment(String author, String content, Date dateCreatedGmt, String user_id, String comment_id, 
			String parent, String status, String link, String post_id, String post_title, String author_url, 
			String author_email, String author_ip) {
		
		this.author = author;
		this.content = content;
		this.dateCreatedGmt = dateCreatedGmt;
		this.userID = user_id;
		this.commentId = Integer.parseInt(comment_id);
		this.commentParent = Integer.parseInt(parent);
		this.commentStatus = status;
		this.link = link;
		this.postID = Integer.parseInt(post_id);
		this.postTitle = post_title;
		this.authorUrl = author_url;
		this.authorEmail = author_email;
		this.authorIp = author_ip;
		
	
	}
	

	public int getID() {
		return commentId;
	}

	public void setID(int commentId) {
		this.commentId = commentId;
	}

	public int getPostID() {
		return postID;
	}

	public void setPostID(int postID) {
		this.postID = postID;
	}

	public int getParent() {
		return commentParent;
	}

	public void setParent(int commentparent) {
		commentParent = commentparent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String cont) {
		content = cont;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String auth) {
		author = auth;
	}

	public String getAuthorUrl() {
		return authorUrl;
	}

	public void setAuthorUrl(String authurl) {
		authorUrl = authurl;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authemail) {
		authorEmail = authemail;
	}

	public void setStatus(String commentStatus) {
		this.commentStatus = commentStatus;
	}

	public String getStatus() {
		return commentStatus;
	}

	public void setDate_created_gmt(Date date_created_gmt) {
		this.dateCreatedGmt = date_created_gmt;
	}

	public Date getDate_created_gmt() {
		return dateCreatedGmt;
	}

	public void setUserId(String user_Id) {
		this.userID = user_Id;
	}

	public String getUserId() {
		return userID;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLink() {
		return link;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setAuthorIp(String author_Ip) {
		this.authorIp = author_Ip;
	}

	public String getAuthorIp() {
		return authorIp;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getCount() {
		return count;
	}
}

