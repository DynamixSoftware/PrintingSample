package com.dynamixsoftware.printingsample.samples;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.dynamixsoftware.printingsample.MainActivity;
import com.dynamixsoftware.printingsample.PrintingSample;

public class ShareIntentSample {
	
	private static String TAG = "ShareIntent";

	private Context context;
	
	public ShareIntentSample(Context context) {
		this.context = context;
	}
	
	/***
	 * Tries to share image using ACTION_VIEW or ACTION_SEND Intent.
	 * @param actionView true if ACTION_VIEW, false if ACTION_SEND
	 */
	public void shareImage(boolean actionView) {
		try {
			PrintingSample.saveTestImage(context);
		} catch (Exception e) {
			Log.d(TAG, "Failed to save test image");
			e.printStackTrace();
			return;
		}
		
		String action = actionView ? Intent.ACTION_VIEW : Intent.ACTION_SEND;
		Intent i = new Intent(action);
		
		Uri uri = Uri.parse("file://" + context.getExternalCacheDir().getAbsolutePath() + "/" + PrintingSample.TEST_PAGE_NAME);
		if (actionView) {
			i.setDataAndType(uri, "image/png");			
		} else {
			i.putExtra(Intent.EXTRA_STREAM, uri);
			i.setType("image/png");
		}
		
		i.setPackage(PrintingSample.PACKAGE_NAME_FREE);
		try {
			context.startActivity(i);
			return;
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_FREE + " is not installed.");
		}
		
		i.setPackage(PrintingSample.PACKAGE_NAME_PREMIUM);
		try {
			context.startActivity(i);
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_PREMIUM + " is not installed.");
		}
	}
	
	/***
	 * Tries to share multiple images.
	 */
	public void shareMiltipleImages() {
		try {
			PrintingSample.saveTestImage(context);
		} catch (Exception e) {
			Log.d(TAG, "Failed to save test image");
			e.printStackTrace();
			return;
		}
		
		Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
		
		Uri uri = Uri.parse("file://" + context.getExternalCacheDir().getAbsolutePath() + "/" + PrintingSample.TEST_PAGE_NAME);
		ArrayList<Uri> urisList = new ArrayList<Uri>();
		urisList.add(uri);
		urisList.add(uri);
		urisList.add(uri);

		i.putExtra(Intent.EXTRA_STREAM, urisList);
		i.setType("image/*");
		
		i.setPackage(PrintingSample.PACKAGE_NAME_FREE);
		try {
			context.startActivity(i);
			return;
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_FREE + " is not installed.");
		}
		
		i.setPackage(PrintingSample.PACKAGE_NAME_PREMIUM);
		try {
			context.startActivity(i);
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_PREMIUM + " is not installed.");
		}
	}
	
	/***
	 * Tries to share image with return to caller application after finish. Check {@link MainActivity#onActivityResult(int requestCode, int resultCode, Intent data)} for available results information.
	 * @param actionView
	 * @param requestCode
	 */
	public void shareImageReturn(boolean actionView, int requestCode) {		
		try {
			PrintingSample.saveTestImage(context);
		} catch (Exception e) {
			Log.d(TAG, "Failed to save test image");
			e.printStackTrace();
			return;
		}
		
		String action = actionView ? Intent.ACTION_VIEW : Intent.ACTION_SEND;
		Intent i = new Intent(action);
		
		Uri uri = Uri.parse("file://" + context.getExternalCacheDir().getAbsolutePath() + "/" + PrintingSample.TEST_PAGE_NAME);
		if (actionView) {
			i.setDataAndType(uri, "image/png");			
		} else {
			i.putExtra(Intent.EXTRA_STREAM, uri);
			i.setType("image/png");
		}
		
		i.setPackage(PrintingSample.PACKAGE_NAME_FREE);
		
		// Set return variable
		i.putExtra("return", true);
		
		try {
			((Activity) context).startActivityForResult(i, requestCode);
			return;
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_FREE + " is not installed.");
		}
		
		i.setPackage(PrintingSample.PACKAGE_NAME_PREMIUM);
		try {
			((Activity) context).startActivityForResult(i, requestCode);
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_PREMIUM + " is not installed.");
		}
	}
	
	/***
	 * Tries to share file using ACTION_VIEW or ACTION_SEND Intent.
	 * @param actionView true if ACTION_VIEW, false if ACTION_SEND
	 */
	public void shareFile(boolean actionView) {
		try {
			PrintingSample.saveTestFile(context);
		} catch (Exception e) {
			Log.d(TAG, "Failed to save test image");
			e.printStackTrace();
			return;
		}
		
		String action = actionView ? Intent.ACTION_VIEW : Intent.ACTION_SEND;
		Intent i = new Intent(action);
		
		// Scheme "content" also available
		String scheme = "file://";
		
		// MIME types available:
		// application/pdf
		// application/vnd.ms-word
		// application/ms-word
		// application/msword
		// application/vnd.openxmlformats-officedocument.wordprocessingml.document
		// application/vnd.ms-excel
		// application/ms-excel
		// application/msexcel
		// application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
		// application/vnd.ms-powerpoint
		// application/ms-powerpoint
		// application/mspowerpoint
		// application/vnd.openxmlformats-officedocument.presentationml.presentation
		// application/haansofthwp
		// text/plain
		// text/html
		
		Uri uri = Uri.parse(scheme + context.getExternalCacheDir().getAbsolutePath() + "/" + PrintingSample.TEST_FILE_NAME);
		if (actionView) {
			i.setDataAndType(uri, "application/msword");
		} else {
			i.putExtra(Intent.EXTRA_STREAM, uri);
			i.setType("application/msword");
		}
		
		i.setPackage(PrintingSample.PACKAGE_NAME_FREE);
		try {
			context.startActivity(i);
			return;
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_FREE + " is not installed.");
		}
		
		i.setPackage(PrintingSample.PACKAGE_NAME_PREMIUM);
		try {
			context.startActivity(i);
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_PREMIUM + " is not installed.");
		}
	}

	/***
	 * Tries to share web page.
	 */
	public void shareWebPage() {
		Intent i = new Intent(Intent.ACTION_VIEW);
		
		Uri uri = Uri.parse("http://printhand.com");
		i.setDataAndType(uri, "text/html");
		i.setPackage(PrintingSample.PACKAGE_NAME_FREE);
		
		try {
			context.startActivity(i);
			return;
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_FREE + " is not installed.");
		}
		
		i.setPackage(PrintingSample.PACKAGE_NAME_PREMIUM);
		try {
			context.startActivity(i);
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_PREMIUM + " is not installed.");
		}
	}
	
	/***
	 * Tries to share web page as string.
	 */
	public void shareWebPageString() {
		Intent i = new Intent(Intent.ACTION_SEND);
		
		String printString = loadHtmlString();
		i.setType("text/html");
		i.putExtra(Intent.EXTRA_TEXT, printString);
		
		i.setPackage(PrintingSample.PACKAGE_NAME_FREE);
		try {
			context.startActivity(i);
			return;
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_FREE + " is not installed.");
		}
		
		i.setPackage(PrintingSample.PACKAGE_NAME_PREMIUM);
		try {
			context.startActivity(i);
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, "Application with package name " + PrintingSample.PACKAGE_NAME_PREMIUM + " is not installed.");
		}
	}

	/***
	 * @return your html string
	 */
	private String loadHtmlString() {
		String html = "";
		return html;
	}
}
