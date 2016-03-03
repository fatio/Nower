package com.project.enjoyit.adapter;

import java.util.ArrayList;

import com.project.enjoyit.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MySelectImgGridViewAdapter extends BaseAdapter {
	private ArrayList<Bitmap> bitmaps;
	private Context context;
	
	public MySelectImgGridViewAdapter(Context context, ArrayList<Bitmap> bitmaps) {
		this.context = context;
		this.bitmaps = bitmaps;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bitmaps.size()+1;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.gridview_imageview_item, parent, false);
			viewHolder.imageView = (ImageView)convertView.findViewById(R.id.iv_imageview);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		if (position == 0){
			//viewHolder.imageView.setVisibility(View.VISIBLE);
			viewHolder.imageView.setImageResource(R.drawable.icon_addpic_unfocused);
		}else if (position <= bitmaps.size()){
			//viewHolder.imageView.setVisibility(View.VISIBLE);
			viewHolder.imageView.setImageBitmap(bitmaps.get(position-1));
		}else{
			//viewHolder.imageView.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	public final class ViewHolder{
		public ImageView imageView;
	}
}

