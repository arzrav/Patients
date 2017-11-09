package com.neurofrank.portfolio.about;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neurofrank.portfolio.R;


public class AboutFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        TextView aboutAppTitle = (TextView) v.findViewById(R.id.about_app_title_text_view);
        TextView aboutLinks = (TextView) v.findViewById(R.id.about_links_text_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            aboutAppTitle.setText(Html.fromHtml(getString(R.string.about_app_title_text_view), Html.FROM_HTML_MODE_LEGACY));
            Spanned spanned = Html.fromHtml(getString(R.string.about_links_text_view), Html.FROM_HTML_MODE_LEGACY);
            aboutLinks.setMovementMethod(LinkMovementMethod.getInstance());
            aboutLinks.setText(spanned);
        } else {
            aboutAppTitle.setText(Html.fromHtml(getString(R.string.about_app_title_text_view)));
            Spanned spanned = Html.fromHtml(getString(R.string.about_links_text_view));
            aboutLinks.setMovementMethod(LinkMovementMethod.getInstance());
            aboutLinks.setText(spanned);
        }
        return v;
    }
}
