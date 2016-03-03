package com.project.enjoyit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.enjoyit.adapter.MySelectImgGridViewAdapter;


import com.project.enjoyit.entity.Show;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;
import com.project.enjoyit.utils.ImageUtil;
import com.project.enjoyit.utils.MyPlace;
import com.project.enjoyit.utils.PictureUtil;
import com.project.enjoyit.view.RecordButton;
import com.project.enjoyit.view.RecordButton.OnFinishedRecordListener;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

import dmax.dialog.SpotsDialog;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.shapes.PathShape;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SendShowActivity extends Activity {
	private static final String TAG = "SendShowActivity";

	private TextView tvBack;
	private TextView tvSend;
	private EditText etContent;
	private RecordButton mRecordButton = null;
	private LinearLayout llCtrl;
	private TextView tvPlay;
	private TextView tvDelete;
	
	 private MediaPlayer mediaPlayer; 
	 
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private static ArrayList<String> bitmapPaths = new ArrayList<String>();
	private static ArrayList<String> bitmapFileNames = new ArrayList<String>();
	private String audioPath = "";
	private String audioName = "";
	private int is_anonymous = 0;
	
	private ToggleButton tbAnSwitch;
	private TextView tvAnText;
	
	private GridView gridView;
	private MySelectImgGridViewAdapter adapter;
	
	
	private static final int REQUEST_IMAGE = 0xff;
	private static Context context;
	private final static int CODE_TOAST_MSG = 0;
	public static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_TOAST_MSG:
				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				break;
			case ImageUtil.CODE_UPLOADED_IMAGES:
				String filename = msg.obj.toString();
				if (filename.equals("start")){
					
				}else{
					bitmapFileNames.add(filename);
					bitmapPaths.remove(0);
				}
				if (!bitmapPaths.isEmpty()){
//					Bitmap bitmap = BitmapFactory.decodeFile(bitmapPaths.get(0));
					
//					BitmapFactory.Options options = new BitmapFactory.Options();  
//					options.inSampleSize=2;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一  
//					options.inTempStorage = new byte[5*1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证）  
//					Bitmap bitmap = BitmapFactory.decodeFile(bitmapPaths.get(0), options);
					
					Bitmap bitmap = PictureUtil.getSmallBitmap(bitmapPaths.get(0));
					
					if (bitmap == null){
						return;
					}
					ImageUtil.uploadImg(context, bitmap, MyURL.UPLOAD_IMAGE_URL, handler);
				}else{
					Toast.makeText(context, "图片上传完毕", Toast.LENGTH_SHORT)
					.show();
				}
				break;
			case ImageUtil.CODE_UPLOAD_IMAGE_FAILED:
				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	public void myToast(String msg) {
		handler.obtainMessage(CODE_TOAST_MSG, msg).sendToTarget();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_send_shows);
		context = SendShowActivity.this;
		initView();
		initListener();
	}

	private void initListener() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		path += "/mmmm.amr";
		mRecordButton.setSavePath(path);
		mRecordButton
				.setOnFinishedRecordListener(new OnFinishedRecordListener() {

					@Override
					public void onFinishedRecord(String path) {
						audioPath = path;
						mRecordButton.setVisibility(View.GONE);
						llCtrl.setVisibility(View.VISIBLE);
						postAudio();
					}
				});

		tvBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SendShowActivity.this.finish();
			}
		});
		tvSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (bitmapFileNames.size() != bitmaps.size()){
					myToast("还有"+(bitmaps.size()-bitmapFileNames.size())+"张图片还没上传完毕，请稍候...");
					return;
				}
				if (etContent.getText().toString().isEmpty()){
					myToast("至少说点什么吧");
					return;
				}
				sendShow();
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos == 0) {
					if (bitmaps.size() > 12){
						myToast("不能选择更多了啦...");
						return;
					}
					Intent intent = new Intent(context,
							MultiImageSelectorActivity.class);
					// 是否显示调用相机拍照
					intent.putExtra(
							MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
					// 最大图片选择数量
					intent.putExtra(
							MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 8);
					// 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者
					// 多选/MultiImageSelectorActivity.MODE_MULTI)
					intent.putExtra(
							MultiImageSelectorActivity.EXTRA_SELECT_MODE,
							MultiImageSelectorActivity.MODE_MULTI);
					startActivityForResult(intent, REQUEST_IMAGE);
				} else {
					bitmaps.remove(pos - 1);
					adapter.notifyDataSetChanged();
					Log.e(TAG, "delete " + (pos-1) +" " + bitmapFileNames.get(pos-1));
					bitmapFileNames.remove(pos-1);
				}
			}
		});
		tvPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mediaPlayer.isPlaying()){
					mediaPlayer.stop();
					mediaPlayer.release();//释放资源
					tvPlay.setText("播放录音");
				}else if (!audioPath.isEmpty()){
					tvPlay.setText("暂停播放");
					playAudio();
				}
			}
		});
		tvDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				audioPath = "";
				mediaPlayer.stop();
				llCtrl.setVisibility(View.GONE);
				mRecordButton.setVisibility(View.VISIBLE);
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				tvPlay.setText("播放录音");
			}
		});
		tbAnSwitch.setOnToggleChanged(new OnToggleChanged(){
            @Override
            public void onToggle(boolean on) {
            	if (on){
            		is_anonymous = 1;
            		tvAnText.setText("匿名 开");
            	}else{
            		is_anonymous = 0;
            		tvAnText.setText("匿名 关");
            	}
            }
    });
	}

	

	protected void sendShow() {
		final AlertDialog loading = new SpotsDialog(context);
		loading.show();
		Listener<String> listener = new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.e(TAG, "上传成功"+response);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(response);
					if (jsonObject.getInt("code") == 1 && jsonObject.getInt("n") == 1){
						JSONObject jshow = jsonObject.getJSONObject("show");
						Show show = new Show();
						show.setShow_id(jshow.getInt("show_id"));
						show.setUsername(jshow.getString("username"));
						show.setHead_pic(jshow.getString("head_pic"));
						show.setShow_time(jshow.getString("show_time"));
						show.setContent(jshow.getString("content"));
						show.setImage_files(jshow.getString("images"));
						show.setAudio_file(jshow.getString("audios"));
						show.setIs_anonymous(jshow.getInt("is_anonymous"));
						show.setAddress(jshow.getString("address"));
						show.setLatitude(jshow.getInt("latitude"));
						show.setLongitude(jshow.getInt("longitude"));
						show.setLikes(jshow.getInt("likes"));
						
						MyShowsFragment.addShowToHead(show);
						NeighborShowsFragment.addShowToHead(show);
						myToast("发表成功！");
						bitmapFileNames.clear();
						finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(TAG, "报告！有bug！");
					myToast("报告！有bug！");
				}finally{
					loading.dismiss();
				}
				
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "上传失败"+error.toString());
				myToast("报告！上传失败！请检查网络连接...");
				loading.dismiss();
			}
		};
		String images = "";
		if (!bitmapFileNames.isEmpty()){
			images = bitmapFileNames.get(0);
		}
		for (int i=1; i<bitmapFileNames.size(); ++i){
			images += "|"+bitmapFileNames.get(i);
		}
		if (images == null){
			myToast("images null");
		}
		if (audioName == null){
			myToast("audio null");
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", MyApplication.getUser().getUsername());
		map.put("content", etContent.getText().toString().trim());
		map.put("images", images);
		map.put("audios", audioName);
		map.put("is_anonymous", is_anonymous+"");
		map.put("address", MyPlace.getMyLocation().getAddrStr());
		MyApplication.getMyVolley().addPostStringRequest(MyURL.POST_SHOW, listener, errorListener, map, TAG);
	}

	protected void playAudio() {
		mediaPlayer.reset();  
        try {
			mediaPlayer.setDataSource(audioPath);
			mediaPlayer.prepare();  
	        mediaPlayer.start(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			myToast("播放失败");
		}
	}

	private void initView() {
		mRecordButton = (RecordButton) findViewById(R.id.record_button);
		tvSend = (TextView) findViewById(R.id.tv_send);
		tvBack = (TextView) findViewById(R.id.tv_back);
		etContent = (EditText) findViewById(R.id.et_content);
		gridView = (GridView) findViewById(R.id.gv_gridview);
		adapter = new MySelectImgGridViewAdapter(SendShowActivity.this, bitmaps);
		gridView.setAdapter(adapter);
		llCtrl = (LinearLayout)findViewById(R.id.ll_ctrl);
		tvPlay = (TextView)findViewById(R.id.tv_play_pause);
		tvDelete = (TextView)findViewById(R.id.tv_delete);
		
		mediaPlayer = new MediaPlayer(); 
		tbAnSwitch = (ToggleButton)findViewById(R.id.tb_answitch);
		tvAnText = (TextView)findViewById(R.id.tv_antext);
		is_anonymous = 0;
		tvAnText.setText("匿名 关");
		tbAnSwitch.setToggleOff();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE) {
			if (resultCode == RESULT_OK) {
				// 获取返回的图片列表
				ArrayList<String> paths = data
						.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				// 处理你自己的逻辑 ....
				for (int i=0; i<paths.size(); ++i){
					bitmapPaths.add(paths.get(i));
				}
				if (paths==null || paths.isEmpty()){
					return;
				}
				handler.obtainMessage(ImageUtil.CODE_UPLOADED_IMAGES, "start").sendToTarget();
				for (String p : paths) {
//					Bitmap bitmap = BitmapFactory.decodeFile(p);
					
//					BitmapFactory.Options options = new BitmapFactory.Options();  
//					options.inSampleSize=2;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一  
//					options.inTempStorage = new byte[5*1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证）  
//					Bitmap bitmap = BitmapFactory.decodeFile(p, options);
					
					Bitmap bitmap = PictureUtil.getSmallBitmap(p);
					
					bitmaps.add(bitmap);
				}
				adapter.notifyDataSetChanged();
				
			}
		}
	}
	protected void postAudio() {
		//异步的客户端对象  
        AsyncHttpClient client = new AsyncHttpClient();  
        //指定url路径  
        String url = MyURL.UPLOAD_AUDIO_URL;  
        //封装文件上传的参数  
        RequestParams params = new RequestParams();  
        //根据路径创建文件  
        File file = new File(audioPath);  
        try {  
            //放入文件  
            params.put("file", file);  
            params.put("name", "file");
        } catch (Exception e) {  
            Log.e(TAG, "文件不存在"); 
            myToast("是不是没有SD卡呢？");
        }  
        //执行post请求  
        client.post(url,params, new AsyncHttpResponseHandler() {

        	@Override  
            public void onSuccess(int statusCode, Header[] headers,  
                    byte[] responseBody) {  
                // 上传成功后要做的工作  
                if (statusCode == 200){
                	try {
						JSONObject jsonObject = new JSONObject(new String(responseBody).trim());
						if(jsonObject.getInt("code") == 1){
							audioName = jsonObject.getString("filename");
							Log.e(TAG, "上传音频成功" + audioName);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e(TAG, "上传音频错误"+new String(responseBody));
						myToast("上传音频错误");
					}
                }
            }  
      
            @Override  
            public void onFailure(int statusCode, Header[] headers,  
                    byte[] responseBody, Throwable error) {  
                // 上传失败后要做到工作  
            	myToast("上传音频错误");
            }  
      
            @Override  
            public void onProgress(int bytesWritten, int totalSize) {  
                // TODO Auto-generated method stub  
                super.onProgress(bytesWritten, totalSize);  
                int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
                // 上传进度显示   
            }  
      
            @Override  
            public void onRetry(int retryNo) {  
                // TODO Auto-generated method stub  
                super.onRetry(retryNo);  
                // 返回重试次数  
            }  
        });  
	}
}
