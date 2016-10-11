package com.reconova.facecloud;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.reconova.facecloud.action.CompareAction;
import com.reconova.facecloud.model.FaceDB;
import com.reconova.facecloud.model.FaceDB_Result;
import com.reconova.facecloud.model.Matching;
import com.reconova.facecloud.model.Person_Rect;
import com.reconova.facecloud.model.Recognition;
import com.reconova.facecloud.model.Recognition_Result;
import com.reconova.facecloud.util.CameraInterface;
import com.reconova.facecloud.util.EventUtil;
import com.reconova.facecloud.util.GoogleFaceDetect;
import com.reconova.facecloud.util.HttpUtil;
import com.reconova.facecloud.util.ImageUtil;
import com.reconova.facecloud.util.JsonUtil;
import com.reconova.facecloud.util.MD5Util;
import com.reconova.facecloud.util.MyApplication;
import com.reconova.facecloud.util.PhotoUtils;
import com.reconova.facecloud.util.SettingsHelper;
import com.reconova.facecloud.util.ToastUtil;
import com.reconova.facecloud.view.CameraSurfaceView;
import com.reconova.facecloud.view.ExitDialog;
import com.reconova.facecloud.view.FaceView;
import com.reconova.facecloud.view.NewDialog;
import com.reconova.facecloud.view.TextMoveLayout;

public class CompareFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static CompareFragment newInstance(int sectionNumber) {
		CompareFragment fragment = new CompareFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	private boolean isDetached = true;

	private FrameLayout framelayout;
	private CameraSurfaceView surfaceView = null;
	private FaceView faceView;

	private Button shutterBtn;
	private Button compareButton;
	private SeekBar seekbar_similarity;
	private TextView tv_similarity;

	NewDialog newDialog;

	/**
	 * 屏幕宽度
	 */
	private int screenWidth;

	/**
	 * 自定义随着拖动条一起移动的空间
	 */
	private TextMoveLayout textMoveLayout;

	private ViewGroup.LayoutParams layoutParams;
	/**
	 * 托动条的移动步调
	 */
	private float moveStep = 0;

	private float previewRate = -1f;
	private MainHandler mMainHandler = null;
	private GoogleFaceDetect googleFaceDetect = null;
	private SettingsHelper settingsHelper;

	private Recognition_Result recognition_result;
	private List<Matching> matchings;
	private List<Bitmap> bitmaps;
	private List<byte[]> byteList;
	private Person_Rect person_rect;
	private MyApplication myApplication;

	private int similarity = 70;
	PhotoUtils mPhotoUtils;
	ImageView iv_selectphoto;
	private boolean isCameraOpen = true;
	private FaceDB_Result facedb_result;
	private StringBuilder sbFaceDB;
	private StringBuilder sbFaceDBName;
	private ImageView mask_rect;

	public CompareFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		isDetached = false;
		settingsHelper = new SettingsHelper(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		isDetached = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_compare, container,
				false);
		myApplication = (MyApplication) getActivity().getApplication();
		initUI(rootView);

		screenWidth = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth();
		tv_similarity = new TextView(getActivity());
		// tv_similarity.setBackgroundColor(Color.rgb(245, 245, 245));
		tv_similarity.setTextColor(Color.rgb(0, 161, 229));
		tv_similarity.setTextSize(16);
		layoutParams = new ViewGroup.LayoutParams(screenWidth, 40);
		textMoveLayout.addView(tv_similarity, layoutParams);
		//seekbar_similarity.setProgress(similarity);
		moveStep = (float) (((float) screenWidth / (float) 100) * 0.8);
		tv_similarity.layout((int) ((similarity * moveStep)), 10, screenWidth,
				70);
		//tv_similarity.setText(similarity + "%");

		mMainHandler = new MainHandler();
		googleFaceDetect = new GoogleFaceDetect(getActivity(), mMainHandler);
		mPhotoUtils = new PhotoUtils((MainActivity) getActivity(),
				(MainActivity) getActivity());

		shutterBtn.setOnClickListener(new BtnListeners());
		compareButton.setOnClickListener(new BtnListeners());

