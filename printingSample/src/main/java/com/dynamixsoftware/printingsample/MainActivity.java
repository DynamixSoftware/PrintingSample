package com.dynamixsoftware.printingsample;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

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

            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    default:
                    case 0:
                        return new ShareIntentFragment();
                    case 1:
                        return new IntentApiFragment();
                    case 2:
                        return new PrintServiceFragment();
                }
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    default:
                    case 0:
                        return getString(R.string.share_intent);
                    case 1:
                        return getString(R.string.intent_api);
                    case 2:
                        return getString(R.string.printing_sdk);
                }
            }
        });
    }

}
