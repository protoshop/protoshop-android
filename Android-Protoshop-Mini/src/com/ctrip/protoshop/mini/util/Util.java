package com.ctrip.protoshop.mini.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.protoshop.lua.LuaConfig;

public class Util {
	public static Typeface getIconTypeface(Context context) {
		return Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome.ttf");
	}

	public static File getRootFile(Context context) {
		LuaConfig config = (LuaConfig) context.getApplicationContext();
		return new File(config.getFloderPath());
	}

	public static File getLocalRootFile(Context context) {

		return getRootFile(context);
	}

	public static File getLocalProFile(Context context, String id) {
		File file = new File(getLocalRootFile(context), id);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static Bitmap getBitmap(String path) {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, opt);
		return bitmap;
	}

	public static String saveBitmap(String dirPath, Bitmap bitmap) {
		String nameString = UUID.randomUUID().toString();
		File file = new File(dirPath, nameString);
		FileOutputStream outputStream;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			outputStream = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 60, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nameString;
	}

	public static String saveBitmap(File dir, Bitmap bitmap) {
		return saveBitmap(dir.getAbsolutePath(), bitmap);
	}

	public static String readStream(InputStream inputStream) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int count = inputStream.read();
			while (count != -1) {
				outputStream.write(count);
				count = inputStream.read();
			}
			inputStream.close();
			return outputStream.toString();
		} catch (IOException e) {
			Log.e("ReadStream", "lua 读取错误!");
			return "";
		}
	}

	public static String readFileContent(File floder, String fileName) {
		try {
			File file = new File(floder, fileName);
			if (file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);
				return readStream(inputStream);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveFile(String dir, String name, String content) {
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		File file = new File(dirFile, name);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		}
	}

	public static void hideSoftkeyboard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void saveScence(Context context, String appID, String id, String content) {
		try {
			File file = getLocalProFile(context, appID);
			if (!file.exists()) {
				file.mkdirs();
			}

			File contentFile = new File(file, id);
			if (!contentFile.exists()) {
				contentFile.createNewFile();
			}

			FileWriter writer = new FileWriter(contentFile);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getUUID() {
		// return UUID.randomUUID().toString().replaceAll("-", "");
		return String.valueOf(System.currentTimeMillis());
	}
}
