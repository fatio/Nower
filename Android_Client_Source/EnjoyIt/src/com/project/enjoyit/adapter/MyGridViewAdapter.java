package com.project.enjoyit.adapter;

import com.android.volley.toolbox.NetworkImageView;
import com.project.enjoyit.R;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyGridViewAdapter extends BaseAdapter {
	private Context context;
	private String[] images;
	private LayoutInflater inflater;
	
	public MyGridViewAdapter(Context context, String imageNames) {
		this.context = context;
		images = imageNames.split("\\|");
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return images.length;
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.gridview_newtworkimageview_item,
					parent, false);
			holder = new ViewHolder();
			holder.image = (NetworkImageView) convertView
					.findViewById(R.id.niv_imageview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String url = MyURL.GET_IMAGE_URL + images[position];

		holder.image.setTag(url);
		
		if (images.length <= position) {
			holder.image.setVisibility(View.GONE);
		}else{
			holder.image.setVisibility(View.VISIBLE);
			if (url!=null && !url.isEmpty()){
				holder.image.setImageUrl(url, MyApplication.getMyVolley().getImageLoader());
			}
			
		}
		
		
		return convertView;
	}
	public class ViewHolder {
		public NetworkImageView image;
	}
}
