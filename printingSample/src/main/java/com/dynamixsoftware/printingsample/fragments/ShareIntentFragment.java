package com.dynamixsoftware.printingsample.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.dynamixsoftware.printingsample.R;
import com.dynamixsoftware.printingsample.samples.ShareIntentSample;

public class ShareIntentFragment extends PlaceholderFragment {
	
	private ShareIntentSample shareIntent;
	private String activationKey = "";
	
	/**
	 * Returns a new instance of this fragment.
	 */
	public static ShareIntentFragment newInstance() {
		ShareIntentFragment fragment = new ShareIntentFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		shareIntent = new ShareIntentSample(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_placeholder, container, false);
		
		ViewGroup buttonsHolder = (ViewGroup) root.findViewById(R.id.btn_holder);
		buttonsHolder.removeAllViews();
		
		Context context = container.getContext();
		Button shareImageView = new Button(context);
		shareImageView.setText(R.string.button_share_image_view);
		shareImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareIntent.shareImage(true);
			}
		});
		buttonsHolder.addView(shareImageView);
		
		Button shareImageSend = new Button(context);
		shareImageSend.setText(R.string.button_share_image_send);
		shareImageSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareIntent.shareImage(false);
			}
		});
		buttonsHolder.addView(shareImageSend);
		
		Button shareMultipleImages = new Button(context);
		shareMultipleImages.setText(R.string.button_share_multiple_images);
		shareMultipleImages.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareIntent.shareMiltipleImages();
			}
		});
		buttonsHolder.addView(shareMultipleImages);
		
		Button shareImageWithReturn = new Button(context);
		shareImageWithReturn.setText(R.string.button_share_image_return);
		shareImageWithReturn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareIntent.shareImageReturn(true, 1234);
			}
		});
		buttonsHolder.addView(shareImageWithReturn);
		
		
		Button shareFileView = new Button(context);
		shareFileView.setText(R.string.button_share_file);
		shareFileView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareIntent.shareFile(true);
			}
		});
		buttonsHolder.addView(shareFileView);
		
		Button shareWebPage = new Button(context);
		shareWebPage.setText(R.string.button_share_webpage);
		shareWebPage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareIntent.shareWebPage();
			}
		});
		buttonsHolder.addView(shareWebPage);
		
		Button shareLocalWebPage = new Button(context);
		shareLocalWebPage.setText(R.string.button_share_webpage_string);
		shareLocalWebPage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareIntent.shareWebPageString();
			}
		});
		buttonsHolder.addView(shareLocalWebPage);

		// if true error dialog will be show to user
		final boolean showErrorMessage = false;

		Button activateLicense = new Button(context);
		activateLicense.setText(R.string.button_activate_license);
		activateLicense.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareIntent.setLicense(activationKey, showErrorMessage);
			}
		});
		buttonsHolder.addView(activateLicense);

		Button activateLicenseReturn = new Button(context);
		activateLicenseReturn.setText(R.string.button_activate_license_return);
		activateLicenseReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareIntent.setLicenseReturn(activationKey, getActivity(), showErrorMessage);
			}
		});
		buttonsHolder.addView(activateLicenseReturn);
		
		return root;
	}

}
