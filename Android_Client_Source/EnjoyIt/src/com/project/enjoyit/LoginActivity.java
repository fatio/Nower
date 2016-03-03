package com.project.enjoyit;

import java.util.HashMap;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.keyczar.Crypter;
import org.keyczar.Encrypter;
import org.keyczar.exceptions.KeyczarException;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.example.android.keyczardemo.AndroidKeyczarReader;
import com.project.enjoyit.global.MyAlgorithm;
import com.project.enjoyit.global.MyApplication;
import com.project.enjoyit.global.MyURL;
import com.project.enjoyit.utils.MyNetworkUtil;
import com.rxy.edittextmodel_master.ClearableEditText;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

import dmax.dialog.SpotsDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private static final String TAG = "LoginActivity";

	private ClearableEditText etUsername;
	private ClearableEditText etPassword;
	private Button btRegister;
	private Button btLogin;
	private NetworkImageView niv_head;
	private ImageView ivUpdatePassword;

	private ToggleButton tbSwitch;
	private TextView tvRecordText;
	
	public static final int CODE_REGISTER2LOGIN = 1;

	private static final String sbDefaultAccountFileName = "DefaultAccount";
	private boolean canRecord = true;
	
	private static Context context;
	private final static int CODE_TOAST_MSG = 0;
	static Handler handler = new Handler() {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_login);

		context = LoginActivity.this;

		initView();
		restoreDefaultAccount();
		initListener();

		// btLogin.performClick();
		etUsername.requestFocus();
		etPassword.requestFocus();
		etUsername.requestFocus();
	}

	private void initListener() {
		tbSwitch.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				SharedPreferences spDefaultAccount = getSharedPreferences(
						sbDefaultAccountFileName, MODE_PRIVATE);
				Editor editor = spDefaultAccount.edit();
				if (on){
					editor.putBoolean("canRecord", true);
					tvRecordText.setText("记住密码 开");
				}else {
					tvRecordText.setText("记住密码 关");
					editor.putBoolean("canRecord", false);
					editor.putString("username", "");
					editor.putString("password", "");
				}
				editor.commit();
			}
		});
		//http://www.2cto.com/kf/201402/276814.html
		etPassword.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				 if(keyCode == KeyEvent.KEYCODE_ENTER){
		                /*隐藏软键盘*/
		                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		                if(inputMethodManager.isActive()){
		                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		                }
		                 btLogin.performClick();
		                return true;
		            }
		            return false;
			}
		});
		ivUpdatePassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final MaterialDialog dialog = new MaterialDialog(context);
				dialog.setTitle("修改密码");
				final View view = LayoutInflater.from(context).inflate(
						R.layout.update_password_dialog, null);
				dialog.setPositiveButton("   确定      ", new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						EditText etUsername1 = (EditText) view
								.findViewById(R.id.et_username);
						EditText etPhone1 = (EditText) view
								.findViewById(R.id.et_phone);
						EditText etPassword1 = (EditText) view
								.findViewById(R.id.et_password);
						EditText etRepassowrd1 = (EditText) view
								.findViewById(R.id.et_repasword);
						String username = etUsername1.getText().toString()
								.trim();
						String phone = etPhone1.getText().toString().trim();
						String password = etPassword1.getText().toString()
								.trim();
						String repassword = etRepassowrd1.getText().toString()
								.trim();
						if (username.isEmpty() || phone.isEmpty()
								|| password.isEmpty() || repassword.isEmpty()) {
							myToast("不能有空哦");
							return;
						}
						if (!password.equals(repassword)) {
							myToast("两次输入的密码不一致呐");
							return;
						}
						updatePassword(dialog, username, phone, password);
					}

				}).setNegativeButton("取消         ", new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.setView(view).show();

			}
		});
		btRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivityForResult(intent, CODE_REGISTER2LOGIN);
			}
		});
		btLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				login();

			}
		});
		etUsername.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {

				} else {
					String url = MyURL.GET_USER_HEAD_PIC
							+ "?username="
							+ MyNetworkUtil.URLEncode(etUsername.getText()
									.toString().trim());
					niv_head.setImageUrl(url, MyApplication.getMyVolley()
							.getImageLoader());
				}
			}
		});
	}

	private void updatePassword(final MaterialDialog dialog,
			final String username, String phone, final String password) {
		final AlertDialog loading = new SpotsDialog(LoginActivity.this);
		loading.setMessage("正在修改中，请稍候....");
		loading.show();

		Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject res = new JSONObject(response);
					// Log.e(TAG, res.toString());
					myToast(res.getString("msg"));
					if (res.getInt("code") == 1) {
						etUsername.setText(username);
						etPassword.setText(password);
						dialog.dismiss();
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
		map.put("username", username);
		map.put("phone", phone);
		map.put("password", MyAlgorithm.hashMd5(password));
		MyApplication.getMyVolley().addPostStringRequest(
				MyURL.UPDATE_PASSWORD_URL, listener, errorListener, map, TAG);

	}

	protected void login() {
		final String username = etUsername.getText().toString().trim();
		String tmp = etPassword.getText().toString().trim();
		if (username.isEmpty() || tmp.isEmpty()) {
			Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		if (tmp.length() != 32) {
			tmp = MyAlgorithm.hashMd5(tmp);
		}

		final AlertDialog loading = new SpotsDialog(LoginActivity.this);
		loading.setMessage("正在登陆中...");
		loading.show();

		final String password = tmp;
		Log.e(TAG, username + " " + password);

		Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject res = new JSONObject(response);
					// Log.e(TAG, res.toString());
					myToast(res.getString("msg"));
					if (res.getInt("code") == 1) {
						String token = res.getString("token");
						MyApplication.getUser().setToken(token);
						MyApplication.getUser().setUsername(username);
						MyApplication.getUser().setPassword(password);

						recordPwd(username, password);
						requestUserInfo(loading);
					}else{
						loading.dismiss();
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
//					loading.dismiss();
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				myToast("是不是网络出问题了呢？");
				loading.dismiss();
			}

		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		String pwd = RSAEncrypt(password);
		Log.e("pwd", password);
		Log.e("rsa", pwd);
		map.put("password", pwd);
		MyApplication.getMyVolley().addPostStringRequest(MyURL.LOGIN_URL,
				listener, errorListener, map, TAG);
	}

	protected void recordPwd(String username, String password) {
		if (canRecord){
			SharedPreferences spDefaultAccount = getSharedPreferences(
					sbDefaultAccountFileName, MODE_PRIVATE);
			Editor editor = spDefaultAccount.edit();
			editor.putString("username", username);
			editor.putString("password", password);
			editor.commit();
		}
	}

	protected void requestUserInfo(final AlertDialog loading) {
		Listener<String> listener = new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getInt("code") == 1) {
						Log.e(TAG, "取用户信息成功");

						MyApplication.getUser()
								.updateFromJSONObject(jsonObject);

						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
						finish();
					} else {
						Log.e(TAG, "获取用户信息失败：" + response);
						myToast("获取用户信息失败");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(TAG, "获取用户信息失败：" + response);
					myToast("解析失败，无法取得用户信息");
					
				}finally{
					loading.dismiss();
				}
			}
		};
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "获取用户信息失败：");
				myToast("是不是网络出问题了呢？");
				loading.dismiss();
			}
		};
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", MyApplication.getUser().getToken());
		map.put("username", MyApplication.getUser().getUsername());
		MyApplication.getMyVolley().addPostStringRequest(MyURL.GET_USER_INFO,
				listener, errorListener, map, TAG);
	}

	private void initView() {
		etUsername = (ClearableEditText) findViewById(R.id.et_username_or_phone);
		etPassword = (ClearableEditText) findViewById(R.id.et_password);
		btRegister = (Button) findViewById(R.id.bt_register);
		btLogin = (Button) findViewById(R.id.bt_login);

		niv_head = (NetworkImageView) findViewById(R.id.niv_head_pic);
		niv_head.setDefaultImageResId(R.drawable.default_photo);
		niv_head.setErrorImageResId(R.drawable.default_photo);
		ivUpdatePassword = (ImageView) findViewById(R.id.iv_update_password);
		
		tvRecordText = (TextView)findViewById(R.id.tv_record_pwd_text);
		tbSwitch = (ToggleButton)findViewById(R.id.tb_record_pwd_switch);
		tbSwitch.setToggleOn(true);
		tvRecordText.setText("记住密码 开");
	}

	private void restoreDefaultAccount() {
		SharedPreferences spDefaultAccount = getSharedPreferences(
				sbDefaultAccountFileName, MODE_PRIVATE);
		canRecord = spDefaultAccount.getBoolean("canRecord", true);
		if (!canRecord){
			tvRecordText.setText("记住密码 关");
			tbSwitch.setToggleOn(false);
			return;
		}
		tvRecordText.setText("记住密码 开");
		tbSwitch.setToggleOn(true);
		String username = spDefaultAccount.getString("username", "");
		String password = spDefaultAccount.getString("password", "");
		etUsername.setText(username);
		etPassword.setText(password);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CODE_REGISTER2LOGIN:
			if (resultCode == RESULT_OK) {
				String username = data.getExtras().getString("username");
				etUsername.setText(username);
				etPassword.setText("");
				Log.e(TAG, username);
			}
			break;

		default:
			break;
		}
	}

	
	// RSA加密
		private String RSAEncrypt(String pwd) {
			Encrypter mCrypter;
			String res = "";
			try {
				mCrypter = new Encrypter(new AndroidKeyczarReader(getResources(),
						"rsa_pub_key"));
				res = mCrypter.encrypt(pwd);
			} catch (KeyczarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return res;
		}

	

}
