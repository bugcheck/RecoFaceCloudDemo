package com.reconova.facecloud;

import java.util.regex.Pattern;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.reconova.facecloud.util.SettingsHelper;
import com.reconova.facecloud.util.ToastUtil;

public class RemoteServerSettingsActivity extends ActionBarActivity {
	private EditText edt_username;
	private EditText edt_password;
	private EditText edt_ip;
	private EditText edt_host;
	private Button btn_ok;
	private SettingsHelper settingsHelper;
	private static final Pattern IPV4_PATTERN = Pattern
			.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_server_settings);
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		View viewTitleBar = getLayoutInflater().inflate(
				R.layout.actionbar_customer, null);
		TextView title = (TextView) viewTitleBar.findViewById(R.id.title);
		title.setText("远程服务器");
		actionBar.setCustomView(viewTitleBar, lp);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		ImageView btn_back = (ImageView) actionBar.getCustomView()
				.findViewById(R.id.btn_back);
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		settingsHelper = new SettingsHelper(this);
		edt_username = (EditText) findViewById(R.id.edt_username);
		edt_password = (EditText) findViewById(R.id.edt_password);
		edt_ip = (EditText) findViewById(R.id.edt_ip);
		edt_host = (EditText) findViewById(R.id.edt_host);
		btn_ok = (Button) findViewById(R.id.btn_ok);

		//设置焦点改变，字体颜色随便改变
		edt_username.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edt_username.setTextColor(getResources().getColor(
							R.color.edit_text_focus));
				}

			}
		});

		edt_password.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edt_username.setTextColor(getResources().getColor(
							R.color.edit_text_focus));

				}

			}
		});

		edt_ip.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)  {
					edt_ip.setTextColor(getResources().getColor(
							R.color.edit_text_focus));
				}
			}
		});

		edt_host.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edt_host.setTextColor(getResources().getColor(
							R.color.edit_text_focus));

				}

			}
		});

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveServerparameter();
			}
		});
	}

	/**
	 * 保存远程服务器设置参数
	 */
	private void saveServerparameter() {
		String username = edt_username.getText().toString().trim();
		String password = edt_password.getText().toString().trim();
		String ip = edt_ip.getText().toString().trim();
		String host = edt_host.getText().toString().trim();
		if (username.equals("") || password.equals("") || ip.equals("")
				|| host.equals("")) {
			ToastUtil.showToast(RemoteServerSettingsActivity.this, "参数不能为空");
			return;
		}
		if (!username.equals("")) {
			settingsHelper.SetUsername(username);
		}
		if (!password.equals("")) {
			settingsHelper.SetPassword(password);
		}
		if (!ip.equals("") && IPV4_PATTERN.matcher(ip).matches()) {
			settingsHelper.SetHttpIp(ip);
		} else {
			ToastUtil.showToast(RemoteServerSettingsActivity.this,
					"输入的IP地址格式不对");
			return;
		}
		if (!host.equals("")) {
			int intHost = 0;
			try {
				intHost = Integer.parseInt(host);
			} catch (Exception e) {
			}
			if (intHost > 65535) {
				ToastUtil.showToast(RemoteServerSettingsActivity.this,
						"输入的端口号不可以大于65535");
				return;
			} else {
				settingsHelper.SetHttpHost(host);
			}
		}
		ToastUtil.showToast(RemoteServerSettingsActivity.this, "保存成功");
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		edt_username.setText(settingsHelper.getUsername());
		edt_password.setText(settingsHelper.getPassword());
		edt_ip.setText(settingsHelper.getHttpIp());
		edt_host.setText(settingsHelper.getHttpHost());
	}

}
