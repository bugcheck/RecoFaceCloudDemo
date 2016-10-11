package com.reconova.facecloud.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera.CameraInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.reconova.facecloud.util.CameraInterface;
import com.reconova.facecloud.util.ToastUtil;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "yanzi";
	CameraInterface mCameraInterface;
	Context mContext;
	SurfaceHolder mSurfaceHolder;
	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceCreated...");
		int cameraId = CameraInterface.getInstance().getDefaultCameraId();
		if (cameraId == -1) {
			ToastUtil.showToast(mContext, "该设备没有摄像头");
			return;
		}
		try {
			CameraInterface.getInstance().doOpenCamera(null, cameraId);
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.showToast(mContext, "摄像头正在使用中，或该设备没有摄像头");
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceChanged...");
		Log.i(TAG, "width=="+width+"   height===="+height+"  this.getHeight()=="+this.getHeight());
		CameraInterface.getInstance().doStartPreview(mSurfaceHolder, 1.333f);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceDestroyed...");
		CameraInterface.getInstance().doStopCamera();
	}
	public SurfaceHolder getSurfaceHolder(){
		return mSurfaceHolder;
	}

}
