package com.project.enjoyit;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import me.drakeet.materialdialog.MaterialDialog;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.jauker.widget.BadgeView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.project.enjoyit.global.MyAlgorithm;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;
import com.project.enjoyit.utils.MyNetworkUtil;
import com.project.enjoyit.utils.MyPlace;

import de.hdodenhof.circleimageview.CircleImageView;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private static int new_msg_num = 0;

	private static CircleImageView civHeadPic;
	private TextView tvUsername;
	private TextView tvMood;
	private TextView tvAddress;
	private TextView tvNeighbor;
	private TextView tvMy;

	private static final int GET_UNREAD_TIME = 5000;// 5s
	private static boolean UNREAD_HEART = true;
	
	private static BadgeView badgeView;

	private FragmentManager fragmentManager;
	private MyShowsFragment myShowsFragment;
	private NeighborShowsFragment neighborShowsFragment;

	private static Context context;
	private final static int CODE_TOAST_MSG = 0;
	public static Handler handler = new Handler() {
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
	
	
	Runnable updateUnreadNum = new Runnable() {

		@Override
		public void run() {
			if (UNREAD_HEART){
				getUnreadMsgNum();
			}
			handler.postDelayed(this, GET_UNREAD_TIME);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_main);
		context = MainActivity.this;
		initView();
		initListener();
		initHeadText();
		initFragments();
		initFloatingActionMenu();
		// getUnreadMsgNum();

	}

	@Override
	protected void onResume() {
		updateView();
		super.onResume();
		getUnreadMsgNum();
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		MyPlace.start();
		UNREAD_HEART = true;
		super.onStart();
		handler.postDelayed(updateUnreadNum, GET_UNREAD_TIME);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		UNREAD_HEART =false;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyPlace.stop();
		handler.removeCallbacks(updateUnreadNum);
		super.onDestroy();
	}

	public static void getUnreadMsgNum() {
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int code = jsonObject.getInt("code");
					if (code == 1) {
						Log.e(TAG, "获取未读信息数目成功");
						int n = jsonObject.getInt("n");
						new_msg_num = n;
						setBadgeNum(n);
					} else {
						Log.e(TAG, "获取未读信息数目失败" + response);
						myToast("获取未读信息数目失败");
					}

				} catch (JSONException e) {
					Log.e(TAG, "获取未读信息数目失败" + response);
					myToast("获取未读信息数目失败");
					e.printStackTrace();
				} finally {

				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "获取未读信息数目失败" + error.toString());
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", MyApplication.getUser().getUsername());
		MyApplication.getMyVolley().addPostStringRequest(MyURL.GET_UNREAD_NUM,
				listener, errorListener, map, TAG);
	}

	public static void setBadgeNum(int n) {
		if (n == 0) {
			badgeView.setVisibility(View.GONE);
		}
		badgeView.setVisibility(View.VISIBLE);
		badgeView.setTargetView(civHeadPic);
		badgeView.setTypeface(Typeface.create(Typeface.SANS_SERIF,
				Typeface.ITALIC));
		badgeView.setShadowLayer(2, -1, -1, Color.RED);
		badgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
		badgeView.setBadgeCount(n);
	}

	private void initView() {
		badgeView = new BadgeView(context);

		civHeadPic = (CircleImageView) findViewById(R.id.civ_head_pic);
		tvUsername = (TextView) findViewById(R.id.tv_username);
		tvMood = (TextView) findViewById(R.id.tv_mood);
		tvMood.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		tvAddress = (TextView) findViewById(R.id.tv_address);
		tvNeighbor = (TextView) findViewById(R.id.bt_neighbor);
		tvMy = (TextView) findViewById(R.id.bt_my);
	}

	private void updateView() {
		ImageListener listener = ImageLoader.getImageListener(civHeadPic,
				R.drawable.head_cat, R.drawable.head_cat);
		String url = MyURL.GET_IMAGE_URL
				+ MyApplication.getUser().getHead_pic();
		MyApplication.getMyVolley().getImageLoader().get(url, listener);

		tvUsername.setText(MyApplication.getUser().getUsername());
		tvMood.setText(MyApplication.getUser().getMood());
		tvAddress.setText(MyPlace.getMyLocation().getAddrStr());
		tvAddress.requestFocus();
	}

	private void initHeadText() {
		ImageListener listener = ImageLoader.getImageListener(civHeadPic,
				R.drawable.head_cat, R.drawable.head_cat);
		String url = MyURL.GET_IMAGE_URL
				+ MyApplication.getUser().getHead_pic();
		MyApplication.getMyVolley().getImageLoader().get(url, listener);

		tvUsername.setText(MyApplication.getUser().getUsername());
		tvMood.setText(MyApplication.getUser().getMood());
		tvAddress.setText(MyPlace.getMyLocation().getAddrStr());
		tvAddress.requestFocus();
	}

	private void initListener() {
		civHeadPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						MessageActivity.class);
				startActivity(intent);
			}
		});
		tvUsername.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						MyInfoActivity.class);
				startActivity(intent);
			}
		});
		tvMy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.show(myShowsFragment);
				transaction.hide(neighborShowsFragment);
				tvMy.setBackgroundColor(Color.parseColor("#af175fa4"));
				tvNeighbor.setBackground(null);
				transaction.commit();
			}
		});
		tvNeighbor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.show(neighborShowsFragment);
				transaction.hide(myShowsFragment);
				tvMy.setBackground(null);
				tvNeighbor.setBackgroundColor(Color.parseColor("#af175fa4"));
				transaction.commit();
			}
		});
		tvAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, MapActivity.class);
				Bundle bundle = new Bundle();
				MyApplication.getMyPlace();
				bundle.putDouble("latitude", MyPlace.getMyLocation()
						.getLatitude());
				MyApplication.getMyPlace();
				bundle.putDouble("longitude", MyPlace.getMyLocation()
						.getLongitude());
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});
		tvMood.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final MaterialDialog dialog = new MaterialDialog(context);
				View view = LayoutInflater.from(context).inflate(
						R.layout.mood_dialog, null);
				final EditText etMood = (EditText) view
						.findViewById(R.id.et_mood);
				etMood.requestFocus();
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				dialog.setPositiveButton("   修改", new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String mood = etMood.getText().toString().trim();
						updateMood(mood);
						dialog.dismiss();
					}
				}).setNegativeButton("取消      ", new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				etMood.setText(MyApplication.getUser().getMood());
				etMood.setSelection(MyApplication.getUser().getMood().length());
				dialog.setView(view).show();
			}
		});
	}

	private void updateMood(final String mood) {
		Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject res = new JSONObject(response);
					// Log.e(TAG, res.toString());
					myToast(res.getString("msg"));
					if (res.getInt("code") == 1) {
						tvMood.setText(mood);
						MyApplication.getUser().setMood(mood);
						myToast(mood);
					}
				} catch (Exception e) {
					e.printStackTrace();
					myToast("更新心情失败，bug!请报告！");
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				myToast("更新心情失败，是不是网络出问题了呢？");
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", MyApplication.getUser().getUsername());
		map.put("mood", mood);
		MyApplication.getMyVolley().addPostStringRequest(MyURL.UPDATE_MOOD_URL,
				listener, errorListener, map, TAG);

	}

	private void initFragments() {
		fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		myShowsFragment = new MyShowsFragment();
		neighborShowsFragment = new NeighborShowsFragment();
		transaction.add(R.id.fl_content, myShowsFragment);
		transaction.add(R.id.fl_content, neighborShowsFragment);

		transaction.show(neighborShowsFragment);
		transaction.hide(myShowsFragment);
		tvMy.setBackground(null);
		tvNeighbor.setBackgroundColor(Color.parseColor("#af175fa4"));
		transaction.commit();
	}

	private void initFloatingActionMenu() {
		// in Activity Context
		ImageView icon = new ImageView(this); // Create an icon
		icon.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_action_new_light));
		FloatingActionButton actionButton = new FloatingActionButton.Builder(
				this).setContentView(icon)
				.setPosition(FloatingActionButton.POSITION_TOP_RIGHT).build();

		SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
		// repeat many times:
		ImageView itemIconSend = new ImageView(this);
		itemIconSend.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_action_new_light));
		SubActionButton btnSend = itemBuilder.setContentView(itemIconSend)
				.build();

		ImageView itemIconMap = new ImageView(this);
		itemIconMap.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_action_place_light));
		SubActionButton btnMap = itemBuilder.setContentView(itemIconMap)
				.build();

		ImageView itemIconFriend = new ImageView(this);
		itemIconFriend.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_action_friend));
		SubActionButton btnFriend = itemBuilder.setContentView(itemIconFriend)
				.build();

		ImageView itemIconHome = new ImageView(this);
		itemIconHome.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_action_home));
		SubActionButton btnHome = itemBuilder.setContentView(itemIconHome)
				.build();

		ImageView itemIconSetting = new ImageView(this);
		itemIconSetting.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_action_setting));
		SubActionButton btnSetting = itemBuilder
				.setContentView(itemIconSetting).build();

		FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
				.addSubActionView(btnSend).addSubActionView(btnHome)
				.addSubActionView(btnMap).addSubActionView(btnSetting)
				.attachTo(actionButton).setStartAngle(-170).setEndAngle(-280)
				.build();

		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						SendShowActivity.class);
				startActivity(intent);

			}
		});
		btnHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						MyInfoActivity.class);
				startActivity(intent);
			}
		});
		btnFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		btnMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, MapActivity.class);
				Bundle bundle = new Bundle();
				MyApplication.getMyPlace();
				bundle.putDouble("latitude", MyPlace.getMyLocation()
						.getLatitude());
				MyApplication.getMyPlace();
				bundle.putDouble("longitude", MyPlace.getMyLocation()
						.getLongitude());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		btnSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, SettingActivity.class);
				context.startActivity(intent);
			}
		});

	}
/*
	//返回键退出程序但不销毁，程序后台运行，同QQ退出处理方式
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	*/

	private long mExitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				// finish();
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
