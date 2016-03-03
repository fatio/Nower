package com.project.enjoyit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import me.drakeet.materialdialog.MaterialDialog;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.project.enjoyit.entity.User;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;
import com.project.enjoyit.utils.ImageUtil;
import com.project.enjoyit.utils.MyNetworkUtil;
import com.project.enjoyit.utils.PictureUtil;
import com.project.enjoyit.view.SelectPicPopupWindow;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyInfoActivity extends Activity implements OnClickListener {
	private static String TAG = "UserInfoActivity";

	private CircleImageView civHeadPic;
	private TextView tvName;
	private TextView tvMood;
	private TextView tvSex;
	private TextView tvAge;
	private TextView tvRegTime;
	private TextView tvPhone;

	private Button btSure;
	private User user;

	private static String head_pic;
	private String phone;
	private String mood;
	private int sex;
	private int age;

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
			case ImageUtil.CODE_UPLOADED_IMAGES:
				head_pic = msg.obj.toString();
				uploading.dismiss();
				Toast.makeText(context, "头像上传成功", Toast.LENGTH_SHORT).show();
				break;
			case ImageUtil.CODE_UPLOAD_IMAGE_FAILED:
				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				uploading.dismiss();
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
		setContentView(R.layout.activity_my_info);

		user = MyApplication.getUser();

		initNewData();
		initView();
		context = MyInfoActivity.this;
		updateView();
		initListener();
	}

	private void initNewData() {
		head_pic = user.getHead_pic();
		phone = user.getPhone();
		mood = user.getMood();
		sex = user.getSex();
		age = user.getAge();
	}

	private void initListener() {
		civHeadPic.setOnClickListener(this);
		tvPhone.setOnClickListener(this);
		tvMood.setOnClickListener(this);
		tvSex.setOnClickListener(this);
		tvAge.setOnClickListener(this);
		btSure.setOnClickListener(this);
	}

	private void initView() {
		civHeadPic = (CircleImageView) findViewById(R.id.civ_head_pic);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvMood = (TextView) findViewById(R.id.tv_mood);
		tvSex = (TextView) findViewById(R.id.tv_sex);
		tvAge = (TextView) findViewById(R.id.tv_age);
		tvRegTime = (TextView) findViewById(R.id.tv_reg_time);
		btSure = (Button) findViewById(R.id.bt_sure);
		tvPhone = (TextView) findViewById(R.id.tv_phone);
	}

	protected void updateView() {

		ImageListener listener = ImageLoader.getImageListener(civHeadPic,
				R.drawable.head_cat, R.drawable.head_cat);
		String url = MyURL.GET_IMAGE_URL + head_pic;
		MyApplication.getMyVolley().getImageLoader().get(url, listener);

		tvName.setText("我是 " + user.getUsername());
		tvMood.setText(mood);
		String tmp = sex == 1 ? "男" : (sex == 0 ? "女" : "男 /女");
		tvSex.setText("我是" + tmp + "的");
		tvAge.setText("我今年" + age + "岁");
		tvRegTime.setText("我是从" + user.getReg_time1() + "开始在这里玩的");
		tvPhone.setText("我的电话是" + phone);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.civ_head_pic:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			// imm.showSoftInput(etUsername, InputMethodManager.SHOW_FORCED);
			imm.hideSoftInputFromWindow(tvName.getWindowToken(), 0); // 强制隐藏键盘
			uploading = new SpotsDialog(context);
			initMenuWindow();
			break;
		case R.id.tv_phone:
			updatePhone();
			break;
		case R.id.tv_mood:
			updateMood();
			break;
		case R.id.tv_sex:
			updateSex();
			break;
		case R.id.tv_age:
			updateAge();
			break;
		case R.id.bt_sure:
			sure();
			break;
		default:
			break;
		}
	}

	private void sure() {
		final AlertDialog loading = new SpotsDialog(context);
		loading.show();

		Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject res = new JSONObject(response);
					// Log.e(TAG, res.toString());
					if (res.getInt("code") == 1) {
						myToast("修改成功！");
						MyApplication.getUser().updateFromNetwork();
					}
				} catch (Exception e) {
					e.printStackTrace();
					myToast("修改失败，bug!请报告！");
				} finally {
					loading.dismiss();
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				myToast("修改失败，是不是网络出问题了呢？");
				loading.dismiss();
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", user.getUsername());
		map.put("mood", mood);
		map.put("phone", phone);
		map.put("head_pic", head_pic);
		map.put("sex", sex + "");
		map.put("age", age + "");
		map.put("state", 1 + "");
		MyApplication.getMyVolley().addPostStringRequest(
				MyURL.UPDATE_USER_INFO_URL, listener, errorListener, map, TAG);
	}

	private int tn = 0;

	private void updateSex() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MyInfoActivity.this);
		builder.setTitle("修改特征");
		// builder.setIcon(android.R.drawable.ic_dialog_info);
		tn = 0;
		builder.setSingleChoiceItems(new String[] { "女", "男", "你猜" }, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int pos) {
						tn = pos;
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int pos) {
				sex = tn;
				Log.e(TAG, pos + "");
				updateView();
			}
		});
		builder.setNegativeButton("取消", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void updateAge() {
		final MaterialDialog dialog = new MaterialDialog(context);
		View view = LayoutInflater.from(context).inflate(R.layout.num_dialog,
				null);
		TextView etTitle = (TextView) view.findViewById(R.id.tv_title);
		etTitle.setText("修改年龄");
		final EditText etText = (EditText) view.findViewById(R.id.et_text);
		etText.setText(user.getAge() + "");
		etText.setSelection(etText.getText().toString().length());
		etText.requestFocus();
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		dialog.setPositiveButton("   修改", new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String text = etText.getText().toString().trim();
				if (text.isEmpty()) {
					myToast("空了！空了！");
					return;
				}
				int t = Integer.parseInt(text);
				if (t >= 200) {
					myToast("别动！留下电话！我发现了仙人了！");
					return;
				}
				if (t < 5) {
					myToast("别动！留下电话！我发现了神童了！");
				}
				age = t;
				updateView();
				dialog.dismiss();
			}
		}).setNegativeButton("取消      ", new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setView(view).show();
	}

	private void updateMood() {
		final MaterialDialog dialog = new MaterialDialog(context);
		View view = LayoutInflater.from(context).inflate(R.layout.text_dialog,
				null);
		TextView etTitle = (TextView) view.findViewById(R.id.tv_title);
		etTitle.setText("修改心情");
		final EditText etText = (EditText) view.findViewById(R.id.et_text);
		etText.setText(user.getMood());
		etText.setSelection(user.getMood().length());
		etText.requestFocus();
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		dialog.setPositiveButton("   修改", new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String text = etText.getText().toString().trim();
				if (text.isEmpty()) {
					myToast("空了！空了！");
					return;
				}
				mood = text;
				updateView();
				dialog.dismiss();
			}
		}).setNegativeButton("取消      ", new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setView(view).show();
	}

	private void updatePhone() {
		final MaterialDialog dialog = new MaterialDialog(context);
		View view = LayoutInflater.from(context).inflate(R.layout.text_dialog,
				null);
		TextView etTitle = (TextView) view.findViewById(R.id.tv_title);
		etTitle.setText("修改电话");
		final EditText etText = (EditText) view.findViewById(R.id.et_text);
		etText.setText(user.getPhone());
		etText.setSelection(user.getPhone().length());
		etText.setInputType(InputType.TYPE_CLASS_PHONE);
		etText.requestFocus();
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		dialog.setPositiveButton("   修改", new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String text = etText.getText().toString().trim();
				if (text.isEmpty()) {
					myToast("空了！空了！");
					return;
				}
				phone = text;
				updateView();
				dialog.dismiss();
			}
		}).setNegativeButton("取消      ", new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setView(view).show();
	}

	private static AlertDialog uploading;
	// 自定义的弹出框类
	SelectPicPopupWindow menuWindow;
	/* 头像文件 */
	private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
	/* 请求识别码 */
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private static final int CODE_CAMERA_REQUEST = 0xa1;
	private static final int CODE_RESULT_REQUEST = 0xa2;
	// 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
	private static int output_X = 160;
	private static int output_Y = 160;
