package com.dynamixsoftware.printingsample.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dynamixsoftware.printingsample.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
	
	protected Activity activity;
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static PlaceholderFragment newInstance() {
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public PlaceholderFragment() {
	}
	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		activity = getActivity();
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
//		TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//		textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
		return rootView;
	}
}