		seekbar_similarity
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						similarity = seekBar.getProgress();
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
												  int progress, boolean fromUser) {
						tv_similarity.layout((int) (progress * moveStep), 10,
								screenWidth, 70);
						tv_similarity.setText(progress + "%");
					}
				});
		return rootView;
	}

	private void initUI(View rootView) {
		framelayout = (FrameLayout) rootView
				.findViewById(R.id.frame_camera_draw);
		surfaceView = (CameraSurfaceView) rootView
				.findViewById(R.id.camera_surfaceview);
		shutterBtn = (Button) rootView.findViewById(R.id.btn_shutter);
		shutterBtn.setVisibility(View.INVISIBLE);
		faceView = (FaceView) rootView.findViewById(R.id.face_view);
		compareButton = (Button) rootView.findViewById(R.id.btn_compare);
		seekbar_similarity = (SeekBar) rootView
				.findViewById(R.id.seekbar_similarity);
		seekbar_similarity.setVisibility(View.INVISIBLE);
		iv_selectphoto = (ImageView) rootView.findViewById(R.id.iv_selectphoto);
		textMoveLayout = (TextMoveLayout) rootView
				.findViewById(R.id.textLayout);
		mask_rect = (ImageView) rootView.findViewById(R.id.mask_rect);
		newDialog = new NewDialog(getActivity());
	}

	private class BtnListeners implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_shutter:
					if (isCameraOpen) {
						takePicture();
						isCameraOpen = false;
					} else {
						startPreview();
						isCameraOpen = true;
					}
					break;
				case R.id.btn_compare:

					takePicture();
					isCameraOpen = false;


					break;
				default:
					break;
			}
		}

	}

	private class MainHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case EventUtil.UPDATE_FACE_RECT:
					Face[] faces = (Face[]) msg.obj;
					faceView.setFaces(faces);

					if (CameraInterface.getInstance().getBitmap() == null) {
						//ToastUtil.showToast(getActivity(), "请拍照");
					} else {
						login();
					}

					break;
				case EventUtil.CAMERA_HAS_STARTED_PREVIEW:
					startGoogleFaceDetect();
					break;
			}
			super.handleMessage(msg);
		}

	}

	/**
	 * 拍照按钮监听
	 */
	private void takePicture() {
		CameraInterface.getInstance().doTakePicture();
		mMainHandler.sendEmptyMessageDelayed(EventUtil.UPDATE_FACE_RECT, 1500);
	}

	/**
	 * 转换摄像头
	 */
	public void switchCamera() {
		// 获取摄像头数量，<2时无法转换
		int mNumberOfCameras = Camera.getNumberOfCameras();
		if (mNumberOfCameras < 2) {
			ToastUtil.showToast(getActivity(), "没有更多的摄像头了");
			return;
		}
		isCameraOpen = true;
		stopGoogleFaceDetect();
		int newId = (CameraInterface.getInstance().getCameraId() + 1) % 2;
		CameraInterface.getInstance().doStopCamera();
		try {
			CameraInterface.getInstance().doOpenCamera(null, newId);
		} catch (Exception e) {
			e.printStackTrace();
			// 打开摄像头时捕获异常，防止打开失败
			ToastUtil.showToast(getActivity(), "摄像头正在使用中，或该设备没有摄像头");
		}
		CameraInterface.getInstance().doStartPreview(
				surfaceView.getSurfaceHolder(), previewRate);
		mMainHandler.sendEmptyMessageDelayed(
				EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
	}

	/**
	 * 开启预览
	 */
	public void startPreview() {
		if (surfaceView == null)
			return;
		// 获取默认摄像头ID，返回-1时表示该设备没有摄像头
		int cameraId = CameraInterface.getInstance().getDefaultCameraId();
		if (cameraId == -1) {
			ToastUtil.showToast(getActivity(), "该设备没有摄像头");
			return;
		}
		try {
			CameraInterface.getInstance().doOpenCamera(null, cameraId);
		} catch (Exception e) {
			e.printStackTrace();
			// 打开摄像头时捕获异常，防止打开失败
			ToastUtil.showToast(getActivity(), "摄像头正在使用中，或该设备没有摄像头");
			return;
		}
		CameraInterface.getInstance().doStartPreview(
				surfaceView.getSurfaceHolder(), previewRate);
		mMainHandler.sendEmptyMessageDelayed(
				EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
	}

	/**
	 * 开始人脸检测
	 */
	private void startGoogleFaceDetect() {
		Camera.Parameters params = CameraInterface.getInstance()
				.getCameraParams();
		if (params == null)
			return;
		// 检测该设备是否支持面部检测特性
		if (params.getMaxNumDetectedFaces() > 0) {
			if (faceView != null) {
				faceView.clearFaces();
				faceView.setVisibility(View.VISIBLE);
			}
			// stopGoogleFaceDetect();
			try {
				CameraInterface.getInstance().getCameraDevice()
						.setFaceDetectionListener(googleFaceDetect);
				CameraInterface.getInstance().getCameraDevice()
						.startFaceDetection();
			} catch (Exception e) {
				e.printStackTrace();
				stopGoogleFaceDetect();
				startGoogleFaceDetect();
				ToastUtil.showToast(getActivity(), "人脸检测已开启");
			}

		} else {
			ToastUtil.showToast(getActivity(), "该设备不支持人脸检测");
		}
	}

	/**
	 * 停止人脸检测
	 */
	public void stopGoogleFaceDetect() {
		Camera.Parameters params = CameraInterface.getInstance()
				.getCameraParams();
		if (params != null && params.getMaxNumDetectedFaces() > 0) {
			CameraInterface.getInstance().getCameraDevice()
					.setFaceDetectionListener(null);
			CameraInterface.getInstance().getCameraDevice().stopFaceDetection();
			faceView.clearFaces();
		}
	}

	/**
	 * 远程登陆
	 */
	private void login() {
		AsyncTask<Void, Void, String> loginTask = new AsyncTask<Void, Void, String>() {

			@Override
			public void onPreExecute() {
				if (!isDetached) {
					((MainActivity) getActivity()).showProgressWindow("人脸比对中",
							"请耐心等待..");
				}
			}

			@Override
			protected String doInBackground(Void... params) {
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
					if (settingsHelper.getFaceDB().equals("")) {
						requestFaceDB();
					} else {
						startCompare();
					}
				} else if (result.equals("JsonNull")) {
					((MainActivity) getActivity()).dismissProgressWindow();
					isCameraOpen = true;
					CameraInterface.getInstance().doStartPreview(
							surfaceView.getSurfaceHolder(), previewRate);
					CameraInterface.getInstance().clearBitmap();
					ToastUtil.showToast(getActivity(), "连接服务器失败！");
				} else {
					((MainActivity) getActivity()).dismissProgressWindow();
					isCameraOpen = true;
					CameraInterface.getInstance().doStartPreview(
							surfaceView.getSurfaceHolder(), previewRate);
					CameraInterface.getInstance().clearBitmap();
					ToastUtil.showToast(getActivity(), result);
				}
			}
		};

		loginTask.execute();
	}

	/**
	 * 开始远程比对
	 */
	private void startCompare() {
		recognition_result = new Recognition_Result();
		bitmaps = new ArrayList<Bitmap>();
		byteList = new ArrayList<byte[]>();
		AsyncTask<Void, Void, String> recognizeTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				byte[] image = ImageUtil.Bitmap2Bytes(CameraInterface
						.getInstance().getBitmap());
				String url = CompareAction.wrapRecognizeUrl(settingsHelper
						.getHttpServer());
				Log.i("test", "url===" + url);
				Map<String, String> map = new HashMap<String, String>();
				map.put("top", settingsHelper.getCompareNum() + "");
				map.put("similarity", CompareSettingsActivity.setting_similarity + "");
				map.put("face_db", settingsHelper.getFaceDB());
				Log.i("test", "similarity======" + CompareSettingsActivity.setting_similarity);
				try {
					CompareAction.getRecognition_Result(url,
							recognition_result, image, map, "POST");
					// person_rect = recognition_result.getRecognition_list()
					// .get(0).getPerson_rect();
					// String json = HttpUtil.recognize(url, image, map,
					// "POST");
					// Log.i("test", "json=========================" + json);
					// recognition_result.parseJson(json);
					matchings = new ArrayList<Matching>();
					for (Recognition r : recognition_result
							.getRecognition_list()) {
						matchings.addAll(r.getMatching_list());
					}
					if (recognition_result.getBlur() == null) {
						return "人脸检测失败";
					} else if (recognition_result.getRet() == 0
							&& matchings.size() != 0) {
						for (Matching match : matchings) {
							String image_url = match
									.getDefault_face_image_url();
							image_url = image_url.replace(
									"api.facecloud.reconova.com",
									settingsHelper.getServer());
							String imageinfo = HttpUtil.getMethod(image_url);
							String base64Data = JsonUtil
									.getBase64Data(imageinfo);
							byte[] bytes = Base64.decode(base64Data,
									Base64.NO_WRAP);
							byteList.add(bytes);
							Bitmap bitmap = ImageUtil
									.base64ToBitmap(base64Data);
							bitmaps.add(bitmap);
						}
						return "success";
					} else {



						return "无匹配，请重新选择照片";
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
				if (isDetached) {
					return;
				}
				((MainActivity) getActivity()).dismissProgressWindow();
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "匹配成功");
					Intent intent = new Intent(getActivity(),
							CompareResultListActivity.class);
					// 将匹配成功的结果存到全局变量中
					myApplication.dataHolder.put("byte_compareList", byteList);
					myApplication.dataHolder.put("matchingList", matchings);
					startActivity(intent);
				} else if(result.equals("无匹配，请重新选择照片")){
					newDialog.show();
					isCameraOpen = true;
					CameraInterface.getInstance().doStartPreview(
							surfaceView.getSurfaceHolder(), previewRate);
					CameraInterface.getInstance().clearBitmap();
				}
				else{
					isCameraOpen = true;
					CameraInterface.getInstance().doStartPreview(
							surfaceView.getSurfaceHolder(), previewRate);
					CameraInterface.getInstance().clearBitmap();
					ToastUtil.showToast(getActivity(), result);
				}
			}

		};
		recognizeTask.execute();
	}

	/**
	 * 请求人脸数据库
	 */
	private void requestFaceDB() {
		settingsHelper.removeKey("facedb_key");
		facedb_result = new FaceDB_Result();
		sbFaceDB = new StringBuilder();
		sbFaceDBName = new StringBuilder();
		myApplication.faceDB.clear();
		AsyncTask<Void, Void, String> getdbTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					String url = CompareAction.wrapFaceDbUrl(settingsHelper
							.getHttpServer());
					Log.i("test", "url===" + url);
					CompareAction.getFaceDB(url, facedb_result);
					for (FaceDB db : facedb_result.getFacedb_list()) {
						// 获取所有的数据库ID用于远程比对的时候设置比对的数据库ID
						sbFaceDB.append(db.getDb_id()).append("+");
						// 获取所有数据库名称用于注册的时候显示数据库列表的名称
						sbFaceDBName.append(db.getName()).append("+");
						myApplication.faceDB.put(db.getDb_id(), db.getName());
					}
					if (sbFaceDB.length() == 0) {
						return "empty";
					}
					sbFaceDB.deleteCharAt(sbFaceDB.length() - 1);
					Log.i("test", "sbFaceDB=====" + sbFaceDB.toString());
					return "success";
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
				if (result.equals("success")) {
					settingsHelper.SetFaceDB(sbFaceDB.toString());
					startCompare();
				} else if (result.equals("empty")) {
					((MainActivity) getActivity()).dismissProgressWindow();
					isCameraOpen = true;
					CameraInterface.getInstance().doStartPreview(
							surfaceView.getSurfaceHolder(), previewRate);
					CameraInterface.getInstance().clearBitmap();
					ToastUtil.showToast(getActivity(), "数据库为空");
				} else {
					((MainActivity) getActivity()).dismissProgressWindow();
					isCameraOpen = true;
					CameraInterface.getInstance().doStartPreview(
							surfaceView.getSurfaceHolder(), previewRate);
					CameraInterface.getInstance().clearBitmap();
					ToastUtil.showToast(getActivity(), result);
				}
			}

		};
		getdbTask.execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		isCameraOpen = true;
		startPreview();
		framelayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						framelayout.postDelayed(new Runnable() {

							@Override
							public void run() {
								framelayout.getWidth();
								Log.i("test", "framelayout.getWidth()==="
										+ framelayout.getWidth());
								Log.i("test", "framelayout.getHeight()==="
										+ framelayout.getHeight());
								int rateHeight = (int) (framelayout.getWidth() * 1.333f);
								if (rateHeight > framelayout.getHeight()) {
									int marginHeight = rateHeight
											- framelayout.getHeight();
									int marginTop = -marginHeight / 2;
									int marginBottom = -(marginHeight + marginTop);
									Log.i("test", "rateHeight===" + rateHeight);
									Log.i("test", "marginBottom==="
											+ marginBottom);
									RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) framelayout
											.getLayoutParams();
									params.topMargin = marginTop;
									params.bottomMargin = marginBottom;
									params.height = rateHeight;
									framelayout.setLayoutParams(params);
									framelayout.requestLayout();

									FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) mask_rect
											.getLayoutParams();
									params2.topMargin = -marginTop;
									params2.bottomMargin = -marginBottom;
									// params2.height = rateHeight;
									mask_rect.setLayoutParams(params2);
									mask_rect.requestLayout();
								}

							}
						}, 300);
					}
				});
	}

}
