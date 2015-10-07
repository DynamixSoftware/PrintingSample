package com.dynamixsoftware.printingsample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

public class PrintingSample extends Application {

	public static String TEST_PAGE_NAME = "test_page.png";
	public static String TEST_FILE_NAME = "What is PrintHand.doc";
	public static String PACKAGE_NAME_FREE = "com.dynamixsoftware.printhand";
	public static String PACKAGE_NAME_PREMIUM = "com.dynamixsoftware.printhand.premium";

	public static void saveTestImage(Context context) throws IOException {
		InputStream fis = null;
		OutputStream fos = null;
		try {
			AssetManager am = context.getAssets();
			
			fis = am.open(PrintingSample.TEST_PAGE_NAME);
			File outFile = new File(context.getExternalCacheDir(), PrintingSample.TEST_PAGE_NAME);
			if (outFile.exists()) {
				outFile.delete();
			}
	
			outFile.createNewFile();
			fos = new FileOutputStream(outFile);
			byte[] buffer = new byte[1024];
	        int read;
	        while ((read = fis.read(buffer)) != -1) {
	        	fos.write(buffer, 0, read);
	        }
	        fos.flush();
		} finally {
			if (fis != null) {
				fis.close();
				fis = null;
			}
			if (fos != null) {
				fos.close();
				fos = null;
			}
		}
	}

	public static void saveTestFile(Context context) throws IOException {
		InputStream fis = null;
		OutputStream fos = null;
		try {
			AssetManager am = context.getAssets();
			
			fis = am.open(PrintingSample.TEST_FILE_NAME);
			File outFile = new File(context.getExternalCacheDir(), PrintingSample.TEST_FILE_NAME);
			if (outFile.exists()) {
				outFile.delete();
			}
	
			outFile.createNewFile();
			fos = new FileOutputStream(outFile);
			byte[] buffer = new byte[1024];
	        int read;
	        while ((read = fis.read(buffer)) != -1) {
	        	fos.write(buffer, 0, read);
	        }
	        fos.flush();
		} finally {
			if (fis != null) {
				fis.close();
				fis = null;
			}
			if (fos != null) {
				fos.close();
				fos = null;
			}
		}
	}

}
