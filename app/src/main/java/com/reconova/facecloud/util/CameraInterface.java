package com.reconova.facecloud.util;

import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;

import com.reconova.facecloud.model.Person_Rect;

public class CameraInterface {
	private static final String TAG = "YanZi";
	private Camera mCamera;
	private Camera.Parameters mParams;
	public boolean isPreviewing = false;
	private float mPreviwRate = -1f;
	private int mCameraId = -1;
	private boolean isGoolgeFaceDetectOn = false;
	private static CameraInterface mCameraInterface;
	private Bitmap rotaBitmap = null;

	public interface CamOpenOverCallback {
		public void cameraHasOpened();
	}

	private CameraInterface() {

	}

	public static synchronized CameraInterface getInstance() {
		if (mCameraInterface == null) {
			mCameraInterface = new CameraInterface();
		}
		return mCameraInterface;
	}

	/**
	 * 打开Camera
	 *
	 * @param callback
	 * @throws Exception
	 */
	public void doOpenCamera(CamOpenOverCallback callback, int cameraId) throws Exception {
		Log.i(TAG, "Camera open....");
		doStopCamera();
		try {
			mCamera = Camera.open(cameraId); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			Log.i("test", "Camera is not available (in use or does not exist)");
			throw e;
		}
		mCameraId = cameraId;
		if (callback != null) {
			callback.cameraHasOpened();
		}
	}

	/**
	 * 获得默认的摄像头ID
	 *
	 * @return
	 */
	public int getDefaultCameraId() {
		Log.i("test", "getDefaultCameraId");
		int defaultId = -1;
		// Find the total number of cameras available
		int mNumberOfCameras = Camera.getNumberOfCameras();
		Log.i("test", "mNumberOfCameras====" + mNumberOfCameras);
		// Find the ID of the default camera
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < mNumberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			// 默认为前置摄像头,CameraId=1
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				defaultId = i;
			}
		}
		if (-1 == defaultId) {
			if (mNumberOfCameras > 0) {
				// 如果没有前置摄像头，则打开后置摄像头,CameraId=0
				defaultId = 0;
				Log.i("test", "没有前置摄像头");
			} else {
				// 没有摄像头
				// Toast.makeText(getApplicationContext(), R.string.no_camera,
				// Toast.LENGTH_LONG).show();
			}
		}
		return defaultId;
	}

	/**
	 * 开启预览
	 *
	 * @param holder
	 * @param previewRate
	 */
	public void doStartPreview(SurfaceHolder holder, float previewRate) {
		Log.i(TAG, "doStartPreview...");
		if (isPreviewing) {
			// mCamera.stopPreview();
			return;
		}
		if (mCamera != null) {

			mParams = mCamera.getParameters();
			mParams.setPictureFormat(PixelFormat.JPEG);// 设置拍照后存储的图片格式
			CamParaUtil.getInstance().printSupportPictureSize(mParams);
			CamParaUtil.getInstance().printSupportPreviewSize(mParams);
			// 设置PreviewSize和PictureSize
			Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
					mParams.getSupportedPictureSizes(), previewRate, 320);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
					mParams.getSupportedPreviewSizes(), previewRate, 320);
			mParams.setPreviewSize(previewSize.width, previewSize.height);

			mCamera.setDisplayOrientation(90);

			CamParaUtil.getInstance().printSupportFocusMode(mParams);
			List<String> focusModes = mParams.getSupportedFocusModes();
			if (focusModes.contains("continuous-video")) {
				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			mCamera.setParameters(mParams);

			try {
				// mCamera.setPreviewCallback(cb)
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();// 开启预览
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			isPreviewing = true;
			mPreviwRate = previewRate;

			mParams = mCamera.getParameters(); // 重新get一次
			Log.i(TAG,
					"最终设置:PreviewSize--With = "
							+ mParams.getPreviewSize().width + "Height = "
							+ mParams.getPreviewSize().height);
			Log.i(TAG,
					"最终设置:PictureSize--With = "
							+ mParams.getPictureSize().width + "Height = "
							+ mParams.getPictureSize().height);
		}
	}

	/**
	 * 停止预览，释放Camera
	 */
	public void doStopCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			isPreviewing = false;
			mPreviwRate = -1f;
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * 拍照
	 */
	public void doTakePicture() {
		if (isPreviewing && (mCamera != null)) {
			mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
		}
	}

	/**
	 * 获取Camera.Parameters
	 *
	 * @return
	 */
	public Camera.Parameters getCameraParams() {
		if (mCamera != null) {
			mParams = mCamera.getParameters();
			return mParams;
		}
		return null;
	}

	/**
	 * 获取Camera实例
	 *
	 * @return
	 */
	public Camera getCameraDevice() {
		return mCamera;
	}

	public int getCameraId() {
		return mCameraId;
	}

	public Bitmap getBitmap() {
		return rotaBitmap;
	}

	public Bitmap getClipBitmap(Person_Rect person_rect) {
		return ImageUtil.getClipBitmap(getBitmap(), person_rect);
	}

	public void clearBitmap() {
		rotaBitmap = null;
	}

	/* 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 */
	ShutterCallback mShutterCallback = new ShutterCallback()
			// 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
	{
		public void onShutter() {
			// TODO Auto-generated method stub
			Log.i(TAG, "myShutterCallback:onShutter...");
		}
	};
	PictureCallback mRawCallback = new PictureCallback()
			// 拍摄的未压缩原数据的回调,可以为null
	{

		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "myRawCallback:onPictureTaken...");

		}
	};
	PictureCallback mJpegPictureCallback = new PictureCallback()
			// 对jpeg图像数据的回调,最重要的一个回调
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if (null != data) {
				b = BitmapFactory.decodeByteArray(data, 0, data.length);// data是字节数据，将其解析成位图
				mCamera.stopPreview();
				isPreviewing = false;
			}
			// 保存图片到sdcard
			if (null != b) {
				if (getCameraId() == CameraInfo.CAMERA_FACING_FRONT) {
					rotaBitmap = ImageUtil.getFacePreRotateBitmap(b, 270.0f);
				} else {
					rotaBitmap = ImageUtil.getFaceBackRotateBitmap(b, 90.0f);
				}
				FileUtil.saveBitmap(rotaBitmap);
			}

			// 再次进入预览
			// mCamera.startPreview();
			// isPreviewing = true;
		}
	};

}
