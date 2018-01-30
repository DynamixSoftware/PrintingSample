package com.dynamixsoftware.printingsample.samples;

import android.content.Context;
import android.content.Intent;
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
import android.os.RemoteException;
import android.util.Log;

import com.dynamixsoftware.printingsdk.DriverHandleEntry;
import com.dynamixsoftware.printingsdk.DriversSearchEntry;
import com.dynamixsoftware.printingsdk.IDiscoverCloudListener;
import com.dynamixsoftware.printingsdk.IDiscoverListener;
import com.dynamixsoftware.printingsdk.IDiscoverSmb;
import com.dynamixsoftware.printingsdk.IDiscoverSmbListener;
import com.dynamixsoftware.printingsdk.IFindDriversListener;
import com.dynamixsoftware.printingsdk.IGetDriversListener;
import com.dynamixsoftware.printingsdk.IPage;
import com.dynamixsoftware.printingsdk.IPrintListener;
import com.dynamixsoftware.printingsdk.IServiceCallback;
import com.dynamixsoftware.printingsdk.ISetLicenseCallback;
import com.dynamixsoftware.printingsdk.ISetupPrinterListener;
import com.dynamixsoftware.printingsdk.Printer;
import com.dynamixsoftware.printingsdk.PrinterOption;
import com.dynamixsoftware.printingsdk.PrinterOptionValue;
import com.dynamixsoftware.printingsdk.PrintingSdk;
import com.dynamixsoftware.printingsdk.Result;
import com.dynamixsoftware.printingsdk.ResultType;
import com.dynamixsoftware.printingsdk.SmbFile;
import com.dynamixsoftware.printingsdk.TransportType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceSample {
	
	private static String TAG = "ServiceSample";
	
	private static PrintingSdk printingSdk;
	
	private static boolean onServiceDisconnected = true;
	
	private List<Printer> listPrinters;
	
	/***
	 * Set Google Account name. This is used for Cloudprint discover
	 */
	private static String GOOGLE_ACCOUNT_NAME = "";
	
	/***
	 * This field is used for SMB operations control like navigation and authorization.
	 */
	protected IDiscoverSmb discoverSmbControl;

	protected Printer currentPrinter;
	protected Printer printer;

	protected List<DriverHandleEntry> listDriveHandleEntry;
	protected List<DriversSearchEntry> listDriversSearchEntry;

	private Context context;
	
	protected static IServiceCallback serviceCallback = new IServiceCallback() {
		
		@Override
		public void onServiceDisconnected() {
			onServiceDisconnected = true;
			Log.d(TAG, "Service disconnected");
		}
		
		@Override
		public void onServiceConnected() {
			onServiceDisconnected = false;
			Log.d(TAG, "Service connected");
		}
	};

	protected IDiscoverListener.Stub discoverListener = new IDiscoverListener.Stub() {
		
		@Override
		public void start() throws RemoteException {
			Log.d(TAG, "IDiscoverListener start");
		}
		
		@Override
		public void printerFound(List<Printer> arg0) throws RemoteException {
			Log.d(TAG, "IDiscoverListener printerFound");
			listPrinters = arg0;
		}
		
		@Override
		public void finish(Result arg0) throws RemoteException {
			Log.d(TAG, "IDiscoverListener finish " + arg0.name());
		}
	};

	protected IDiscoverCloudListener.Stub discoverCloudListener = new IDiscoverCloudListener.Stub() {
		
		@Override
		public void start() throws RemoteException {
			Log.d(TAG, "IDiscoverCloudListener start");
		}
		
		@Override
		public void showAuthorization(Intent arg0) throws RemoteException {
			// Launch Intent arg0 to show authorization activity			
		}
		
		@Override
		public void printerFound(List<Printer> arg0) throws RemoteException {
			Log.d(TAG, "IDiscoverCloudListener printerFound");
			listPrinters = arg0;
		}
		
		@Override
		public void finish(Result arg0) throws RemoteException {
			Log.d(TAG, "IDiscoverCloudListener finish " + arg0.name());
		}
	};

	protected IDiscoverSmbListener.Stub discoverSmbListener = new IDiscoverSmbListener.Stub() {
		
		@Override
		public void start() throws RemoteException {
			Log.d(TAG, "IDiscoverSmbListener start");
		}
		
		@Override
		public void smbFilesFound(List<SmbFile> arg0) throws RemoteException {
			// Show list of SMB files. This listener is used for navigation. 
			// You should call discoverSmbControl.move(arg0) to change location. 
		}
		
		@Override
		public void showAuthorization() throws RemoteException {
			// You have to ask user for authorization credentials and call discoverSmbControl.login(arg0, arg1);
		}
		
		@Override
		public void printerFound(List<Printer> arg0) throws RemoteException {
			Log.d(TAG, "IDiscoverSmbListener printerFound");
			listPrinters = arg0;
		}
		
		@Override
		public void finish(Result arg0) throws RemoteException {
			Log.d(TAG, "IDiscoverSmbListener finish " + arg0.name());
		}
	};

	protected IGetDriversListener.Stub getDriversListener = new IGetDriversListener.Stub() {
		
		@Override
		public void start() throws RemoteException {
			Log.d(TAG, "IGetDriversListener start");
		}
		
		@Override
		public void finish(List<DriverHandleEntry> arg0) throws RemoteException {
			Log.d(TAG, "IGetDriversListener finish");
			listDriveHandleEntry = arg0;
		}
	};

	private IFindDriversListener.Stub findDriversListener = new IFindDriversListener.Stub() {
		
		@Override
		public void start() throws RemoteException {
			Log.d(TAG, "IFindDriversListener start");
		}
		
		@Override
		public void finish(List<DriversSearchEntry> arg0) throws RemoteException {
			Log.d(TAG, "IFindDriversListener finish; Found "
					+ arg0.size() + " drivers entries;" +
					((arg0.size() == 0) ? "" : ""));
			listDriversSearchEntry = arg0;
		}
	};

	protected ISetupPrinterListener.Stub setupListener = new ISetupPrinterListener.Stub() {

	@Override
		public void start() throws RemoteException {
			Log.d(TAG, "ISetupPrinterListener start");
		}

		@Override
		public void libraryPackInstallationProcess(int arg0) throws RemoteException {
			Log.d(TAG, "ISetupPrinterListener libraryPackInstallationProcess " + arg0 + " %");
		}

		@Override
		public void finish(Result arg0) throws RemoteException {
			Log.d(TAG, "ISetupPrinterListener finish " + arg0.name());
			if (arg0.getType().equals(ResultType.ERROR_LIBRARY_PACK_NOT_INSTALLED)) {
				// printingSdk.setup should be called with forceInstall = true
				// to download required drivers
			}
		}
	};

	private IPrintListener.Stub printListener = new IPrintListener.Stub() {
		
		@Override
		public void startingPrintJob() throws RemoteException {
			Log.d(TAG, "IPrintListener startingPrintJob");
		}
		
		@Override
		public void start() throws RemoteException {
			Log.d(TAG, "IPrintListener start");
		}
		
		@Override
		public void sendingPage(int arg0, int arg1) throws RemoteException {
			Log.d(TAG, "IPrintListener sendingPage " + arg0 + "; progress " + arg1 + "%");
		}
		
		@Override
		public void preparePage(int arg0) throws RemoteException {
			Log.d(TAG, "IPrintListener preparePage " + arg0);
		}
		
		@Override
		public boolean needCancel() throws RemoteException {
			Log.d(TAG, "IPrintListener needCancel");
			// Return false if cancel needed.
			return false;
		}
		
		@Override
		public void finishingPrintJob() throws RemoteException {
			Log.d(TAG, "IPrintListener finishingPrintJob");
			
		}
		
		@Override
		public void finish(Result arg0, int arg1, int arg2) throws RemoteException {
			Log.d(TAG, "IPrintListener finish Result " + arg0 +
					"; Result type " + arg0.getType() +
					"; Total pages " + arg1 + 
					"; Pages sent " + arg2);
		}
	};
	
	public ServiceSample(Context context) {
		this.context = context;
	}
	
	public void startService(Context context) {
		if (printingSdk == null) {
			printingSdk = new PrintingSdk(context);
			printingSdk.startService(serviceCallback);
		} else {
			if (onServiceDisconnected) {
				printingSdk.startService(serviceCallback);
			} else {
				Log.d(TAG, "Service already started");
			}
		}
	}

   private boolean checkService() {
      if (printingSdk != null || onServiceDisconnected) {
         return true;
      } else {
         return false;
      }
   }


	public void setLicense(String license) {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			printingSdk.setLicense(license, new ISetLicenseCallback.Stub() {
				
				@Override
				public void start() throws RemoteException {
					Log.d(TAG, "setLicense start");
				}
				
				@Override
				public void finish(Result arg0) throws RemoteException {
					Log.d(TAG, "setLicense finish Result " + arg0
							+ "; ResultType " + arg0.getType()
							+ "; message " + arg0.getType().getMessage());
				}

				@Override
				public void serverCheck() throws RemoteException {
					Log.d(TAG, "setLicense serverCheck");
				}
			});
		}
	}
	
	/***
	 * Initializes current and recent printers.
	 * Use this method to initialize current and recent printers after application restart.  
	 */
	public void initRecentPrinters() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				printingSdk.initRecentPrinters(setupListener);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * Currently installed printer. This printer will be used for printing.
	 */
	public void getCurrentPrinter() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				Printer currentPrinter = printingSdk.getCurrentPrinter();
				if (currentPrinter != null) {
					Log.d(TAG, "Current printer name: " + currentPrinter.getName());
				} else {
					Log.d(TAG, "Current printer is null");
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * Recent printers. These printers where installed earlier.
	 */
	public void getRecentPrintersList() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				List<Printer> recentPrintersList = printingSdk.getRecentPrintersList();
				if (recentPrintersList.isEmpty()) {
					Log.d(TAG, "Recent printer list is empty");
				}
				
				for (Printer printer : recentPrintersList) {
					Log.d(TAG, "Recent printer name: " + printer.getName());
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startDiscoverWiFi() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				printingSdk.startDiscoverWiFi(discoverListener);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startDiscoverBluetooth() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				printingSdk.startDiscoverBluetooth(discoverListener);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startDiscoverCloud() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				printingSdk.startDiscoverCloud(GOOGLE_ACCOUNT_NAME , discoverCloudListener);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startDiscoverSMB() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				discoverSmbControl = printingSdk.startDiscoverSmb(discoverSmbListener );
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startDiscoverUSB() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				printingSdk.startDiscoverUSB(discoverListener);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * Try to find appropriate drivers for the first found printer.
	 * This method must be run after startDiscoverWiFi, startDiscoverBluetooth,
	 * startDiscoverCloud, startDiscoverSMB or startDiscoverUSB.
	 */
	public void findDriver() {
		int printerIndex = 0;
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			if (listPrinters != null && listPrinters.get(printerIndex) != null) {
				currentPrinter = listPrinters.get(printerIndex);
				try {
					printingSdk.findDrivers(currentPrinter, findDriversListener);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else {
				Log.d(TAG, "Printers must be discoverd first");
			}
		}
	}
	
	/***
	 * If findDriver() didn't find any appropriate driver 
	 * you could try to find all drivers for given printer and protocol wrapper.
	 */
	public void getDriversList() {
		int printerIndex = 0;
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			if (listPrinters != null && listPrinters.get(printerIndex) != null) {
				currentPrinter = listPrinters.get(printerIndex);
				TransportType transportType = currentPrinter.getTransportTypes().get(0);
				if (transportType != null) {
					try {
						printingSdk.getDriversList(currentPrinter, transportType, getDriversListener);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			} else {
				Log.d(TAG, "Printers must be discoverd first");
			}
		}
	}
	
	/***
	 * Use this method to setup printer from recent printers list.
	 * This method preferred for recent printers, because it keeps
	 * its settings and setup faster.
	 */
	public void setupRecent() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				List<Printer> printerList = printingSdk.getRecentPrintersList();
				if (printerList.isEmpty()) {
					Log.d(TAG, "Only recent printer can be resetup this way.");
				} else {
					printer = printerList.get(0);
					printingSdk.setup(printer, true, setupListener);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * Setup discovered printer.
	 */
	public void setup() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			if (listPrinters == null || listPrinters.isEmpty()) {
				Log.d(TAG, "Discover printers firstly.");
			} else {
				// Lets try to setup the first one.
				printer = listPrinters.get(0);
				Log.d(TAG, "Try to setup printer " + printer.getName());
				if (listDriversSearchEntry == null) {
					Log.d(TAG, "Find drivers for the printer first");
				} else {
					try {
						printingSdk.setup(printer, 
								// Select driver wrapper. The first one is the most appropriate.
								listDriversSearchEntry.get(0).getDriverHandlesList().get(0),
								// Select protocol wrapper. The first one is the most appropriate
								listDriversSearchEntry.get(0).getTransportType(),
								false, setupListener);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	public void changeOption() {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			try {
				Printer currentPrinter = printingSdk.getCurrentPrinter();
				if (currentPrinter != null) {
					List<PrinterOption> optionsList = currentPrinter.getOptions();
					PrinterOption printerOption = optionsList.get(0);
					Log.d(TAG, "Option " + printerOption.getName() 
							+ ", current value " + printerOption.getOptionValue().getName());
					List<PrinterOptionValue> optionValues = printerOption.getOptionValueList();
					for (PrinterOptionValue printerOptionValue : optionValues) {
						// Change option 
						if (!printerOptionValue.getId().equals(printerOption.getOptionValue().getId())) {
							printingSdk.setCurrentPrinterOptionValue(printerOption, printerOptionValue);
							break;
						}
					}
					Log.d(TAG, "After change option " + printerOption.getName() 
							+ ", current value " + printerOption.getOptionValue().getName());
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * Prints test page
	 * @throws RemoteException
	 */
	public void print() throws RemoteException {
		if (printingSdk == null || onServiceDisconnected) {
			Log.d(TAG, "Start service first");
		} else {
			if (printingSdk.getCurrentPrinter() != null) {
				List<IPage> pages = new ArrayList<IPage>();
				pages.add(new IPage() {
					
					@Override
					public Bitmap getBitmapFragment(Rect fragment) {
						Printer printer = null;
						try {
							printer = printingSdk.getCurrentPrinter();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
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
									
									int xDpi = printer.getContext().getHResolution();
									int yDpi = printer.getContext().getVResolution();

									// in dots
									int paperWidth = printer.getContext().getPaperWidth() * xDpi / 72;
									int paperHeight = printer.getContext().getPaperHeight() * yDpi / 72;

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
				});
				try {
					printingSdk.print(pages , 1, printListener );
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else {
				Log.d(TAG, "Setup printer first.");
			}
		}
	}
}
