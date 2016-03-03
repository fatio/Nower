package com.project.enjoyit;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.project.enjoyit.adapter.MyShowsListViewAdapter;
import com.project.enjoyit.entity.Show;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;
import com.project.enjoyit.utils.MyPlace;
import com.project.enjoyit.view.MyScrollListView;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView.FindListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class MyShowsFragment extends Fragment {
	private static final String TAG = "MyShowsFragment";
	private View view;

	private static PullToRefreshListView myListView;
	private static MyShowsListViewAdapter adapter;
	private static ArrayList<Show> shows = new ArrayList<Show>();

	private static int page_start = 0;
	private static final int page_num = 5;

	private final static int CODE_TOAST_MSG = 0;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_TOAST_MSG:
				Toast.makeText(getActivity(), msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	public void myToast(String msg) {
		handler.obtainMessage(CODE_TOAST_MSG, msg).sendToTarget();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_my_shows, container, false);
		initView();
		return view;
	}

	private void initView() {
		myListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);

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
				shows.clear();
				page_start = 0;
				addShows();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				Log.e(TAG, "上拉加载");
				addShows();
			}

		});

		shows.clear();
		page_start = 0;
		addShows();
		adapter = new MyShowsListViewAdapter(getActivity(), shows);
		myListView.setAdapter(adapter);
	}

	private void addShows() {
		Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					Log.e(TAG, jsonObject.getString("msg"));
					if (jsonObject.getInt("code") == 1) {
						Log.e(TAG, "my shows获取成功");
						JSONArray jsonArray = jsonObject.getJSONArray("data");

						addShowsFromJSONArray(jsonArray);

						if (jsonArray.length() == 0) {
							myToast("没有更多的了...");
						}
					} else {
						Log.e(TAG, "my shows获取失败：" + response);
						myToast("已没有更多数据");
					}
				} catch (JSONException e) {
					Log.e(TAG, "my shows json bug：" + response);
					e.printStackTrace();
				} finally {
					new FinishRefresh().execute();
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				new FinishRefresh().execute();
				myToast("无法获取数据，请检查网络连接");
			}
		};
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		int show_id = 0xfffff;
		if (!shows.isEmpty()) {
			show_id = shows.get(shows.size() - 1).getShow_id();
		}
		map.put("show_id", show_id + "");
		map.put("start", page_start + "");
		map.put("num", page_num + "");

		map.put("username", MyApplication.getUser().getUsername());
		String url = MyURL.GET_MY_SHOWS_URL;
//		 map.put("address", MyPlace.getMyLocation().getAddrStr());
//		 String url = MyURL.GET_NEIGHBOR_SHOWS_URL;
		MyApplication.getMyVolley().addPostStringRequest(url, listener,
				errorListener, map, TAG);
	}

	private void addShowsFromJSONArray(JSONArray jsonArray)
			throws JSONException {
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jshow = jsonArray.getJSONObject(i);
			Show show = new Show();
			show.updateFromJSONObject(jshow);
			shows.add(show);
		}
		page_start += jsonArray.length();
		adapter.notifyDataSetChanged();
	}
	
	public static void addShowToHead(Show show){
		shows.add(0, show);
		adapter.notifyDataSetChanged();
	}
	public static void delShow(Show show){
		shows.remove(show);
		adapter.notifyDataSetChanged();
	}
	public static void flushDatas() {
		adapter.notifyDataSetChanged();
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
