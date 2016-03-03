package com.project.enjoyit;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.baidu.location.b.d;
import com.project.enjoyit.entity.User;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;
import com.project.enjoyit.utils.MyNetworkUtil;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity {
	private static String TAG = "UserInfoActivity";
	private static User user = new User();
	private static String username = "";

	private CircleImageView civHeadPic;
	private TextView tvName;
	private TextView tvMood;
	private TextView tvSex;
	private TextView tvAge;
	private TextView tvRegTime;
	
	public static final int CODE_REGISTER2LOGIN = 1;

	private static final String sbDefaultAccountFileName = "DefaultAccount";
	private static Context context;
	private final static int CODE_TOAST_MSG = 0;
	static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_TOAST_MSG:
				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		};
	};

	public static void myToast(String msg) {
		handler.obtainMessage(CODE_TOAST_MSG, msg).sendToTarget();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_user_info);
		initView();
		context = UserInfoActivity.this;
	}
	private void initView() {
		civHeadPic = (CircleImageView)findViewById(R.id.civ_head_pic);
		tvName = (TextView)findViewById(R.id.tv_name);
		tvMood = (TextView)findViewById(R.id.tv_mood);
		tvSex = (TextView)findViewById(R.id.tv_sex);
		tvAge = (TextView)findViewById(R.id.tv_age);
		tvRegTime = (TextView)findViewById(R.id.tv_reg_time);
		
	}
	@Override
	protected void onStart() { 
		// TODO Auto-generated method stub
		super.onStart();
		username = this.getIntent().getExtras().getString("username");
		Log.e("FFF", username);
		getUserInfo();
	}
	private void getUserInfo() {
		final AlertDialog dialog = new SpotsDialog(context);
		dialog.show();
		Listener<String> listener = new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getInt("code") == 1) {
						Log.e(TAG, "取用户信息成功" );
						user.updateFromJSONObject(jsonObject);
						updateView();
					} else {
						Log.e(TAG, "获取用户信息失败：" + response);
						myToast("获取用户信息失败");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(TAG, "获取用户信息失败：" + response);
					myToast("解析失败，无法取得用户信息");
				}finally{
					dialog.dismiss();
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "获取用户信息失败：");
				myToast("是不是网络出问题了呢？");
				dialog.dismiss();
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", username);
		MyApplication.getMyVolley().addPostStringRequest(MyURL.GET_USER_INFO,
				listener, errorListener, map, TAG);
	}

	protected void updateView() {
		ImageListener listener = ImageLoader.getImageListener(civHeadPic,  
		        R.drawable.head_cat, R.drawable.head_cat);  
		String url = MyURL.GET_IMAGE_URL + user.getHead_pic();
		MyApplication.getMyVolley().getImageLoader().get(url, listener);
		
		tvName.setText("我是 "+user.getUsername());
		tvMood.setText(user.getMood());
		String sex = user.getSex()==1? "男" : (user.getSex()==0? "女" : "男 /女");
		tvSex.setText("我是"+sex+"的");
		tvAge.setText("我今年"+user.getAge()+"岁");
		tvRegTime.setText("我是从"+user.getReg_time1()+"开始在这里玩的");
	}

}
