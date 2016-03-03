package com.project.enjoyit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

import com.android.volley.toolbox.NetworkImageView;

import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;

public class ImageActivity extends Activity {
	private NetworkImageView ivImage;
	private String[] imgnames;
	private int pos = 0;
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// È¥µô±êÌâÀ¸
		setContentView(R.layout.activity_image);
		ivImage = (NetworkImageView) findViewById(R.id.iv_image);

		// gestureDetector = new
		// GestureDetector(ImageActivity.this,onGestureListener);

		Intent intent = getIntent();
		if (intent != null) {
			String imgname = intent.getStringExtra("imgnames");
			pos = intent.getIntExtra("pos", 0);
			imgnames = imgname.split("\\|");
			if (pos >= imgnames.length) {
				pos = 0;
			}
			// String url = MyURL.GET_IMAGE_URL + imgnames[pos];
			// ivImage.setImageUrl(url,
			// MyApplication.getMyVolley().getImageLoader());
			initViewer();
		}

	}

	private void initViewer() {
		// String[] urls = {
		// "http://cs407831.userapi.com/v407831207/18f6/jBaVZFDhXRA.jpg",
		// "http://cs407831.userapi.com/v4078f31207/18fe/4Tz8av5Hlvo.jpg",
		// //special url with error
		// "http://cs407831.userapi.com/v407831207/1906/oxoP6URjFtA.jpg",
		// "http://cs407831.userapi.com/v407831207/190e/2Sz9A774hUc.jpg",
		// "http://cs407831.userapi.com/v407831207/1916/Ua52RjnKqjk.jpg",
		// "http://cs407831.userapi.com/v407831207/191e/QEQE83Ok0lQ.jpg"
		// };
		// List<String> items = new ArrayList<String>();
		// Collections.addAll(items, urls);
		ArrayList<String> items = new ArrayList<String>();
		for (String img : imgnames){
			String url = MyURL.GET_IMAGE_URL + img;
			items.add(url);
		}
		UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);
		GalleryViewPager mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(pos, true);

	}

	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();

			if (x > 0) { // RIGHT
				if (pos == 0) {
					pos = imgnames.length - 1;
				} else {
					pos--;
				}
			} else if (x < 0) { // LEFT
				if (pos == imgnames.length - 1) {
					pos = 0;
				} else {
					pos++;
				}

			}
			String url = MyURL.GET_IMAGE_URL + imgnames[pos];
			ivImage.setImageUrl(url, MyApplication.getMyVolley()
					.getImageLoader());
			return true;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image, menu);
		return true;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

}
