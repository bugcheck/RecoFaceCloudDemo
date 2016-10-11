package com.reconova.facecloud.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 在Application中统一捕获异常
 *
 * @author tony
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	// 错误日志保存路径
	private String path = "/sdcard/RecoFaceCloud/Log/";

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 *
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 *
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG)
						.show();
				// 收集设备参数信息
				collectDeviceInfo(mContext);
				// 保存日志文件,并发送邮件
				sendEmail(saveCrashInfo2File(ex));
				Looper.loop();
			}
		}.start();
		return true;
	}

	/**
	 * 收集设备参数信息
	 *
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 *
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "FaceCloud-APP-" + time + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}

	/**
	 * 发送邮件的方法
	 *
	 * @return
	 */
	private boolean sendEmail(String fileName) {
		Log.i("test", "开始发送邮件----------");
		Properties props = new Properties();
		props.put("mail.smtp.protocol", "smtp");
		props.put("mail.smtp.auth", "true");// 设置要验证
		props.put("mail.smtp.host", "smtp.163.com");// 设置host
		props.put("mail.smtp.port", "25"); // 设置端口
		PassAuthenticator pass = new PassAuthenticator(); // 获取帐号密码
		Session session = Session.getInstance(props, pass); // 获取验证会话
		try {
			// 配置发送及接收邮箱
			InternetAddress fromAddress, toAddress;
			/**
			 * 这个地方需要改成自己的邮箱
			 */
			fromAddress = new InternetAddress("linfeihu@163.com", "");
			toAddress = new InternetAddress("linfeihu@reconova.cn", "");
			/**
			 * 一下内容是：发送邮件时添加附件
			 */
			MimeBodyPart attachPart = new MimeBodyPart();
			FileDataSource fds = new FileDataSource(
					Environment.getExternalStorageDirectory() + "/RecoFaceCloud/Log/" + fileName); // 打开要发送的文件
			attachPart.setDataHandler(new DataHandler(fds));
			attachPart.setFileName(fds.getName());
			MimeMultipart allMultipart = new MimeMultipart("mixed"); // 附件
			allMultipart.addBodyPart(attachPart);// 添加
			// 配置发送信息
			MimeMessage message = new MimeMessage(session);
			// message.setContent("test", "text/plain");
			message.setContent(allMultipart); // 发邮件时添加附件
			message.setSubject(fileName);
			message.setFrom(fromAddress);
			message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress);
			message.saveChanges();
			// 连接邮箱并发送
			Transport transport = session.getTransport("smtp");
			/**
			 * 这个地方需要改称自己的账号和密码
			 */
			// transport.connect("smtp.163.com", "linfeihu@163.com",
			// "13476697");
			transport.send(message);
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
			//	throw new RuntimeException();// 将此异常向上抛出，此时CrashHandler就能够接收这里抛出的异常并最终将其存放到txt文件中
		}
		Log.i("test", "结束发送邮件----------");
		return false;
	}
}
