package com.dynamixsoftware.printingsample;

import java.util.Locale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dynamixsoftware.printingsample.fragments.IntentAPIFragment;
import com.dynamixsoftware.printingsample.fragments.PlaceholderFragment;
import com.dynamixsoftware.printingsample.fragments.PrintServiceFragment;
import com.dynamixsoftware.printingsample.fragments.ShareIntentFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	private final MainActivity mainActivity;

	public SectionsPagerAdapter(MainActivity mainActivity, FragmentManager fm) {
		super(fm);
		this.mainActivity = mainActivity;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return ShareIntentFragment.newInstance();
		case 1:
			return IntentAPIFragment.newInstance();
		case 2:
			return PrintServiceFragment.newInstance(mainActivity.getApplicationContext());
		}
		return PlaceholderFragment.newInstance();
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return this.mainActivity.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return this.mainActivity.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return this.mainActivity.getString(R.string.title_section3).toUpperCase(l);
		}
		return null;
	}
}