//	private static String head_pic_name = "";

	protected void initMenuWindow() {
		// 实例化SelectPicPopupWindow
		menuWindow = new SelectPicPopupWindow(MyInfoActivity.this, itemsOnClick);
		// 显示窗口
		menuWindow.showAtLocation(
				MyInfoActivity.this.findViewById(R.id.bt_sure),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
		// 为弹出窗口实现监听类
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				choseHeadImageFromCameraCapture();
				break;
			case R.id.btn_pick_photo:
				choseHeadImageFromGallery();
				break;
			default:
				break;
			}

		}

	};

	// 从本地相册选取图片作为头像
	private void choseHeadImageFromGallery() {
		Intent intentFromGallery = new Intent();
		// 设置文件类型
		intentFromGallery.setType("image/*");
		intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
	}

	// 启动手机相机拍摄照片作为头像
	private void choseHeadImageFromCameraCapture() {
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// 判断存储卡是否可用，存储照片文件
		if (hasSdcard()) {
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
		}

		startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		// 用户没有进行有效的设置操作，返回
		if (resultCode == RESULT_CANCELED) {
			// Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
			return;
		}

		switch (requestCode) {
		case CODE_GALLERY_REQUEST:
			cropRawPhoto(intent.getData());
			break;

		case CODE_CAMERA_REQUEST:
			if (hasSdcard()) {
				File tempFile = new File(
						Environment.getExternalStorageDirectory(),
						IMAGE_FILE_NAME);
				Bitmap bitmap = PictureUtil.getSmallBitmap(tempFile
						.getAbsolutePath());

				Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
						getContentResolver(), bitmap, null, null));
				cropRawPhoto(uri);
				// cropRawPhoto(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(getApplication(), "没有SDCard!",
						Toast.LENGTH_SHORT).show();
			}

			break;

		case CODE_RESULT_REQUEST:
			if (intent != null) {
				setImageToHeadView(intent);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	/**
	 * 裁剪原始的图片
	 */
	public void cropRawPhoto(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");

		// 设置裁剪
		intent.putExtra("crop", "true");

		// aspectX , aspectY :宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX , outputY : 裁剪图片宽高
		intent.putExtra("outputX", output_X);
		intent.putExtra("outputY", output_Y);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, CODE_RESULT_REQUEST);
	}

	/**
	 * 提取保存裁剪之后的图片数据，并设置头像部分的View
	 */
	private void setImageToHeadView(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			civHeadPic.setImageBitmap(photo);
			uploading.show();
			upload_head_pic(photo);
		}
	}

	private void upload_head_pic(Bitmap bitmap) {
		ImageUtil.uploadImg(MyInfoActivity.this, bitmap,
				MyURL.UPLOAD_IMAGE_URL, handler);
	}

	/**
	 * 检查设备是否存在SDCard的工具方法
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// 有存储的SDCard
			return true;
		} else {
			return false;
		}
	}

}
