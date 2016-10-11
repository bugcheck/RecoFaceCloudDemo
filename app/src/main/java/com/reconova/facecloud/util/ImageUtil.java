package com.reconova.facecloud.util;

import java.io.ByteArrayOutputStream;

import com.reconova.facecloud.model.Person_Rect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

public class ImageUtil {
	/**
	 * 旋转Bitmap
	 *
	 * @param b
	 * @param rotateDegree
	 * @return
	 */
	public static Bitmap getFacePreRotateBitmap(Bitmap b, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postScale(1, -1); // 镜像翻转
		matrix.postRotate((float) rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	public static Bitmap getFaceBackRotateBitmap(Bitmap b, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postRotate((float) rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	public static Bitmap getClipBitmap(Bitmap b, Person_Rect person_rect) {
		int top = person_rect.getTop();
		int left = person_rect.getLeft();
		int right = person_rect.getRight();
		int bottom = person_rect.getBottom();
		int width = right - left;
		int height = bottom - top;
		Bitmap rotaBitmap = Bitmap.createBitmap(b, left, top, width, height);
		return rotaBitmap;
	}

	public static Bitmap base64ToBitmap(String base64Data) {
		Bitmap bitmap = null;
		byte[] bytes = Base64.decode(base64Data, Base64.NO_WRAP);
		bitmap = Bytes2Bimap(bytes);
		return bitmap;
	}

	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
}
