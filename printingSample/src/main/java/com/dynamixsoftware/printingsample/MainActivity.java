package com.dynamixsoftware.printingsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FilesUtils.extractFilesFromAssets(this);

        this.<ViewPager>findViewById(R.id.pager).setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new ShareIntentFragment();
                    case 1:
                        return new IntentApiFragment();
                    case 2:
                        return new PrintServiceFragment();
                }
                return null;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.share_intent);
                    case 1:
                        return getString(R.string.intent_api);
                    case 2:
                        return getString(R.string.printing_sdk);
                }
                return null;
            }
        });
    }

}
