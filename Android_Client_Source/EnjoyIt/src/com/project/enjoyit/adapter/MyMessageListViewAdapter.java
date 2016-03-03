package com.project.enjoyit.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import me.drakeet.materialdialog.MaterialDialog;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.jauker.widget.BadgeView;
import com.project.enjoyit.MainActivity;
import com.project.enjoyit.MessageActivity;
import com.project.enjoyit.R;
import com.project.enjoyit.ShowActivity;
import com.project.enjoyit.UserInfoActivity;
import com.project.enjoyit.adapter.MyShowsListViewAdapter.ViewHolder;
import com.project.enjoyit.entity.Comment;
import com.project.enjoyit.entity.Message;
import com.project.enjoyit.entity.Show;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;

import dmax.dialog.SpotsDialog;
import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.NoCopySpan.Concrete;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyMessageListViewAdapter extends BaseAdapter {
	private static final String TAG = "MyMessageListViewAdapter";

	private ArrayList<Message> messages;
	private Context context;

	public MyMessageListViewAdapter(Context context,
			ArrayList<Message> messages) {
		this.messages = messages;
		this.context = context;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int arg0) {
		return messages.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.message_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.tvMsg = (TextView) convertView
					.findViewById(R.id.tv_msg);
			viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tv_time);
			viewHolder.badgeView = new BadgeView(context);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Message message = messages.get(position);
		final int n = message.getMsg_type();
		String msg = message.getMessage();
		if (n==0){
			viewHolder.tvMsg.setText("ϵͳ��Ϣ��"+msg);
		}else if (n==1){
			viewHolder.tvMsg.setText(msg);
		}else if (n==2){
			viewHolder.tvMsg.setText(msg);
		}
		
		viewHolder.tvTime.setText(message.getSend_time1());
		
		if (message.getHas_read() == 0){
			viewHolder.badgeView.setVisibility(View.VISIBLE);
			viewHolder.badgeView.setTargetView(viewHolder.tvMsg);  
			viewHolder.badgeView.setTypeface(Typeface.create(Typeface.SANS_SERIF,  
	                Typeface.ITALIC));  
			viewHolder.badgeView.setShadowLayer(2, -1, -1, Color.RED);
			viewHolder.badgeView.setBadgeGravity(Gravity.CENTER | Gravity.RIGHT);  
//			viewHolder.badgeView.setTextColor(Color.RED);
			viewHolder.badgeView.setBadgeCount(1); 
		}else{
			viewHolder.badgeView.setVisibility(View.GONE);
		}
		viewHolder.tvMsg.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				if (message.getHas_read()==0){
					setMessageRead(position);
				}
				final MaterialDialog dialog = new MaterialDialog(context);
				dialog.setMessage("ȷ��Ҫɾ��������Ϣ��");
				dialog.setPositiveButton("ȷ��", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						delMsg(message.getMessage_id(), position);
						dialog.dismiss();
					}
				}).setNegativeButton("ȡ��", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.show();
				return true;
			}
		});
		viewHolder.tvMsg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setMessageRead(position);
				if (n==0){
					final MaterialDialog dialog = new MaterialDialog(context);
					dialog.setMessage(message.getMessage());
					dialog.setPositiveButton("ȷ��", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							dialog.dismiss();
						}
					}).show();
				}else if(n==1){
					int show_id = Integer.parseInt(message.getMsg_obj());
					Intent intent = new Intent(context, ShowActivity.class);
					intent.putExtra("show_id", show_id);
					context.startActivity(intent);
				}else if (n==2){
					int show_id = Integer.parseInt(message.getMsg_obj());
					Intent intent = new Intent(context, ShowActivity.class);
					intent.putExtra("show_id", show_id);
					context.startActivity(intent);
				}
				
			}
		});

		return convertView;
	}
	private void myToast(String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	protected void delMsg(int message_id, final int position) {
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int code = jsonObject.getInt("code");
					if (code == 1){
						Log.e(TAG, "ɾ����Ϣ�ɹ�");
						myToast("ɾ����Ϣ�ɹ�");
						messages.remove(position);
						MyMessageListViewAdapter.this.notifyDataSetChanged();
					}else{
						Log.e(TAG, "ɾ����Ϣʧ��"+response);
						myToast("ɾ����Ϣʧ��");
					}
					
				} catch (JSONException e) {
					Log.e(TAG, "ɾ����Ϣʧ��"+response);
					myToast("ɾ����Ϣʧ��");
					e.printStackTrace();
				}finally{
					
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "ɾ����Ϣʧ��"+error.toString());
				myToast("ɾ����Ϣʧ��");
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", MyApplication.getUser().getUsername());
		map.put("message_id", message_id+"");
		MyApplication.getMyVolley().addPostStringRequest(MyURL.DEL_MSG, listener, errorListener, map, TAG);
	}

	protected void setMessageRead(final int position) {
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int code = jsonObject.getInt("code");
					if (code == 1){
						Log.e(TAG, "������Ϣ�Ѷ��ɹ�");
//						messages.get(position).setHas_read(1);
//						MyMessageListViewAdapter.this.notifyDataSetChanged();
						MessageActivity.setRead(position);
						
//						MessageActivity.subLastId();
					}else{
						Log.e(TAG, "������Ϣ�Ѷ�ʧ��"+response);
					}
					
				} catch (JSONException e) {
					Log.e(TAG, "������Ϣ�Ѷ�ʧ��"+response);
					e.printStackTrace();
				}finally{
					
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "������Ϣ�Ѷ�ʧ��"+error.toString());
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", MyApplication.getUser().getUsername());
		map.put("message_id", messages.get(position).getMessage_id()+"");
		MyApplication.getMyVolley().addPostStringRequest(MyURL.SER_MSG_READ, listener, errorListener, map, TAG);
	
		
	}

	public final class ViewHolder {
		public TextView tvMsg;
		private TextView tvTime;
		private BadgeView badgeView;
	}
}