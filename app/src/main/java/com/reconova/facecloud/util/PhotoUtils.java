package com.reconova.facecloud.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class PhotoUtils {
	/** 图片类型 */
	static final String PHOTO_TYPE = "image/*";
	/** 由相册跳转到截图的请求码 */
	private static final int PHOTO_ZOOM =1 << 6;
	/** 截图结束后的请求码 */
	private static final int IMAGE_RESULT =1 << 7;
	/** 由拍照跳转到截图的请求码 */
	private static final int PHOTO_GRAPH = 1 << 8;
	/** 日期类 */
	private Date date = new Date();
	/** 当前日期 */
	private String nowDate;
	private Bitmap bmp;
	/** 新建文件，用于保存图片 */
	private File cropFile;

	private Activity activity;

	private CaptureImageAction captureImageAction;

	public PhotoUtils(Activity activity, CaptureImageAction captureImageAction) {
		this.activity = activity;
		this.captureImageAction = captureImageAction;
	}

	public Bitmap getBitmap() {
		return bmp;
	}

	public void takePhoto() {
		File lcoalfile = new File(
				Environment.getExternalStorageDirectory(), "/RecoFaceCloud/Temp/Img/");// 外存根目录下的文件夹
		File dir = new File(lcoalfile.getPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 打开照相机
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		nowDate = String.valueOf(date.getTime());
		Log.i("test", "nowDate==" + nowDate);
		// 保存图片时的路径和文件名
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				Environment.getExternalStorageDirectory(), "/RecoFaceCloud/Temp/Img/"
				+ nowDate + ".jpg")));
		// 跳转去照相，照相完后保存照片并跳转到截图
		activity.startActivityForResult(intent, PHOTO_GRAPH);
	}

	public void selectPhoto() {
		Log.i("test", "进入相册：-----");
		// 进入到相册
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		// 只取相册中所有的图片文件
		intent.setDataAndType(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PHOTO_TYPE);
		// 选择完照片后跳转到截图
		activity.startActivityForResult(intent, PHOTO_ZOOM);
	}

	/**
	 * 根据选择拍照、相册之后返回的请求码分别执行不同的操作
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 照相后 intent data值为null
		if (requestCode == PHOTO_GRAPH) {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/RecoFaceCloud/Temp/Img/" + nowDate + ".jpg");
			startPhotoZoom(Uri.fromFile(file));
		}
		if (data == null) {
			return;
		}

		switch (requestCode) {
			case PHOTO_ZOOM:
				Log.i("test", "进入相册取照片：-----");
				// 取到选择的相片
				Uri uri = data.getData();
				Log.i("test", "进入相册取照片截图：-----" + uri);
				// 截图处理
				startPhotoZoom(uri);
				break;
			case IMAGE_RESULT:
				Log.i("test", "进入相册取照片取到截完图的bitmap：-----");
				// 取到截图后的bitmap
				Bundle bundle = data.getExtras();
				bmp = bundle.getParcelable("data");
				if (bmp == null) return;
				// // 进入这里说明图片被更新了
				File tmp = new File(Environment.getExternalStorageDirectory(),
						"/RecoFaceCloud/Temp/CropImg");
				File dir = new File(tmp.getPath());
				if (!dir.exists()) {
					dir.mkdirs();
					Log.i("test", "创建图片临时存放文件夹成功！-----");
				}

				cropFile = new File(Environment.getExternalStorageDirectory()
						+ "/RecoFaceCloud/Temp/CropImg/" + date.getTime() + ".jpg");

				Log.e("test", "图片名称-----" + cropFile);

				BufferedOutputStream bos;
				try {
					bos = new BufferedOutputStream(new java.io.FileOutputStream(
							cropFile.getPath()));
					bmp.compress(CompressFormat.JPEG, 100, bos);
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (captureImageAction != null) {
					//captureImageAction.onCaptureImage(cropFile.getAbsolutePath());
					captureImageAction.onCaptureImage(bmp);
				}
				break;
			case 0:
				return;
		}
	}

	/**
	 * 对照片进行截图处理
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, PHOTO_TYPE);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("return-data", true);
		Log.i("test", "进入相册取照片截完图：-----");
		// 截图完成后 进行跳转
		activity.startActivityForResult(intent, IMAGE_RESULT);
	}

	public static interface CaptureImageAction {
		//	public void onCaptureImage(String path);
		public void onCaptureImage(Bitmap bmp);
	}

}
