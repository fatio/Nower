package com.project.enjoyit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.project.enjoyit.adapter.MyMessageListViewAdapter;
import com.project.enjoyit.entity.Message;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;
import android.widget.Toast;

public class MessageActivity extends Activity {
	private static final String TAG = "MessageActivity";

	private static PullToRefreshListView myListView;
	private static ArrayList<Message> messages = new ArrayList<Message>();
	private static MyMessageListViewAdapter adapter;

	private static int last_id=9999;
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

	public static void subLastId(){
		last_id -= 1;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_message);
		context = MessageActivity.this;
		initView();
		initListView();
	}

	private void initListView() {
		myListView = (PullToRefreshListView) findViewById(R.id.listview);
		adapter = new MyMessageListViewAdapter(context, messages);
		myListView.setAdapter(adapter);
		getUnreadMsg();

		AnimationSet set = new AnimationSet(false);
		Animation animation = new AlphaAnimation(0, 1); // AlphaAnimation
														// 控制渐变透明的动画效果
		animation.setDuration(500); // 动画时间毫秒数
		set.addAnimation(animation); // 加入动画集合
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 1);
		myListView.setLayoutAnimation(controller); // ListView 设置动画效果

		myListView.setMode(Mode.BOTH);
		myListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				Log.e(TAG, "下拉刷新");
				getUnreadMsg();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				Log.e(TAG, "上拉加载");
				getReadMsg();
			}

		});
	}

	protected void getReadMsg() {
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int code = jsonObject.getInt("code");
					if (code == 1) {
						messages.clear();
						Log.e(TAG, "获取已读信息成功");
						int n = jsonObject.getInt("n");
						if (n == 0) {
							myToast("没有信息哦");
							adapter.notifyDataSetChanged();
							return;
						}
						JSONArray jsonArray = jsonObject.getJSONArray("msgs");
						if (jsonArray.length()!=0){
							last_id += jsonArray.getJSONObject(jsonArray.length()-1).getInt("message_id");
						}
						updateMessages(jsonArray);

					} else {
						Log.e(TAG, "获取已读信息失败" + response);
						myToast("获取信息失败");
					}

				} catch (JSONException e) {
					Log.e(TAG, "获取信息失败" + response);
					myToast("获取信息失败");
					e.printStackTrace();
				} finally {
					new FinishRefresh().execute();
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "获取信息失败" + error.toString());
				myToast("获取信息失败，请检查网络连接");
				new FinishRefresh().execute();
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", MyApplication.getUser().getUsername());
		map.put("last_id", last_id+"");
		map.put("num", 10+"");
		MyApplication.getMyVolley().addPostStringRequest(MyURL.GET_READ_MSG,
				listener, errorListener, map, TAG);

	}

	private void getUnreadMsg() {
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int code = jsonObject.getInt("code");
					messages.clear();
					if (code == 1) {
						Log.e(TAG, "获取未读信息成功");
//						messages.clear();
						int n = jsonObject.getInt("n");
						if (n == 0) {
							myToast("没有新的未读信息了");
							adapter.notifyDataSetChanged();
							getReadMsg();
							return;
						}
//						myToast("获取" + n + "条未读信息成功");
						updateMessages(jsonObject.getJSONArray("msgs"));

					} else {
						Log.e(TAG, "获取未读信息失败" + response);
						myToast("获取未读信息失败");
					}

				} catch (JSONException e) {
					Log.e(TAG, "获取未读信息失败" + response);
					myToast("获取未读信息失败");
					e.printStackTrace();
				} finally {
					new FinishRefresh().execute();
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "获取未读信息失败" + error.toString());
				myToast("获取未读信息失败，请检查网络连接");
				new FinishRefresh().execute();
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", MyApplication.getUser().getUsername());
		MyApplication.getMyVolley().addPostStringRequest(MyURL.GET_UNREAD_MSG,
				listener, errorListener, map, TAG);

	}

	public static void setRead(int position) {
		messages.get(position).setHas_read(1);
		adapter.notifyDataSetChanged();
	}

	protected void updateMessages(JSONArray jsonArray) throws JSONException {
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Message message = new Message();
			message.updateFromJSONObject(jsonObject);
			messages.add(message);
		}
		adapter.notifyDataSetChanged();
		new FinishRefresh().execute();
	}

	private void initView() {

	}

	private class FinishRefresh extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			myListView.onRefreshComplete();

		}
	}
}
