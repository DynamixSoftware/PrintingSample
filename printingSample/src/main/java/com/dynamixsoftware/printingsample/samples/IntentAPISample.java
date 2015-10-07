package com.dynamixsoftware.printingsample.samples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.dynamixsoftware.intentapi.IDocument;
import com.dynamixsoftware.intentapi.IJob;
import com.dynamixsoftware.intentapi.IPrintCallback;
import com.dynamixsoftware.intentapi.IPrinterContext;
import com.dynamixsoftware.intentapi.IPrinterInfo;
import com.dynamixsoftware.intentapi.IServiceCallback;
import com.dynamixsoftware.intentapi.ISetLicenseCallback;
import com.dynamixsoftware.intentapi.IntentAPI;
import com.dynamixsoftware.intentapi.PrintHandOption;
import com.dynamixsoftware.intentapi.Result;

public class IntentAPISample {
	
	private static String TAG = "IntentAPISample";
	
	private IntentAPI intentAPI;
	private Context context;
	
	public boolean connected = false;
	
	/***
	 * Service must be started. You can use service methods after onServiceConnected event.
	 * @param activity this activity will be used to start PrintHand
	 */
	public IntentAPISample(Activity activity) {
		context = activity;
	}
	
	public void startService() {
		if (intentAPI == null || !intentAPI.isServiceRunning()) {
			intentAPI = new IntentAPI((Activity) context);
			try {
				intentAPI.runService(new IServiceCallback.Stub() {
					
					@Override
					public void onServiceDisconnected() {
						connected = false;
						Log.d(TAG, "Service disconnected");
					}
					
					@Override
					public void onServiceConnected() {
						connected = true;
						Log.d(TAG, "Service connected");
					}

					@Override
					public void onFileOpen(int arg0, int arg1) {
						Log.d(TAG, "onFileOpen progress " + arg0 + "; finished " + (arg1 == 1 ? true : false));
					}

					@Override
					public void onLibraryDownload(int arg0)
							throws RemoteException {
						Log.d(TAG, "onLibraryDownload progress " + arg0);
					}

					@Override
					public boolean onRenderLibraryCheck(boolean arg0, boolean arg1) throws RemoteException {
						Log.d(TAG, "onRenderLibraryCheck render library " + arg0 + "; fonts library " + arg1);
						return true;
					}

				});
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setCallback(IPrintCallback callback) throws RemoteException {
		if (intentAPI.isServiceRunning() && connected) {
			intentAPI.setCallback(callback);
		} else {
			serviceStopped();
		}
	}
	
	public void checkPremium() throws RemoteException {
		if (intentAPI.isServiceRunning() && connected) {
			Log.d(TAG, "checkPremium " + intentAPI.checkPremium());
		} else {
			serviceStopped();
		}
	}
	
	public void print(Uri uri, String contentType, String description) {
		if (intentAPI.isServiceRunning() && connected) {
			intentAPI.print(uri, contentType, description);
		} else {
			serviceStopped();
		}
	}
	
	public void setupCurrentPrinter() {
		if (intentAPI.isServiceRunning() && connected) {
			intentAPI.setupCurrentPrinter();
		} else {
			serviceStopped();
		}
	}
	
	public IPrinterInfo getCurrentPrinter() throws RemoteException {
		if (intentAPI.isServiceRunning() && connected) {
			return intentAPI.getCurrentPrinter();
		} else {
			serviceStopped();
			return null;
		}
	}
	
	public void changeOptions() {
		if (intentAPI.isServiceRunning() && connected) {
			intentAPI.changePrinterOptions();
		} else {
			serviceStopped();
		}
	}
	
	public void setLicense(String licenseID, ISetLicenseCallback licenseCallback) {
		if (intentAPI.isServiceRunning() && connected) {
			intentAPI.setLicense(licenseID, licenseCallback);
		} else {
			serviceStopped();
		}
	}
	
	private void serviceStopped() {
		Log.d(TAG, "Service is not running");
	}
	
	/***
	 * Prints test page with small image.
	 * @throws RemoteException
	 */
	public void printIJob() throws RemoteException {
		IJob.Stub job = new IJob.Stub() {

			@Override
			public Bitmap renderPageFragment(int num, Rect fragment)
					throws RemoteException {
				IPrinterInfo printer = getCurrentPrinter();
				if (printer != null) {
					Bitmap bitmap = Bitmap.createBitmap(fragment.width(), fragment.height(), Config.ARGB_8888);

					AssetManager am = context.getAssets();
					for (int i = 0; i < 3; i++)
						try {
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inPreferredConfig = Bitmap.Config.ARGB_8888;
							options.inDither = false;
							if (i > 0) {
								options.inSampleSize = 1 << i;
							}
							Bitmap imageBMP = BitmapFactory.decodeStream(am.open("test_page.png"), null, options);

							Paint p = new Paint();

							int imageWidth = 0;
							int imageHeight = 0;

							if (imageBMP != null) {
								imageWidth = imageBMP.getWidth();
								imageHeight = imageBMP.getHeight();
							}
							
							int xDpi = printer.getPrinterContext().getHResolution();
							int yDpi = printer.getPrinterContext().getVResolution();

							// in dots
							int paperWidth = printer.getPrinterContext().getPaperWidth() * xDpi / 72;
							int paperHeight = printer.getPrinterContext().getPaperHeight() * yDpi / 72;

							float aspectH = (float) imageHeight / (float) paperHeight;
							
							float aspectW = (float) imageWidth / (float) paperWidth;
							
							RectF dst = new RectF(0, 0, fragment.width() * aspectW, fragment.height() * aspectH);
							
							float sLeft = 0;
							float sTop = fragment.top * aspectH;
							float sRight = imageWidth;
							float sBottom = fragment.top * aspectH + fragment.bottom * aspectH;

							RectF source = new RectF(sLeft, sTop, sRight, sBottom);
							
							Canvas canvas = new Canvas(bitmap);
							canvas.drawColor(Color.WHITE);

							// move image to actual printing area
							dst.offsetTo(dst.left - fragment.left, dst.top - fragment.top);
							Matrix matrix = new Matrix();
							matrix.setRectToRect(source, dst, Matrix.ScaleToFit.FILL);
							canvas.drawBitmap(imageBMP, matrix, p);

							break;
						} catch (IOException ex) {
							ex.printStackTrace();
							break;
						} catch (OutOfMemoryError ex) {
							if (bitmap != null) {
								bitmap.recycle();
								bitmap = null;
							}
							continue;
						}

					return bitmap;
				} else {
					return null;
				}
			}

			@Override
			public int getTotalPages() throws RemoteException {
				return 1;
			}
		};

		if (intentAPI.isServiceRunning() && connected) {
			intentAPI.print(job, 1);
		} else {
			serviceStopped();
		}
	}

	public void printIDoc() throws RemoteException {
		IDocument.Stub document = new IDocument.Stub() {
			
			private int thumbnailWidth;
			private int thumbnailHeight;
			
			@Override
			public Bitmap renderPageFragment(int arg0, Rect fragment)
					throws RemoteException {
				IPrinterInfo printer = getCurrentPrinter();
				if (printer != null) {
					Bitmap bitmap = Bitmap.createBitmap(fragment.width(), fragment.height(), Config.ARGB_8888);

					AssetManager am = context.getAssets();
					for (int i = 0; i < 3; i++)
						try {
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inPreferredConfig = Bitmap.Config.ARGB_8888;
							options.inDither = false;
							if (i > 0) {
								options.inSampleSize = 1 << i;
							}
							Bitmap imageBMP = BitmapFactory.decodeStream(am.open("test_page.png"), null, options);

							Paint p = new Paint();

							int imageWidth = 0;
							int imageHeight = 0;

							if (imageBMP != null) {
								imageWidth = imageBMP.getWidth();
								imageHeight = imageBMP.getHeight();
							}
							
							int xDpi = printer.getPrinterContext().getHResolution();
							int yDpi = printer.getPrinterContext().getVResolution();

							// in dots
							int paperWidth = printer.getPrinterContext().getPaperWidth() * xDpi / 72;
							int paperHeight = printer.getPrinterContext().getPaperHeight() * yDpi / 72;

							float aspectH = (float) imageHeight / (float) paperHeight;
							float aspectW = (float) imageWidth / (float) paperWidth;
							
							aspectH = aspectH > 1 ? 1 / aspectH : aspectH;
							aspectW = aspectW > 1 ? 1 / aspectW : aspectW;
							
							RectF dst = new RectF(0, 0, fragment.width() * aspectW, fragment.height() * aspectH);
							
							float sLeft = 0;
							float sTop = fragment.top * aspectH;
							float sRight = imageWidth;
							float sBottom = fragment.top * aspectH + fragment.bottom * aspectH;

							RectF source = new RectF(sLeft, sTop, sRight, sBottom);
							
							Canvas canvas = new Canvas(bitmap);
							canvas.drawColor(Color.WHITE);

							// move image to actual printing area
							dst.offsetTo(dst.left - fragment.left, dst.top - fragment.top);
							Matrix matrix = new Matrix();
							matrix.setRectToRect(source, dst, Matrix.ScaleToFit.FILL);
							canvas.drawBitmap(imageBMP, matrix, p);

							break;
						} catch (IOException ex) {
							ex.printStackTrace();
							break;
						} catch (OutOfMemoryError ex) {
							if (bitmap != null) {
								bitmap.recycle();
								bitmap = null;
							}
							continue;
						}

					return bitmap;
				} else {
					return null;
				}
			}
			
			@Override
			public void initDeviceContext(IPrinterContext printerContext, int thumbnailWidth, int thumbnailHeight)
					throws RemoteException {
				Log.d(TAG, "initDeviceContext");
				this.thumbnailWidth = thumbnailWidth;
				this.thumbnailHeight = thumbnailHeight;
			}
			
			@Override
			public int getTotalPages() throws RemoteException {
				return 1;
			}
			
			@Override
			public String getDescription() throws RemoteException {
				return "PrintHand test page";
			}

			@Override
			public Bitmap getPageThumbnail(int arg0) throws RemoteException {
				Bitmap bitmap = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight, Config.ARGB_8888);

				AssetManager am = context.getAssets();
				for (int i = 0; i < 3; i++)
					try {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inPreferredConfig = Bitmap.Config.ARGB_8888;
						options.inDither = false;
						if (i > 0) {
							options.inSampleSize = 1 << i;
						}
						Bitmap imageBMP = BitmapFactory.decodeStream(am.open("test_page.png"), null, options);

						Paint p = new Paint();

						int imageWidth = 0;
						int imageHeight = 0;

						if (imageBMP != null) {
							imageWidth = imageBMP.getWidth();
							imageHeight = imageBMP.getHeight();
						}

						// default
						int xDpi = 300;
						int yDpi = 300;
						int paperWidth = 2481;
						int paperHeight = 3507;

						IPrinterInfo printer = getCurrentPrinter();
						if (printer != null) {
							xDpi = printer.getPrinterContext().getHResolution();
							yDpi = printer.getPrinterContext().getVResolution();

							// in dots
							paperWidth = printer.getPrinterContext().getPaperWidth() * xDpi / 72;
							paperHeight = printer.getPrinterContext().getPaperHeight() * yDpi / 72;
						}

						float aspectW = (float) imageWidth / (float) paperWidth;
						float aspectH = (float) imageHeight / (float) paperHeight;

						RectF dst = new RectF(0, 0, thumbnailWidth * aspectW, thumbnailHeight * aspectH);

						float sLeft = 0;
						float sTop = 0;
						float sRight = imageWidth;
						float sBottom = imageHeight;

						RectF source = new RectF(sLeft, sTop, sRight, sBottom);

						Canvas canvas = new Canvas(bitmap);
						canvas.drawColor(Color.WHITE);

						Matrix matrix = new Matrix();
						matrix.setRectToRect(source, dst, Matrix.ScaleToFit.FILL);
						canvas.drawBitmap(imageBMP, matrix, p);

						break;
					} catch (IOException ex) {
						ex.printStackTrace();
						break;
					} catch (OutOfMemoryError ex) {
						if (bitmap != null) {
							bitmap.recycle();
							bitmap = null;
						}
						continue;
					}

				return bitmap;
			}
		};
	
		if (intentAPI.isServiceRunning() && connected) {
			intentAPI.print(document);
		} else {
			serviceStopped();
		}
	}

	public void printWithoutUI(String filename) throws RemoteException, IOException {
		if (intentAPI.isServiceRunning() && connected) {
			AssetManager assetMgr = context.getAssets();
			InputStream in = assetMgr.open(filename);
			OutputStream out = null;

			File f = new File(context.getExternalFilesDir(null), filename);

			out = new FileOutputStream(f);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;

			intentAPI.print("PrintingSample", "image/png", Uri.fromFile(f));
		} else {
			serviceStopped();
		}
	}
	
	public void changeImageOptions() throws RemoteException {
		List<PrintHandOption> imagesOptions = intentAPI.getImagesOptions();
		for (PrintHandOption phOption : imagesOptions) {
			Log.d(TAG, "Current option " + phOption.getName() + " value is " + phOption.getValue());
			for (String phOptionValue : phOption.getValuesList()) {
				if (!phOption.getValue().equals(phOption.getValue())) {
					phOption.setValue(phOptionValue);
					break;
				}
			}
			Log.d(TAG, "Changed option " + phOption.getName() + " value is " + phOption.getValue());
		}
	}
	
}
