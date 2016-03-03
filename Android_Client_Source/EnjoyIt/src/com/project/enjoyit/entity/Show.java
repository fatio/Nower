package com.project.enjoyit.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Show {
	private int show_id;
	private String username;
	private String head_pic;
	private Date show_time;
	private String content;
	private String image_files;
	private String audio_file;
	private int is_anonymous;
	private int likes;
	private String address;
	private double latitude;
	private double longitude;
	private boolean has_liked;
	
	private ArrayList<Comment> allComments = new ArrayList<Comment>();

	public void updateFromJSONObject(JSONObject jshow) throws JSONException{
		setShow_id(jshow.getInt("show_id"));
		setUsername(jshow.getString("username"));
		setHead_pic(jshow.getString("head_pic"));
		setShow_time(jshow.getString("show_time"));
		setContent(jshow.getString("content"));
		setImage_files(jshow.getString("images"));
		setAudio_file(jshow.getString("audios"));
		setIs_anonymous(jshow.getInt("is_anonymous"));
		setAddress(jshow.getString("address"));
		setLatitude(jshow.getDouble("latitude"));
		setLongitude(jshow.getDouble("longitude"));
		setLikes(jshow.getInt("likes"));
		setHas_liked(jshow.getBoolean("has_liked"));
		
		JSONArray jsonArray = jshow.getJSONArray("comments");
		allComments.clear();
		for (int i=0; i<jsonArray.length(); ++i){
			Comment comment = new Comment();
			comment.updateFromJSONObject(jsonArray.getJSONObject(i));
			allComments.add(comment);
		}
	}
	public ArrayList<Comment> getAllComments() {
		return allComments;
	}

	public void setAllComments(ArrayList<Comment> allComments) {
		this.allComments = allComments;
	}
	
	
	public int getShow_id() {
		return show_id;
	}
	public void setShow_id(int show_id) {
		this.show_id = show_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getHead_pic() {
		return head_pic;
	}
	public void setHead_pic(String head_pic) {
		this.head_pic = head_pic;
	}
	public String getShow_time() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss"); 
		String t = "";
        if (show_time != null) {
        	t =format.format(show_time);
        }
        return t;
	}
	public void setShow_time(String show_time) {
		
		this.show_time = new Date(show_time);
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAudio_file() {
		return audio_file;
	}
	public void setAudio_file(String audio_file) {
		this.audio_file = audio_file;
	}
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
	public String getImage_files() {
		return image_files;
	}
	public void setImage_files(String image_files) {
		this.image_files = image_files;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getIs_anonymous() {
		return is_anonymous;
	}
	public void setIs_anonymous(int is_anonymous) {
		this.is_anonymous = is_anonymous;
	}

	public boolean isHas_liked() {
		return has_liked;
	}

	public void setHas_liked(boolean has_liked) {
		this.has_liked = has_liked;
	}
}
