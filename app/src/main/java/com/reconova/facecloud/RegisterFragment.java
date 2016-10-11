package com.reconova.facecloud;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.reconova.facecloud.util.CameraInterface;
import com.reconova.facecloud.util.SettingsHelper;

public class RegisterFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/** Click this for select images. */
	ImageView mSelectCamera;
	ImageView mSelectPhoto;

	EditText mNameEditText;
	EditText mIdcardEditText;
	Spinner mFaceDbSpinner;
	ArrayAdapter<String> facedbAdapter = null;
	String[] facedbName;
	String[] facedbID;
	private SettingsHelper settingsHelper;
	String facedb_id;

	Button mResgisterButton;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static RegisterFragment newInstance(int sectionNumber) {
		RegisterFragment fragment = new RegisterFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		settingsHelper = new SettingsHelper(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_register, container,
				false);

		mSelectPhoto = (ImageView) rootView.findViewById(R.id.iv_select_photo);
		mSelectCamera = (ImageView) rootView.findViewById(R.id.iv_select_image);
		mSelectCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Ask for select image.
				((MainActivity) getActivity()).showPhotoPopupWindow();
			}

		});

		mNameEditText = (EditText) rootView.findViewById(R.id.edt_name);
		mIdcardEditText = (EditText) rootView.findViewById(R.id.edt_idcard);
		mFaceDbSpinner = (Spinner) rootView.findViewById(R.id.spin_facedb);
		mResgisterButton = (Button) rootView.findViewById(R.id.register_button);

		mFaceDbSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				facedb_id = facedbID[position];
				Log.i("test", "facedb[position]===" + facedbID[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				facedb_id = facedbID[0];
			}
		});

		mResgisterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = mNameEditText.getText().toString().trim();
				String idcard = mIdcardEditText.getText().toString().trim()
						.toUpperCase(Locale.US);
				if (name.isEmpty()) {
					Toast.makeText(getActivity(), "名字不能为空", Toast.LENGTH_SHORT)
							.show();
				} else if (idcard.isEmpty()) {
					Toast.makeText(getActivity(), "证件号不能为空", Toast.LENGTH_SHORT)
							.show();
				} else if (mFaceDbSpinner.getSelectedItemId() == 0) {
					Toast.makeText(getActivity(), "数据库不能为空", Toast.LENGTH_SHORT)
							.show();
				} else {
					((MainActivity) getActivity()).login(name, idcard,
							facedb_id);
				}
			}

		});

		return rootView;
	}

	public void onPhotoSelect() {

	}

	/**
	 * 注册成功清空注册信息
	 */
	public void onRegisterOk() {
		mNameEditText.getText().clear();
		mIdcardEditText.getText().clear();
		mFaceDbSpinner.setSelection(0, true);
		mSelectPhoto.setScaleX(1.0f);
		mSelectPhoto.setScaleY(1.0f);
		mSelectPhoto.setImageBitmap(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "settings---facedb===" + settingsHelper.getFaceDB());
		facedbID = ("请选择数据库+" + settingsHelper.getFaceDB()).split("\\+");
		//设置数据库名称的列表
		facedbName = ("请选择数据库+" + settingsHelper.getFaceDBName()).split("\\+");
		facedbAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, facedbName);
		mFaceDbSpinner.setAdapter(facedbAdapter);
	}

	@Override
	public void onPause() {
		super.onPause();
		CameraInterface.getInstance().doStopCamera();
	}

}
