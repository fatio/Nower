package com.project.enjoyit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends Activity {
	private static MapView mMapView = null;
	private static BaiduMap mBaiduMap;
	private double latitude = 0;
	private double longtitude = 0;
	private LocationClient mLocClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		setContentView(R.layout.activity_map);
		// ��ȡ��ͼ�ؼ�����
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		mMapView.removeViewAt(1); // ȥ���ٶ�logo
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);// 17 100m
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, null));

	}



	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Bundle bundle = this.getIntent().getExtras();
		latitude = bundle.getDouble("latitude");
		longtitude = bundle.getDouble("longitude");
		Log.e("MAP", latitude + " " + longtitude);
		centerToMyLocation();
	}

	/**
	 * ���ö�λ�㲢�Ƶ�����
	 */
	private void centerToMyLocation() {
		LatLng latLng1 = new LatLng(latitude, longtitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng1);
		mBaiduMap.animateMapStatus(msu);// �������ĵ�
		// ����Maker�����
		// ����Markerͼ��
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.ic_map);
		// ����MarkerOption�������ڵ�ͼ������Marker
		OverlayOptions option = new MarkerOptions().position(latLng1).icon(
				bitmap);
		// �ڵ�ͼ������Marker������ʾ
		mBaiduMap.addOverlay(option);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}