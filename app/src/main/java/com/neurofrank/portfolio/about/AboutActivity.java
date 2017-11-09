package com.neurofrank.portfolio.about;

import android.support.v4.app.Fragment;

import com.neurofrank.portfolio.SingleFragmentActivity;


public class AboutActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AboutFragment();
    }
}
