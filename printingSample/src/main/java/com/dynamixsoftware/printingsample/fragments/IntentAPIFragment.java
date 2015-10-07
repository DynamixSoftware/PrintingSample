package com.dynamixsoftware.printingsample.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.dynamixsoftware.intentapi.IPrintCallback;
import com.dynamixsoftware.intentapi.IPrinterInfo;
import com.dynamixsoftware.intentapi.ISetLicenseCallback;
import com.dynamixsoftware.intentapi.Result;
import com.dynamixsoftware.printingsample.PrintingSample;
import com.dynamixsoftware.printingsample.R;
import com.dynamixsoftware.printingsample.samples.IntentAPISample;

public class IntentAPIFragment extends PlaceholderFragment {
	
	private static String TAG = "IntentAPI";
	
	private IntentAPISample intentAPISample;
	protected IPrintCallback callback = new IPrintCallback.Stub() {
		
		@Override
		public void startingPrintJob() throws RemoteException {
			Log.d(TAG, "startingPrintJob");
		}
		
		@Override
		public void start() throws RemoteException {
			Log.d(TAG, "start");
		}
		
		@Override
		public void sendingPage(int arg0, int arg1) throws RemoteException {
			Log.d(TAG, "sendingPage number " + arg0 + ", progress " + arg1);
		}
		
		@Override
		public void preparePage(int arg0) throws RemoteException {
			Log.d(TAG, "preparePage number " +  arg0);	
		}
		
		@Override
		public boolean needCancel() throws RemoteException {
			Log.d(TAG, "needCancel");
			// If you need to cancel printing send true
			return false;
		}
		
		@Override
		public void finishingPrintJob() throws RemoteException {
			Log.d(TAG, "needCancel");
		}
		
		@Override
		public void finish(Result arg0, int arg1) throws RemoteException {
			Log.d(TAG, "finish, Result " + arg0
					+ "; Result type " + arg0.getType()
					+ "; Result message " + arg0.getType().getMessage()
					+ "; pages printed " + arg1);
		}
	};
	
	/**
	 * Returns a new instance of this fragment.
	 */
	public static IntentAPIFragment newInstance() {
		IntentAPIFragment fragment = new IntentAPIFragment();
		return fragment;
	}
	
	public IntentAPIFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Service should be started firstly
		if (intentAPISample == null) {
			intentAPISample = new IntentAPISample(getActivity());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_placeholder, container, false);
		
		ViewGroup buttonsHolder = (ViewGroup) root.findViewById(R.id.btn_holder);
		buttonsHolder.removeAllViews();
		
		Context context = container.getContext();
		
		Button startService = new Button(context);
		startService.setText(R.string.button_intentapi_startservice);
		startService.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intentAPISample.startService();
			}
		});
		buttonsHolder.addView(startService);
		
		Button checkPremium = new Button(context);
		checkPremium.setText("Check Premium");
		checkPremium.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					intentAPISample.checkPremium();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		buttonsHolder.addView(checkPremium);
		
		Button activateOnline = new Button(context);
		activateOnline.setText("Activate online");
		activateOnline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// set activation key here
				intentAPISample.setLicense("", new ISetLicenseCallback.Stub() {
					
					@Override
					public void start() throws RemoteException {
						Log.d(TAG, "activateOnline start");
					}
					
					@Override
					public void serverCheck() throws RemoteException {
						Log.d(TAG, "activateOnline serverCheck");
					}
					
					@Override
					public void finish(Result arg0) throws RemoteException {
						Log.d(TAG, "activateOnline finish " + arg0);
					}
					
				});;
			}
		});
		buttonsHolder.addView(activateOnline);
		
		Button setCallback = new Button(context);
		setCallback.setText(R.string.button_intentapi_setcallback);
		setCallback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					intentAPISample.setCallback(callback );
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		buttonsHolder.addView(setCallback);
		
		Button setupPrinter = new Button(context);
		setupPrinter.setText(R.string.button_intentapi_setup);
		setupPrinter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intentAPISample.setupCurrentPrinter();
			}
		});
		buttonsHolder.addView(setupPrinter);
		
		Button changeOptions = new Button(context);
		changeOptions.setText(R.string.button_intentapi_changeoptions);
		changeOptions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intentAPISample.changeOptions();
			}
		});
		buttonsHolder.addView(changeOptions);
		
		Button getCurrentPrinter = new Button(context);
		getCurrentPrinter.setText(R.string.button_intentapi_getcurrentprinter);
		getCurrentPrinter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					IPrinterInfo printer = intentAPISample.getCurrentPrinter();
					if (printer == null) {
						Log.d(TAG, "Current printer null");
					} else {
						Log.d(TAG, "Current printer name " + printer.getName());
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		buttonsHolder.addView(getCurrentPrinter);
		
		Button printImage = new Button(context);
		printImage.setText(R.string.button_intentapi_printimage);
		printImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					PrintingSample.saveTestImage(getActivity());
				} catch (Exception e) {
					Log.d(TAG, "Failed to save test image");
					e.printStackTrace();
					return;
				}
				Uri uri = Uri.parse("file://" + getActivity().getExternalCacheDir().getAbsolutePath() + "/" + PrintingSample.TEST_PAGE_NAME);
				intentAPISample.print(uri, "image/png", "Test image print");
			}
		});
		buttonsHolder.addView(printImage);
		
		Button printFile = new Button(context);
		printFile.setText(R.string.button_intentapi_printdoc);
		printFile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					PrintingSample.saveTestFile(getActivity());
				} catch (Exception e) {
					Log.d(TAG, "Failed to save test image");
					e.printStackTrace();
					return;
				}
				Uri uri = Uri.parse("file://" + getActivity().getExternalCacheDir().getAbsolutePath() + "/" + PrintingSample.TEST_FILE_NAME);
				intentAPISample.print(uri, "application/msword", "Test file print");
			}
		});
		buttonsHolder.addView(printFile);
		
		Button printDocument = new Button(context);
		printDocument.setText(R.string.button_intentapi_printidoc);
		printDocument.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					intentAPISample.printIDoc();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		buttonsHolder.addView(printDocument);
		
		Button printIJob = new Button(context);
		printIJob.setText(R.string.button_intentapi_printijob);
		printIJob.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					intentAPISample.printIJob();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		buttonsHolder.addView(printIJob);
		
		Button printImageWithoutPHUI = new Button(context);
		printImageWithoutPHUI.setText(R.string.button_intentapi_print_image_PH);
		printImageWithoutPHUI.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					intentAPISample.printWithoutUI("test_page.png");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		buttonsHolder.addView(printImageWithoutPHUI);
		
		Button changeImageOptions = new Button(context);
		changeImageOptions.setText(R.string.button_intentapi_change_image_options);
		changeImageOptions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					intentAPISample.changeImageOptions();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		buttonsHolder.addView(changeImageOptions);
		
		return root;
	}


}
