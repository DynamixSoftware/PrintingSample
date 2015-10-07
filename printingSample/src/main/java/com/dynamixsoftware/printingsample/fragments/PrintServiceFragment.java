package com.dynamixsoftware.printingsample.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.dynamixsoftware.printingsample.R;
import com.dynamixsoftware.printingsample.samples.ServiceSample;

/***
 * Shows how to operate with service SDK.
 * All calls to SDK must be after service started.
 *
 */
public class PrintServiceFragment extends PlaceholderFragment {
	
	/***
	 * Set license id for the service.
	 */
	private static String licenseID = "";
	
	private static ServiceSample serviceSample;
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static PrintServiceFragment newInstance(Context context) {
		PrintServiceFragment fragment = new PrintServiceFragment(context);
		return fragment;
	}
	
	public PrintServiceFragment() {
	}
	
	public PrintServiceFragment(Context context) {
		if (serviceSample == null) {
			serviceSample = new ServiceSample(context);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_placeholder, container, false);
		
		/***
		 * Initialize buttons
		 */
		ViewGroup buttonsHolder = (ViewGroup) root.findViewById(R.id.btn_holder);
		buttonsHolder.removeAllViews();
		
		Context context = container.getContext();
		
		Button startService = new Button(context);
		startService.setText(R.string.button_printservice_startservice);
		startService.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (serviceSample == null) {
					serviceSample.startService(getActivity().getApplicationContext());
				} else {
					serviceSample = new ServiceSample(getActivity().getApplicationContext());
					serviceSample.startService(getActivity().getApplicationContext());
				}
			}
		});
		buttonsHolder.addView(startService);
		
		Button seLicense = new Button(context);
		seLicense.setText(R.string.button_printservice_setlicense);
		seLicense.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.setLicense(licenseID);
			}
		});
		buttonsHolder.addView(seLicense);
		
		Button initRecent = new Button(context);
		initRecent.setText(R.string.button_printservice_initrecent);
		initRecent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.initRecentPrinters();
			}
		});
		buttonsHolder.addView(initRecent);
		
		Button getCurrent = new Button(context);
		getCurrent.setText(R.string.button_printservice_getcurrent);
		getCurrent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.getCurrentPrinter();
			}
		});
		buttonsHolder.addView(getCurrent);
		
		Button getRecent = new Button(context);
		getRecent.setText(R.string.button_printservice_getrecent);
		getRecent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.getRecentPrintersList();
			}
		});
		buttonsHolder.addView(getRecent);
		
		Button discoverWiFi = new Button(context);
		discoverWiFi.setText(R.string.button_printservice_discoverwifi);
		discoverWiFi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.startDiscoverWiFi();
			}
		});
		buttonsHolder.addView(discoverWiFi);
		
		Button discoverBluetooth = new Button(context);
		discoverBluetooth.setText(R.string.button_printservice_discoverbluetooth);
		discoverBluetooth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.startDiscoverBluetooth();
			}
		});
		buttonsHolder.addView(discoverBluetooth);
		
		Button discoverCloud = new Button(context);
		discoverCloud.setText(R.string.button_printservice_discovercloud);
		discoverCloud.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.startDiscoverCloud();
			}
		});
		buttonsHolder.addView(discoverCloud);
		
		Button discoverSmb = new Button(context);
		discoverSmb.setText(R.string.button_printservice_discoversmd);
		discoverSmb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.startDiscoverSMB();
			}
		});
		buttonsHolder.addView(discoverSmb);
		
		Button discoverUSB = new Button(context);
		discoverUSB.setText(R.string.button_printservice_discoverusb);
		discoverUSB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.startDiscoverUSB();
			}
		});
		buttonsHolder.addView(discoverUSB);
		
		Button findDriver = new Button(context);
		findDriver.setText(R.string.button_printservice_finddriver);
		findDriver.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.findDriver();
			}
		});
		buttonsHolder.addView(findDriver);
		
		Button getDriversList = new Button(context);
		getDriversList.setText(R.string.button_printservice_getdriverslist);
		getDriversList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.getDriversList();
			}
		});
		buttonsHolder.addView(getDriversList);
		
		Button setupRecent = new Button(context);
		setupRecent.setText(R.string.button_printservice_setuprecent);
		setupRecent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.setupRecent();
			}
		});
		buttonsHolder.addView(setupRecent);
		
		Button setup = new Button(context);
		setup.setText(R.string.button_printservice_setup);
		setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.setup();
			}
		});
		buttonsHolder.addView(setup);
		
		Button changeOption = new Button(context);
		changeOption.setText(R.string.button_printservice_changeoption);
		changeOption.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				serviceSample.changeOption();
			}
		});
		buttonsHolder.addView(changeOption);
		
		Button print = new Button(context);
		print.setText(R.string.button_printservice_print);
		print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					serviceSample.print();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		buttonsHolder.addView(print);
		
		return root;
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
