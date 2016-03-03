package com.project.enjoyit.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.project.enjoyit.LoginActivity;
import com.project.enjoyit.MainActivity;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;




public class User {
	private String token;
	private String username;
	private String password;
	private int sex;
	private String mood;
	private int age;
	private int state;
	private String phone;
	private Date reg_time;
	private String head_pic;
	
	public String getHead_pic() {
		return head_pic;
	}

	public void setHead_pic(String head_pic) {
		this.head_pic = head_pic;
	}

	public void updateFromJSONObject(JSONObject jsonObject) throws JSONException{
		username = jsonObject.getString("username");
		sex = jsonObject.getInt("sex");
		mood = jsonObject.getString("mood");
		age = jsonObject.getInt("age");
		state = jsonObject.getInt("state");
		phone = jsonObject.getString("phone");
		setReg_time(jsonObject.getString("reg_time"));
		head_pic = jsonObject.getString("head_pic");
	}
	
	public String getReg_time(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");  
		String t = "";
        if (reg_time != null) {
        	t =format.format(reg_time);
        }  
        return t;
	}
	public String getReg_time1(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");  
		String t = "";
        if (reg_time != null) {
        	t =format.format(reg_time);
        }  
        return t;
	}
	
	public void updateFromNetwork(){
		final String TAG = "User";
		Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getInt("code") == 1) {
						Log.e(TAG, "取用户信息成功" );
						updateFromJSONObject(jsonObject);

					} else {
						Log.e(TAG, "获取用户信息失败：" + response);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(TAG, "获取用户信息失败：" + response);
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "获取用户信息失败：");
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", username);
		MyApplication.getMyVolley().addPostStringRequest(MyURL.GET_USER_INFO,
				listener, errorListener, map, TAG);
	}
	
	public void setReg_time(String reg_time){
		this.reg_time = new Date(reg_time);
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token.replace("=", "%3D");
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
