package com.project.enjoyit.global;

import com.baidu.mapapi.SDKInitializer;
import com.project.enjoyit.entity.User;
import com.project.enjoyit.utils.MyNetworkUtil;
import com.project.enjoyit.utils.MyPlace;
import com.project.enjoyit.utils.MyVolley;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	private static final String TAG = "MyApplication";

	private static Context context;
	private static MyNetworkUtil myNetworkUtil;
	private static MyVolley myVolley;
	private static MyPlace myPlace;
	private static User user;

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());
		context = getApplicationContext();

		initMyApplication();
	}
	
	public static void initMyApplication() {
		myNetworkUtil = new MyNetworkUtil();
		myNetworkUtil.initNetworkEnvironment(context);
		myVolley = new MyVolley(context);

		myPlace = new MyPlace(context);

		user = new User();
	}

	public static User getUser() {
		return user;
	}

	public static MyNetworkUtil getMyNetworkUtil() {
		return myNetworkUtil;
	}

	public static MyVolley getMyVolley() {
		return myVolley;
	}

	public static MyPlace getMyPlace() {
		return myPlace;
	}
}
