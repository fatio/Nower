package com.project.enjoyit.entity;

import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {
	private int comment_id;
	private String comment_time;
	private String comment_user;
	private String commented_user;
	private String content;
	private int is_anonymous;
	
	public void updateFromJSONObject(JSONObject jshow) throws JSONException{
		setComment_id(jshow.getInt("comment_id"));
		setComment_user(jshow.getString("comment_user"));
		setCommented_user(jshow.getString("commented_user"));
		setComment_time(jshow.getString("comment_time"));
		setContent(jshow.getString("content"));
		setIs_anonymous(jshow.getInt("is_anonymous"));
	}
	
	
	
	public int getComment_id() {
		return comment_id;
	}
	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}
	public String getComment_time() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss"); 
		String t = "";
        if (comment_time != null) {
        	t =format.format(comment_time);
        }
        return t;
	}
	public void setComment_time(String comment_time) {
		this.comment_time = comment_time;
	}
	public String getComment_user() {
		return comment_user;
	}
	public void setComment_user(String comment_user) {
		this.comment_user = comment_user;
	}
	public String getCommented_user() {
		return commented_user;
	}
	public void setCommented_user(String commented_user) {
		this.commented_user = commented_user;
	}

	public int getIs_anonymous() {
		return is_anonymous;
	}
	public void setIs_anonymous(int is_anonymous) {
		this.is_anonymous = is_anonymous;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}
	
}
