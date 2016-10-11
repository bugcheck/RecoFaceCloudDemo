package com.reconova.facecloud;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.reconova.facecloud.action.CompareAction;
import com.reconova.facecloud.util.HttpUtil;
import com.reconova.facecloud.util.ImageUtil;
import com.reconova.facecloud.util.JsonUtil;
import com.reconova.facecloud.util.MD5Util;
import com.reconova.facecloud.util.PhotoUtils;
import com.reconova.facecloud.util.PhotoUtils.CaptureImageAction;
import com.reconova.facecloud.util.SettingsHelper;
import com.reconova.facecloud.util.ToastUtil;
import com.reconova.facecloud.view.ExitDialog;
import com.reconova.facecloud.view.IconButton;
import com.reconova.facecloud.view.MenuPopupWindow;
import com.reconova.facecloud.view.ProgressWindow;
import com.reconova.facecloud.view.SelectPhotoPopupwindow;
import com.reconova.facecloud.view.SelectPhotoPopupwindow.SelectPhotoAction;

public class MainActivity extends ActionBarActivity implements
		SelectPhotoAction, CaptureImageAction {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	IconButton mRegisterPageButton;
	IconButton mComparePageButton;

	ImageView mCameraSwitch;
	TextView mTitleTextView;

	int mRegisterPageIndex = 1;
	int mComparePageIndex = 0;

	SelectPhotoPopupwindow selectPhotoWindow;
	PhotoUtils mPhotoUtils;

	ProgressWindow progressWindow;

	ExitDialog exitDialog;

	private Bitmap selectBitmap;
	private SettingsHelper settingsHelper;

	private MenuPopupWindow menuPopupWindow;
	private boolean isComparePage = true;
	private View register_line;
	private View compare_line;

	@Override
	public void onPause() {
		super.onPause();
		Log.e("test", "onPause onPause onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.e("test", "onStop onStop onStop");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (exitDialog != null) {
				exitDialog.show();
			}
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// showMenuPop();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		// actionBar.setIcon(R.drawable.home_logo);
		actionBar.setCustomView(R.layout.actionbar_customer);
		mTitleTextView = (TextView) actionBar.getCustomView().findViewById(
				R.id.title);
		mCameraSwitch = (ImageView) actionBar.getCustomView().findViewById(
				R.id.camera_switch);
		mCameraSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CompareFragment fragment = getCompareFragment();
				if (fragment != null) {
					fragment.switchCamera();
				}
			}

		});

		setContentView(R.layout.activity_main);

		register_line = findViewById(R.id.register_line);
		compare_line = findViewById(R.id.compare_line);

		mRegisterPageButton = (IconButton) findViewById(R.id.btn_register);
		mRegisterPageButton.setOnClickListener(mBottomBtnClickLs);

		mComparePageButton = (IconButton) findViewById(R.id.iconBtn_compare);
		mComparePageButton.setOnClickListener(mBottomBtnClickLs);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int pageIndex) {
				if (pageIndex == mRegisterPageIndex) {
					onRegisterPage();
				} else if (pageIndex == mComparePageIndex) {
					onComparePage();
				}
			}

		});
		mPhotoUtils = new PhotoUtils(this, this);
		selectPhotoWindow = new SelectPhotoPopupwindow(this, this);
		exitDialog = new ExitDialog(this);
		settingsHelper = new SettingsHelper(this);
		progressWindow = new ProgressWindow(this);
		onComparePage();
	}

	private void onRegisterPage() {
		mRegisterPageButton.setSelected(true);
		mComparePageButton.setSelected(false);
		// 因为标题无法居中，所有前面加了空格
		mTitleTextView.setText("      人脸注册");
		mCameraSwitch.setVisibility(View.INVISIBLE);
		isComparePage = false;
		register_line.setBackgroundColor(Color.parseColor("#5c98e0"));
		compare_line.setBackgroundColor(Color.parseColor("#D9D9D9"));
	}

	private void onComparePage() {
		mRegisterPageButton.setSelected(false);
		mComparePageButton.setSelected(true);
		mTitleTextView.setText("      人脸比对");
		mCameraSwitch.setVisibility(View.VISIBLE);
		isComparePage = true;
		compare_line.setBackgroundColor(Color.parseColor("#5c98e0"));
		register_line.setBackgroundColor(Color.parseColor("#D9D9D9"));
	}

	private OnClickListener mBottomBtnClickLs = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mRegisterPageButton) {
				mViewPager.setCurrentItem(mRegisterPageIndex);
			} else if (v == mComparePageButton) {
				mViewPager.setCurrentItem(mComparePageIndex);
			}
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			showMenuPop();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showMenuPop() {
		if (menuPopupWindow == null) {
			menuPopupWindow = new MenuPopupWindow(this);
			menuPopupWindow.showWindow(findViewById(R.id.actionbar));
		} else {
			menuPopupWindow.showWindow(findViewById(R.id.actionbar));
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			if (position == mRegisterPageIndex) {
				return RegisterFragment.newInstance(position + 1);
			} else if (position == mComparePageIndex) {
				return CompareFragment.newInstance(position + 1);
			}
			return null;
			// return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * 将人脸注册到远程数据库
	 * @param name 姓名
	 * @param idcard 身份证
	 * @param facedb_id 数据库ID
	 */
	public void addPerson(final String name, final String idcard,
						  final String facedb_id) {
		AsyncTask<Void, Void, String> recognizeTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					byte[] image = ImageUtil.Bitmap2Bytes(selectBitmap);
					Map<String, String> map = new HashMap<String, String>();
					map.put("name", name);
					map.put("id_card", idcard);
					String url = CompareAction.wrapAddPersonUrl(
							settingsHelper.getHttpServer(), facedb_id);
					Log.i("test", "url=====" + url);
					String result = HttpUtil.putMethod(url, map);
					Log.i("test", "result====" + result);
					JSONObject json = new JSONObject(result);
					JSONObject person = json.getJSONObject("person_data");
					if (person != null) {
						String personId = person.getString("person_id");
						String result2 = HttpUtil.recognize(url + "/"
								+ personId + "/image", image, null, "PUT");
						Log.i("test", "result2====" + result2);
						JSONObject resultJson = new JSONObject(result2);
						int ret = resultJson.getInt("ret");
						if (ret == 2005) {
							return "人脸检测失败";
						} else if (ret == 0) {
							return "success";
						} else {
							return "failed";
						}
					} else {
						return "failed";
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return getString(R.string.base64_encode_failed_tip);
				} catch (JSONException e) {
					e.printStackTrace();
					return getString(R.string.json_decode_failed_tip);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					return getString(R.string.scoket_timeout_failed_tip);
				} catch (IOException e) {
					e.printStackTrace();
					return getString(R.string.io_failed_tip);
				}
			}

			@Override
			protected void onPostExecute(String result) {
				dismissProgressWindow();
				if (result.equals("success")) {
					getRegisterFragment().onRegisterOk();
					selectBitmap = null;
					ToastUtil.showToast(MainActivity.this, "注册成功");
				} else {
					ToastUtil.showToast(MainActivity.this, result);
				}
			}

		};
		recognizeTask.execute();
	}

	/**
	 * 远程登陆
	 */
	public void login(final String name, final String idcard,
					  final String facedb_id) {
		AsyncTask<Void, Void, String> loginTask = new AsyncTask<Void, Void, String>() {

			@Override
			public void onPreExecute() {
				showProgressWindow("注册人脸", "请耐心等待");
			}

			@Override
			protected String doInBackground(Void... params) {
				if (selectBitmap == null)
					return "请选择照片";
				Map<String, String> map = new HashMap<String, String>();
				map.put("user_name", settingsHelper.getUsername());
				map.put("user_pwd",
						MD5Util.getMD5(settingsHelper.getPassword()));
				map.put("mode", "force_login");
				try {
					String url = CompareAction.wrapLoginUrl(settingsHelper
							.getHttpServer());
					String json = HttpUtil.postLogin(url, map);
					Log.i("test", "json====" + json);
					if (json.equals("")) {
						return "JsonNull";
					}
					String ret_mes = JsonUtil.parseLoginResult(json);
					return ret_mes;
				} catch (ConnectException e) {
					e.printStackTrace();
					return getString(R.string.connect_failed_tip);
				} catch (SocketException e) {
					e.printStackTrace();
					return getString(R.string.socket_failed_tip);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					return getString(R.string.scoket_timeout_failed_tip);
				} catch (JSONException e) {
					e.printStackTrace();
					return getString(R.string.json_decode_failed_tip);
				} catch (IOException e) {
					e.printStackTrace();
					return getString(R.string.io_failed_tip);
				}

			}

			@Override
			protected void onPostExecute(String result) {
				if (result.equals("成功")) {
					addPerson(name, idcard, facedb_id);
				} else if (result.equals("JsonNull")) {
					dismissProgressWindow();
					ToastUtil.showToast(MainActivity.this, "连接服务器失败！");
				} else {
					dismissProgressWindow();
					ToastUtil.showToast(MainActivity.this, result);
				}
			}
		};

		loginTask.execute();
	}

	@Override
	public void takePhoto() {
		mPhotoUtils.takePhoto();
	}

	@Override
	public void choosePhoto() {
		mPhotoUtils.selectPhoto();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mPhotoUtils.onActivityResult(requestCode, resultCode, data);
	}

	public void showPhotoPopupWindow() {
		selectPhotoWindow.showPopupWindow(findViewById(R.id.main));
	}

	public void showProgressWindow(String title, String msg) {
		progressWindow.showProgressWindows(title, msg, findViewById(R.id.main));
	}

	public void dismissProgressWindow() {
		progressWindow.dismiss();
	}

	private RegisterFragment getRegisterFragment() {
		return (RegisterFragment) mSectionsPagerAdapter.instantiateItem(
				mViewPager, mRegisterPageIndex);
	}

	private CompareFragment getCompareFragment() {
		return (CompareFragment) mSectionsPagerAdapter.instantiateItem(
				mViewPager, mComparePageIndex);
	}

	@SuppressLint("NewApi") @Override
	public void onCaptureImage(Bitmap bmp) {
		if (bmp == null) {
			ToastUtil.showToast(this, "请选择照片");
			return;
		}
		this.selectBitmap = bmp;
		if (isComparePage) {
			getCompareFragment().iv_selectphoto.setImageBitmap(bmp);
		} else {
			getRegisterFragment().mSelectPhoto.setScaleX(0.65f);
			getRegisterFragment().mSelectPhoto.setScaleY(0.65f);
			getRegisterFragment().mSelectPhoto.setImageBitmap(bmp);
		}

	}

	public Bitmap getSelectBitmap() {
		return selectBitmap;
	}

}
