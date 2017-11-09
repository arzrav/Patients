package com.neurofrank.portfolio.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.neurofrank.portfolio.R;

import java.util.List;

import static com.neurofrank.portfolio.util.Constants.EXTRA_IMAGE;


public class PhotoGalleryPagerActivity extends AppCompatActivity {

    private static List<DocumentFile> mImages;

    public static Intent newIntent(Context packageContext, DocumentFile currentImage, List<DocumentFile> images) {
        Intent intent = new Intent(packageContext, PhotoGalleryPagerActivity.class);
        intent.putExtra(EXTRA_IMAGE, currentImage.getUri().toString());
        mImages = images;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        final String currentImage = (String) getIntent().getSerializableExtra(EXTRA_IMAGE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_view_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                DocumentFile currentImage = mImages.get(position);
                return PhotoGalleryFragment.newInstance(currentImage.getUri());
            }

            @Override
            public int getCount() {
                return mImages.size();
            }
        });

        for (int i = 0; i < mImages.size(); i++) {
            if (mImages.get(i).getUri().toString().equals(currentImage)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
